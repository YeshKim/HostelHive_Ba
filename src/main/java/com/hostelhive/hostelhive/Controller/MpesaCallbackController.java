package com.hostelhive.hostelhive.Controller;

import com.hostelhive.hostelhive.Service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import java.util.Map;

@RestController
@RequestMapping("/api/mpesa/callback")
public class MpesaCallbackController {

    private final PaymentService paymentService;

    @Autowired
    public MpesaCallbackController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<String> handleMpesaCallback(@RequestBody Map<String, Object> callbackData) {
        // Log the callback data for debugging
        System.out.println("M-Pesa Callback Received: " + callbackData);

        try {
            // Check if it's an STK Push callback
            if (callbackData.containsKey("Body")) {
                Map<String, Object> body = (Map<String, Object>) callbackData.get("Body");
                Map<String, Object> stkCallback = (Map<String, Object>) body.get("stkCallback");

                if (stkCallback != null) {
                    String resultCode = stkCallback.get("ResultCode").toString();
                    String resultDesc = stkCallback.get("ResultDesc").toString();
                    String merchantRequestID = stkCallback.get("MerchantRequestID").toString();
                    String checkoutRequestID = stkCallback.get("CheckoutRequestID").toString();

                    // Extract transaction details
                    Map<String, Object> callbackMetadata = (Map<String, Object>) stkCallback.get("CallbackMetadata");
                    if (callbackMetadata != null) {
                        for (Map<String, Object> item : (Iterable<Map<String, Object>>) callbackMetadata.get("Item")) {
                            String name = item.get("Name").toString();
                            if ("MpesaReceiptNumber".equals(name)) {
                                String transactionId = item.get("Value").toString();
                                String amount = item.get("Value").toString(); // Adjust based on your logic
                                String status = "0".equals(resultCode) ? "COMPLETED" : "FAILED";

                                // Update payment (assuming bookingId can be derived from merchantRequestID or similar)
                                // For now, you'll need to map merchantRequestID to a payment ID
                                Long paymentId = extractPaymentIdFromRequest(merchantRequestID); // Implement this logic
                                if (paymentId != null) {
                                    paymentService.updatePaymentStatus(paymentId, transactionId, status);
                                }
                            }
                        }
                    }
                }
            } else if (callbackData.containsKey("Result")) {
                // Handle B2C callback (disbursement)
                Map<String, Object> result = (Map<String, Object>) callbackData.get("Result");
                String resultCode = result.get("ResultCode").toString();
                String resultDesc = result.get("ResultDesc").toString();
                String transactionId = result.get("TransactionID").toString();
                String status = "0".equals(resultCode) ? "DISBURSED" : "FAILED";

                // Update payment (assuming paymentId can be derived from transaction context)
                Long paymentId = extractPaymentIdFromTransaction(transactionId); // Implement this logic
                if (paymentId != null) {
                    paymentService.updatePaymentStatus(paymentId, transactionId, status);
                }
            }

            // Return success response as required by M-Pesa
            return ResponseEntity.ok("Callback received successfully");
        } catch (Exception e) {
            System.err.println("Error processing callback: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing callback");
        }
    }

    // Placeholder methods to map request IDs to payment IDs
    private Long extractPaymentIdFromRequest(String merchantRequestID) {
        // Implement logic to map merchantRequestID to a Payment ID (e.g., from database or request context)
        // This is a simplification; in practice, you might store this mapping when initiating STK Push
        return 1L; // Replace with actual logic
    }

    private Long extractPaymentIdFromTransaction(String transactionId) {
        // Implement logic to map transactionId to a Payment ID (e.g., query database)
        // This is a simplification; in practice, you might store this when initiating B2C
        return 1L; // Replace with actual logic
    }
}