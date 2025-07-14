package com.hostelhive.hostelhive.Controller;

import com.hostelhive.hostelhive.DTOs.CallbackMetadata;
import com.hostelhive.hostelhive.DTOs.ItemDTO;
import com.hostelhive.hostelhive.DTOs.StkCallback;
import com.hostelhive.hostelhive.DTOs.StkResultBody;
import com.hostelhive.hostelhive.models.Payment;
import com.hostelhive.hostelhive.models.PaymentRequest;
import com.hostelhive.hostelhive.Service.PaymentService;
import jakarta.annotation.security.PermitAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);
    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<Payment> createPayment(@Valid @RequestBody Payment payment) {
        try {
            logger.info("Creating payment for bookingId: {}, amount: {}", payment.getBookingId(), payment.getAmount());
            Payment createdPayment = paymentService.createPayment(payment);
            logger.info("Payment created successfully: {}", createdPayment.getId());
            return new ResponseEntity<>(createdPayment, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error creating payment: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable Long id) {
        try {
            logger.info("Retrieving payment with id: {}", id);
            Payment payment = paymentService.getPaymentById(id);
            logger.info("Payment retrieved successfully: {}", payment.getId());
            return new ResponseEntity<>(payment, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error retrieving payment: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
        try {
            logger.info("Retrieving all payments");
            List<Payment> payments = paymentService.getAllPayments();
            logger.info("Retrieved {} payments", payments.size());
            return new ResponseEntity<>(payments, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error retrieving all payments: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Payment> updatePaymentStatus(@PathVariable Long id,
                                                      @RequestParam String transactionId,
                                                      @RequestParam String status) {
        try {
            logger.info("Updating status for payment id: {}, transactionId: {}, status: {}", id, transactionId, status);
            Payment updatedPayment = paymentService.updatePaymentStatus(id, transactionId, status);
            logger.info("Payment status updated successfully: {}", updatedPayment.getId());
            return new ResponseEntity<>(updatedPayment, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error updating payment status: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/initiate")
    @PermitAll
    public ResponseEntity<String> initiateStudentPayment(@RequestBody PaymentRequest paymentRequest) {
        try {
            logger.info("Initiating payment for bookingId: {}, phone: {}, amount: {}", 
                        paymentRequest.getBookingId(), 
                        paymentRequest.getPhoneNumber(), 
                        paymentRequest.getAmount());

            if (paymentRequest.getBookingId() == null) {
                logger.error("No bookingId provided in payment request");
                return ResponseEntity.badRequest().body("{\"message\":\"Booking ID is required\"}");
            }

            String response = paymentService.initiateStudentPayment(paymentRequest.getBookingId(), 
                                                                  paymentRequest.getPhoneNumber(), 
                                                                  paymentRequest.getAmount());
            logger.info("Payment initiated successfully: {}", response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error initiating payment: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\":\"" + e.getMessage() + "\"}");
        }
    }

    @PostMapping("/disburse")
    public ResponseEntity<String> disburseToManager(@RequestParam Long paymentId,
                                                   @RequestParam String phoneNumber,
                                                   @RequestParam double amount) {
        try {
            logger.info("Disbursing to manager for paymentId: {}, phone: {}, amount: {}", paymentId, phoneNumber, amount);
            String response = paymentService.disburseToManager(paymentId, phoneNumber, amount);
            logger.info("Disbursement initiated successfully: {}", response);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error disbursing to manager: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\":\"" + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/by-booking/{bookingId}")
    public ResponseEntity<List<Payment>> getPaymentsByBookingId(@PathVariable Long bookingId) {
        try {
            logger.info("Retrieving payments for bookingId: {}", bookingId);
            List<Payment> payments = paymentService.getPaymentsByBookingId(bookingId);
            logger.info("Retrieved {} payments for bookingId: {}", payments.size(), bookingId);
            return new ResponseEntity<>(payments, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error retrieving payments by bookingId: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/by-status")
    public ResponseEntity<List<Payment>> getPaymentsByStatus(@RequestParam String status) {
        try {
            logger.info("Retrieving payments with status: {}", status);
            List<Payment> payments = paymentService.getPaymentsByStatus(status);
            logger.info("Retrieved {} payments with status: {}", payments.size(), status);
            return new ResponseEntity<>(payments, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error retrieving payments by status: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/simulate-callback")
    public ResponseEntity<String> simulateCallback(@RequestBody Map<String, String> callbackData) {
        try {
            logger.info("Simulating callback with data: {}", callbackData);
            String transactionId = callbackData.get("transactionId");
            String status = callbackData.get("status");
            Long paymentId = Long.parseLong(callbackData.get("paymentId"));
            paymentService.updatePaymentStatus(paymentId, transactionId, status);
            logger.info("Payment status updated via simulation for paymentId: {}", paymentId);
            return ResponseEntity.ok("Payment status updated via simulation");
        } catch (Exception e) {
            logger.error("Error simulating callback: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\":\"" + e.getMessage() + "\"}");
        }
    }

    @PostMapping("/handleCallback")
    @PermitAll
    public ResponseEntity<Void> handlePaymentCallback(@RequestBody(required = false) StkResultBody callbackData) {
        try {
            logger.info("M-Pesa Callback received: {}", callbackData);
            if (callbackData == null || callbackData.getBody() == null || callbackData.getBody().getStkCallback() == null) {
                logger.error("Invalid M-Pesa callback received: null or missing required data");
                return ResponseEntity.badRequest().build();
            }

            StkCallback callback = callbackData.getBody().getStkCallback();
            String resultDesc = callback.getResultDesc();
            int resultCode = callback.getResultCode();
            String merchantRequestId = callback.getMerchantRequestID();
            String checkoutRequestId = callback.getCheckoutRequestID();

            logger.info("M-Pesa Result Description: {}, CheckoutRequestID: {}", resultDesc, checkoutRequestId);

            CallbackMetadata metadata = callback.getCallbackMetadata();
            String phoneNumber = "";
            double amount = 0.0;
            String transactionId = null;

            if (metadata != null && metadata.getItems() != null) {
                logger.info("M-Pesa Callback Metadata:");
                for (ItemDTO item : metadata.getItems()) {
                    logger.info("  - {}: {}", item.getName(), item.getValue());
                    if ("PhoneNumber".equals(item.getName())) {
                        phoneNumber = item.getValue();
                    } else if ("Amount".equals(item.getName())) {
                        amount = Double.parseDouble(item.getValue());
                    } else if ("MpesaReceiptNumber".equals(item.getName())) {
                        transactionId = item.getValue();
                    }
                }
            }

            logger.info("M-Pesa Transaction Details - Phone: {}, Amount: {}, Receipt: {}, MerchantRequestID: {}",
                        phoneNumber, amount, transactionId, merchantRequestId);

            String status = (resultCode == 0) ? "ACTIVE" : "FAILED";
            paymentService.handleCallback(checkoutRequestId, transactionId, status);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error handling payment callback: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/query-stk")
    @PermitAll
    public ResponseEntity<String> queryStkPush(@RequestBody Map<String, String> queryRequest) {
        try {
            String checkoutRequestId = queryRequest.get("CheckoutRequestID");
            if (checkoutRequestId == null) {
                logger.error("CheckoutRequestID is required");
                return ResponseEntity.badRequest().body("{\"message\":\"CheckoutRequestID is required\"}");
            }

            Map<String, String> passwordData = paymentService.getMpesaService().generatePassword();
            String response = paymentService.queryStkPush(
                    paymentService.getMpesaService().getShortcode(),
                    passwordData.get("password"),
                    passwordData.get("timestamp"),
                    checkoutRequestId
            );

            logger.info("STK push query successful: {}", response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error querying STK push: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"message\":\"" + e.getMessage() + "\"}");
        }
    }
}