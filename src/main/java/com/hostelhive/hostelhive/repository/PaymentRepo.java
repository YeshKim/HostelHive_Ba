package com.hostelhive.hostelhive.repository;

import com.hostelhive.hostelhive.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepo extends JpaRepository<Payment, Long> {
    /**
     * Find payments by booking ID
     * @param bookingId the booking ID to search for
     * @return List of payments associated with the booking
     */
    List<Payment> findByBookingId(Long bookingId);

    /**
     * Find payments by status
     * @param status the payment status
     * @return List of payments with the specified status
     */
    @Query("SELECT p FROM Payment p WHERE p.status = :status")
    List<Payment> findByStatus(@Param("status") String status);

    /**
     * Find payment by transaction ID
     * @param transactionId the M-Pesa transaction ID
     * @return Payment associated with the transaction ID, or null if not found
     */
    @Query("SELECT p FROM Payment p WHERE p.transactionId = :transactionId")
    Payment findByTransactionId(@Param("transactionId") String transactionId);
}