package com.hostelhive.hostelhive.Service;

import com.hostelhive.hostelhive.models.Payment;
import com.hostelhive.hostelhive.repository.PaymentRepo;
import com.hostelhive.hostelhive.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PaymentService {
    private final PaymentRepo paymentRepo;
    private final MpesaService mpesaService;

    @Autowired
    public PaymentService(PaymentRepo paymentRepo, MpesaService mpesaService) {
        this.paymentRepo = paymentRepo;
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
        return paymentRepo.save(payment);
    }

    public String initiateStudentPayment(Long bookingId, String phoneNumber, double amount) {
        Payment payment = new Payment(bookingId, amount);
        payment = paymentRepo.save(payment);
        return mpesaService.initiateStkPush(phoneNumber, amount, "Booking_" + payment.getId(), "Hostel Booking Payment");
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
        return paymentRepo.findAll().stream()
                .filter(p -> p.getTransactionId() != null && p.getTransactionId().equals(transactionId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with transaction ID: " + transactionId));
    }
}