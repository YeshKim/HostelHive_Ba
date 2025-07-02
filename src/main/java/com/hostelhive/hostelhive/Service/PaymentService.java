package com.hostelhive.hostelhive.Service;

import com.hostelhive.hostelhive.models.Payment;
import com.hostelhive.hostelhive.models.Transaction;
import com.hostelhive.hostelhive.repository.PaymentRepo;
import com.hostelhive.hostelhive.repository.TransactionRepo;
import com.hostelhive.hostelhive.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            return response;
        } catch (Exception e) {
            System.err.println("Error in initiateStudentPayment: " + e.getMessage());
            e.printStackTrace();
            throw e; // Re-throw to ensure the 500 error includes the stack trace
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

    @Scheduled(fixedRate = 10000) // Runs every 10 seconds
    public void checkPaymentStatuses() {
        List<Payment> pendingPayments = paymentRepo.findByStatus("PENDING");
        for (Payment payment : pendingPayments) {
            if (payment.getTransactionId() != null) {
                Map<String, Object> response = mpesaService.queryTransactionStatus(payment.getTransactionId(), "STK");
                if (response != null && response.containsKey("Result")) {
                    Map<String, Object> result = (Map<String, Object>) response.get("Result");
                    String resultCode = result.get("ResultCode").toString();
                    String newStatus = "0".equals(resultCode) ? "COMPLETED" : "FAILED";
                    if (!payment.getStatus().equals(newStatus)) {
                        updatePaymentStatus(payment.getId(), payment.getTransactionId(), newStatus);
                    }
                }
            }
        }
    }
}