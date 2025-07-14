package com.hostelhive.hostelhive.Service;

import com.hostelhive.hostelhive.models.Payment;
import com.hostelhive.hostelhive.models.Transaction;
import com.hostelhive.hostelhive.repository.PaymentRepo;
import com.hostelhive.hostelhive.repository.TransactionRepo;
import com.hostelhive.hostelhive.exceptions.ResourceNotFoundException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class PaymentService {
    private final PaymentRepo paymentRepo;
    private final TransactionRepo transactionRepo;
    private final MpesaService mpesaService;

    @Autowired
    public PaymentService(PaymentRepo paymentRepo, TransactionRepo transactionRepo, MpesaService mpesaService) {
        this.paymentRepo = paymentRepo;
        this.transactionRepo = transactionRepo;
        this.mpesaService = mpesaService;
    }

    public MpesaService getMpesaService() {
        return mpesaService;
    }

    public Payment createPayment(Payment payment) {
        if (payment.getBookingId() == null || payment.getAmount() == null) {
            throw new IllegalArgumentException("Booking ID and amount are required");
        }
        return paymentRepo.save(payment);
    }

    @Transactional(readOnly = true)
    public Payment getPaymentById(Long paymentId) {
        return paymentRepo.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + paymentId));
    }

    @Transactional(readOnly = true)
    public List<Payment> getAllPayments() {
        return paymentRepo.findAll();
    }

    public Payment updatePaymentStatus(Long paymentId, String transactionId, String status) {
        Payment payment = paymentRepo.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + paymentId));

        payment.setTransactionId(transactionId);
        payment.setStatus(status);
        Payment updatedPayment = paymentRepo.save(payment);

        // Record transaction
        Transaction transaction = new Transaction(paymentId, transactionId, status, payment.getAmount());
        transactionRepo.save(transaction);

        return updatedPayment;
    }

    public String initiateStudentPayment(Long bookingId, String phoneNumber, double amount) {
        try {
            System.out.println("Initiating payment - bookingId: " + bookingId + ", phoneNumber: " + phoneNumber + ", amount: " + amount);
            Payment payment = new Payment(bookingId, amount);
            System.out.println("Payment object created: " + payment);
            payment = paymentRepo.save(payment);
            System.out.println("Payment saved with id: " + payment.getId());
            String response = mpesaService.initiateStkPush(phoneNumber, amount, "Booking_" + payment.getId(), "Hostel Booking Payment");
            System.out.println("STK Push response: " + response);
            
            // Parse response to get CheckoutRequestID
            JSONObject jsonResponse = new JSONObject(response);
            String checkoutRequestId = jsonResponse.getString("CheckoutRequestID");
            
            // Store CheckoutRequestID in payment
            payment.setTransactionId(checkoutRequestId);
            paymentRepo.save(payment);
            
            return response;
        } catch (Exception e) {
            System.err.println("Error in initiateStudentPayment: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public String disburseToManager(Long paymentId, String phoneNumber, double amount) {
        Payment payment = paymentRepo.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + paymentId));
        payment.setStatus("DISBURSED");
        paymentRepo.save(payment);
        return mpesaService.initiateB2CDisbursement(phoneNumber, amount, "Manager Disbursement for Booking " + payment.getBookingId());
    }

    @Transactional(readOnly = true)
    public List<Payment> getPaymentsByBookingId(Long bookingId) {
        return paymentRepo.findByBookingId(bookingId);
    }

    @Transactional(readOnly = true)
    public List<Payment> getPaymentsByStatus(String status) {
        return paymentRepo.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public Payment getPaymentByTransactionId(String transactionId) {
        return paymentRepo.findByTransactionId(transactionId);
    }

    public String queryStkPush(String businessShortCode, String password, String timestamp, String checkoutRequestId) {
        String queryUrl = "https://sandbox.safaricom.co.ke/mpesa/stkpushquery/v1/query";
        String accessToken = "Bearer " + mpesaService.getAccessToken();

        String requestPayload = "{"
                + "\"BusinessShortCode\":\"" + businessShortCode + "\","
                + "\"Password\":\"" + password + "\","
                + "\"Timestamp\":\"" + timestamp + "\","
                + "\"CheckoutRequestID\":\"" + checkoutRequestId + "\""
                + "}";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", accessToken);

        HttpEntity<String> requestEntity = new HttpEntity<>(requestPayload, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                queryUrl,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            JSONObject jsonResponse = new JSONObject(response.getBody());
            String resultCode = jsonResponse.getString("ResultCode");
            String transactionId = jsonResponse.has("MpesaReceiptNumber") ? jsonResponse.getString("MpesaReceiptNumber") : checkoutRequestId;

            // Find payment by CheckoutRequestID
            Payment payment = paymentRepo.findByTransactionId(checkoutRequestId);
            if (payment != null && "0".equals(resultCode)) {
                updatePaymentStatus(payment.getId(), transactionId, "ACTIVE");
            } else if (payment != null && !"0".equals(resultCode)) {
                updatePaymentStatus(payment.getId(), transactionId, "FAILED");
            }

            return response.getBody();
        } else {
            throw new RuntimeException("STK Push query failed: " + response.getBody());
        }
    }

    public void handleCallback(String checkoutRequestId, String transactionId, String status) {
        Payment payment = paymentRepo.findByTransactionId(checkoutRequestId);
        if (payment != null) {
            updatePaymentStatus(payment.getId(), transactionId, status);
        } else {
            System.err.println("No payment found for CheckoutRequestID: " + checkoutRequestId);
        }
    }

    @Scheduled(fixedRate = 10000)
    public void checkPaymentStatuses() {
        List<Payment> pendingPayments = paymentRepo.findByStatus("PENDING");
        for (Payment payment : pendingPayments) {
            if (payment.getTransactionId() != null) {
                Map<String, String> passwordData = mpesaService.generatePassword();
                try {
                    String response = queryStkPush(
                            mpesaService.getShortcode(),
                            passwordData.get("password"),
                            passwordData.get("timestamp"),
                            payment.getTransactionId()
                    );
                    JSONObject jsonResponse = new JSONObject(response);
                    String resultCode = jsonResponse.getString("ResultCode");
                    String transactionId = jsonResponse.has("MpesaReceiptNumber") ? 
                            jsonResponse.getString("MpesaReceiptNumber") : payment.getTransactionId();
                    String newStatus = "0".equals(resultCode) ? "ACTIVE" : "FAILED";
                    if (!payment.getStatus().equals(newStatus)) {
                        updatePaymentStatus(payment.getId(), transactionId, newStatus);
                    }
                } catch (Exception e) {
                    System.err.println("Error checking payment status for paymentId " + payment.getId() + ": " + e.getMessage());
                }
            }
        }
    }
}