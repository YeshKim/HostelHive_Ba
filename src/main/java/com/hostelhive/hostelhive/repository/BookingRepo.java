package com.hostelhive.hostelhive.repository;

import com.hostelhive.hostelhive.models.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepo extends JpaRepository<Booking, Long> {
    /**
     * Find bookings by student ID
     * @param studentId the student ID to search for
     * @return List of bookings associated with the student
     */
    List<Booking> findByStudentId(Long studentId);

    /**
     * Find bookings by room ID
     * @param roomId the room ID to search for
     * @return List of bookings associated with the room
     */
    List<Booking> findByRoomId(Long roomId);

    /**
     * Find active bookings (status = 'active')
     * @return List of active bookings
     */
    @Query("SELECT b FROM Booking b WHERE b.status = 'active'")
    List<Booking> findActiveBookings();

    /**
     * Find bookings by student ID and status
     * @param studentId the student ID
     * @param status the booking status
     * @return List of bookings matching the criteria
     */
    @Query("SELECT b FROM Booking b WHERE b.studentId = :studentId AND b.status = :status")
    List<Booking> findByStudentIdAndStatus(@Param("studentId") Long studentId, @Param("status") String status);
}