package com.hostelhive.hostelhive.Controller;

import com.hostelhive.hostelhive.Service.PaymentService;
import com.hostelhive.hostelhive.models.Payment;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<Payment> createPayment(@Valid @RequestBody Payment payment) {
        Payment createdPayment = paymentService.createPayment(payment);
        return new ResponseEntity<>(createdPayment, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable Long id) {
        Payment payment = paymentService.getPaymentById(id);
        return new ResponseEntity<>(payment, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
        List<Payment> payments = paymentService.getAllPayments();
        return new ResponseEntity<>(payments, HttpStatus.OK);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Payment> updatePaymentStatus(@PathVariable Long id,
                                                      @RequestParam String transactionId,
                                                      @RequestParam String status) {
        Payment updatedPayment = paymentService.updatePaymentStatus(id, transactionId, status);
        return new ResponseEntity<>(updatedPayment, HttpStatus.OK);
    }

    @PostMapping("/initiate")
    public ResponseEntity<String> initiateStudentPayment(@RequestParam Long bookingId,
                                                        @RequestParam String phoneNumber,
                                                        @RequestParam double amount) {
        String response = paymentService.initiateStudentPayment(bookingId, phoneNumber, amount);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/disburse")
    public ResponseEntity<String> disburseToManager(@RequestParam Long paymentId,
                                                   @RequestParam String phoneNumber,
                                                   @RequestParam double amount) {
        String response = paymentService.disburseToManager(paymentId, phoneNumber, amount);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/by-booking/{bookingId}")
    public ResponseEntity<List<Payment>> getPaymentsByBookingId(@PathVariable Long bookingId) {
        List<Payment> payments = paymentService.getPaymentsByBookingId(bookingId);
        return new ResponseEntity<>(payments, HttpStatus.OK);
    }

    @GetMapping("/by-status")
    public ResponseEntity<List<Payment>> getPaymentsByStatus(@RequestParam String status) {
        List<Payment> payments = paymentService.getPaymentsByStatus(status);
        return new ResponseEntity<>(payments, HttpStatus.OK);
    }

    // Endpoint to simulate callback update
    @PostMapping("/simulate-callback")
    public ResponseEntity<String> simulateCallback(@RequestBody Map<String, String> callbackData) {
        String transactionId = callbackData.get("transactionId");
        String status = callbackData.get("status");
        Long paymentId = Long.parseLong(callbackData.get("paymentId")); // Assume paymentId is sent for simulation
        paymentService.updatePaymentStatus(paymentId, transactionId, status);
        return ResponseEntity.ok("Payment status updated via simulation");
    }
}