package com.hostelhive.hostelhive.Service;

import com.hostelhive.hostelhive.models.Booking;
import com.hostelhive.hostelhive.repository.BookingRepo;
import com.hostelhive.hostelhive.exceptions.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class BookingService {
    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);
    private final BookingRepo bookingRepo;

    @Autowired
    public BookingService(BookingRepo bookingRepo) {
        this.bookingRepo = bookingRepo;
    }

    public Booking createBooking(Booking booking) {
        logger.debug("Creating booking: studentId={}, roomId={}, startDate={}, endDate={}, status={}",
                booking.getStudentId(), booking.getRoomId(), booking.getStartDate(), booking.getEndDate(), booking.getStatus());
        if (booking.getStartDate().isAfter(booking.getEndDate())) {
            throw new IllegalArgumentException("Start date must be before end date");
        }
        return bookingRepo.save(booking);
    }

    // Other methods remain unchanged
    // ...


    /**
     * Get booking by ID
     * @param bookingId the booking ID
     * @return the booking
     * @throws ResourceNotFoundException if booking not found
     */
    @Transactional(readOnly = true)
    public Booking getBookingById(Long bookingId) {
        return bookingRepo.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));
    }

    /**
     * Get all bookings
     * @return list of all bookings
     */
    @Transactional(readOnly = true)
    public List<Booking> getAllBookings() {
        return bookingRepo.findAll();
    }

    /**
     * Update booking details
     * @param bookingId the booking ID
     * @param updatedBooking the updated booking details
     * @return the updated booking
     * @throws ResourceNotFoundException if booking not found
     * @throws IllegalArgumentException if dates are invalid
     */
    public Booking updateBooking(Long bookingId, Booking updatedBooking) {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        if (updatedBooking.getStudentId() != null) booking.setStudentId(updatedBooking.getStudentId());
        if (updatedBooking.getRoomId() != null) booking.setRoomId(updatedBooking.getRoomId());
        if (updatedBooking.getStartDate() != null && updatedBooking.getEndDate() != null) {
            if (updatedBooking.getStartDate().isAfter(updatedBooking.getEndDate())) {
                throw new IllegalArgumentException("Start date must be before end date");
            }
            booking.setStartDate(updatedBooking.getStartDate());
            booking.setEndDate(updatedBooking.getEndDate());
        }
        if (updatedBooking.getStatus() != null) booking.setStatus(updatedBooking.getStatus());

        return bookingRepo.save(booking);
    }

    /**
     * Delete booking
     * @param bookingId the booking ID
     * @throws ResourceNotFoundException if booking not found
     */
    public void deleteBooking(Long bookingId) {
        if (!bookingRepo.existsById(bookingId)) {
            throw new ResourceNotFoundException("Booking not found with id: " + bookingId);
        }
        bookingRepo.deleteById(bookingId);
    }

    /**
     * Get bookings by student ID
     * @param studentId the student ID
     * @return list of bookings for the student
     */
    @Transactional(readOnly = true)
    public List<Booking> getBookingsByStudentId(Long studentId) {
        return bookingRepo.findByStudentId(studentId);
    }

    /**
     * Get bookings by room ID
     * @param roomId the room ID
     * @return list of bookings for the room
     */
    @Transactional(readOnly = true)
    public List<Booking> getBookingsByRoomId(Long roomId) {
        return bookingRepo.findByRoomId(roomId);
    }

    /**
     * Get active bookings
     * @return list of active bookings
     */
    @Transactional(readOnly = true)
    public List<Booking> getActiveBookings() {
        return bookingRepo.findActiveBookings();
    }

    /**
     * Get bookings by student ID and status
     * @param studentId the student ID
     * @param status the booking status
     * @return list of bookings matching the criteria
     */
    @Transactional(readOnly = true)
    public List<Booking> getBookingsByStudentIdAndStatus(Long studentId, String status) {
        return bookingRepo.findByStudentIdAndStatus(studentId, status);
    }
    public List<Booking> getBookingsByHostelId(Long hostelId) {
        return bookingRepo.findByHostelId(hostelId);
    }

    public List<Booking> getBookingsByHostelIdAndStatus(Long hostelId, String status) {
        return bookingRepo.findByHostelIdAndStatus(hostelId, status);
    }
    public Booking updateBookingStatus(Long id, String status) {
        Booking booking = bookingRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));
        
        // Validate status
        if (!isValidStatus(status)) {
            throw new IllegalArgumentException("Invalid status: " + status);
        }
        
        booking.setStatus(status);
        return bookingRepo.save(booking);
    }

    private boolean isValidStatus(String status) {
        return status.equals("PENDING") || status.equals("CONFIRMED") || 
               status.equals("CANCELLED") || status.equals("EXPIRED");
    }
}