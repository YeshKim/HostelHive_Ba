package com.hostelhive.hostelhive.Service;

import com.hostelhive.hostelhive.models.Payment;
import com.hostelhive.hostelhive.models.Transaction;
import com.hostelhive.hostelhive.models.Booking;
import com.hostelhive.hostelhive.repository.PaymentRepo;
import com.hostelhive.hostelhive.repository.TransactionRepo;
import com.hostelhive.hostelhive.repository.BookingRepo;
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
    private final BookingRepo bookingRepo;  // Added for booking status update
    private final MpesaService mpesaService;

    @Autowired
    public PaymentService(PaymentRepo paymentRepo,
                          TransactionRepo transactionRepo,
                          BookingRepo bookingRepo,  // Inject BookingRepo
                          MpesaService mpesaService) {
        this.paymentRepo = paymentRepo;
        this.transactionRepo = transactionRepo;
        this.bookingRepo = bookingRepo;
        this.mpesaService = mpesaService;
    }

    public MpesaService getMpesaService() {
        return mpesaService;
    }

    // CREATE PAYMENT
    public Payment createPayment(Payment payment) {
        if (payment.getBookingId() == null || payment.getAmount() == null) {
            throw new IllegalArgumentException("Booking ID and amount are required");
        }
        return paymentRepo.save(payment);
    }

    // GET BY ID
    @Transactional(readOnly = true)
    public Payment getPaymentById(Long paymentId) {
        return paymentRepo.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + paymentId));
    }

    // GET ALL
    @Transactional(readOnly = true)
    public List<Payment> getAllPayments() {
        return paymentRepo.findAll();
    }

    // UPDATE STATUS + RECORD TRANSACTION
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

    // INITIATE STK PUSH
    public String initiateStudentPayment(Long bookingId, String phoneNumber, double amount) {
        try {
            System.out.println("Initiating payment - bookingId: " + bookingId + ", phoneNumber: " + phoneNumber + ", amount: " + amount);
            Payment payment = new Payment(bookingId, amount);
            payment = paymentRepo.save(payment);
            System.out.println("Payment saved with id: " + payment.getId());

            String response = mpesaService.initiateStkPush(
                phoneNumber, amount,
                "Booking_" + payment.getId(),
                "Hostel Booking Payment"
            );
            System.out.println("STK Push response: " + response);

            // Parse CheckoutRequestID
            JSONObject jsonResponse = new JSONObject(response);
            String checkoutRequestId = jsonResponse.getString("CheckoutRequestID");

            // Store in payment
            payment.setTransactionId(checkoutRequestId);
            paymentRepo.save(payment);

            return response;
        } catch (Exception e) {
            System.err.println("Error in initiateStudentPayment: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    // DISBURSE TO MANAGER
    public String disburseToManager(Long paymentId, String phoneNumber, double amount) {
        Payment payment = paymentRepo.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + paymentId));
        payment.setStatus("DISBURSED");
        paymentRepo.save(payment);
        return mpesaService.initiateB2CDisbursement(
            phoneNumber, amount,
            "Manager Disbursement for Booking " + payment.getBookingId()
        );
    }

    // GET BY BOOKING ID
    @Transactional(readOnly = true)
    public List<Payment> getPaymentsByBookingId(Long bookingId) {
        return paymentRepo.findByBookingId(bookingId);
    }

    // GET BY STATUS
    @Transactional(readOnly = true)
    public List<Payment> getPaymentsByStatus(String status) {
        return paymentRepo.findByStatus(status);
    }

    // GET BY TRANSACTION ID
    @Transactional(readOnly = true)
    public Payment getPaymentByTransactionId(String transactionId) {
        return paymentRepo.findByTransactionId(transactionId);
    }

    // QUERY STK PUSH STATUS
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
                queryUrl, HttpMethod.POST, requestEntity, String.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            JSONObject jsonResponse = new JSONObject(response.getBody());
            String resultCode = jsonResponse.optString("ResultCode", "1");
            String receiptNumber = jsonResponse.has("MpesaReceiptNumber")
                    ? jsonResponse.getString("MpesaReceiptNumber")
                    : checkoutRequestId;

            Payment payment = paymentRepo.findByTransactionId(checkoutRequestId);
            if (payment != null) {
                String newStatus = "0".equals(resultCode) ? "ACTIVE" : "FAILED";
                updatePaymentStatus(payment.getId(), receiptNumber, newStatus);
            }
            return response.getBody();
        } else {
            throw new RuntimeException("STK Push query failed: " + response.getBody());
        }
    }

    // HANDLE M-PESA CALLBACK
    public void handleCallback(String checkoutRequestId, String receiptNumber, String status) {
        Payment payment = paymentRepo.findByTransactionId(checkoutRequestId);
        if (payment != null) {
            updatePaymentStatus(payment.getId(), receiptNumber, status);

            // UPDATE BOOKING STATUS
            Booking booking = bookingRepo.findById(payment.getBookingId())
                    .orElseThrow(() -> new RuntimeException("Booking not found for payment: " + payment.getId()));
            booking.setStatus(status.equals("ACTIVE") ? "CONFIRMED" : "CANCELLED");
            bookingRepo.save(booking);

            System.out.println("CALLBACK SUCCESS: Booking " + booking.getId() + " -> " + booking.getStatus());
        } else {
            System.err.println("CALLBACK ERROR: No payment found for CheckoutRequestID: " + checkoutRequestId);
        }
    }

    // SCHEDULED POLLING (every 10 sec)
    @Scheduled(fixedRate = 10000)
    public void checkPaymentStatuses() {
        List<Payment> pendingPayments = paymentRepo.findByStatus("PENDING");
        System.out.println("Polling " + pendingPayments.size() + " pending payments...");

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
                    String resultCode = jsonResponse.optString("ResultCode", "1");
                    String receiptNumber = jsonResponse.has("MpesaReceiptNumber")
                            ? jsonResponse.getString("MpesaReceiptNumber")
                            : payment.getTransactionId();

                    String newStatus = "0".equals(resultCode) ? "ACTIVE" : "FAILED";
                    if (!payment.getStatus().equals(newStatus)) {
                        updatePaymentStatus(payment.getId(), receiptNumber, newStatus);
                        System.out.println("POLLING UPDATE: Payment " + payment.getId() + " -> " + newStatus);
                    }
                } catch (Exception e) {
                    System.err.println("Polling error for payment " + payment.getId() + ": " + e.getMessage());
                }
            }
        }
    }
}