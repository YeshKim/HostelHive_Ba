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

    // ===================== CREATE =====================
    public Booking createBooking(Booking booking) {
        logger.debug("Creating booking: studentId={}, roomId={}, startDate={}, endDate={}, status={}",
                booking.getStudentId(), booking.getRoomId(), booking.getStartDate(), booking.getEndDate(), booking.getStatus());

        if (booking.getStartDate().isAfter(booking.getEndDate())) {
            throw new IllegalArgumentException("Start date must be before end date");
        }
        return bookingRepo.save(booking);
    }

    // ===================== READ =====================
    @Transactional(readOnly = true)
    public Booking getBookingById(Long bookingId) {
        return bookingRepo.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));
    }

    @Transactional(readOnly = true)
    public List<Booking> getAllBookings() {
        return bookingRepo.findAll();
    }

    @Transactional(readOnly = true)
    public List<Booking> getBookingsByStudentId(Long studentId) {
        return bookingRepo.findByStudentId(studentId);
    }

    @Transactional(readOnly = true)
    public List<Booking> getBookingsByRoomId(Long roomId) {
        return bookingRepo.findByRoomId(roomId);
    }

    @Transactional(readOnly = true)
    public List<Booking> getActiveBookings() {
        return bookingRepo.findActiveBookings();
    }

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

    // ===================== UPDATE =====================
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

        if (updatedBooking.getStatus() != null) {
            booking.setStatus(updatedBooking.getStatus().toUpperCase());
        }

        return bookingRepo.save(booking);
    }

    // ===================== STATUS UPDATE (PATCH) =====================
    public Booking updateBookingStatus(Long id, String status) {
        Booking booking = bookingRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));

        String upperStatus = status.toUpperCase();

        if (!isValidStatus(upperStatus)) {
            throw new IllegalArgumentException(
                "Invalid status: " + status +
                ". Allowed values: PENDING, ACTIVE, CONFIRMED, CANCELLED, COMPLETED, EXPIRED"
            );
        }

        logger.info("Updating booking {} status from {} → {}", id, booking.getStatus(), upperStatus);
        booking.setStatus(upperStatus);
        return bookingRepo.save(booking);
    }

    private boolean isValidStatus(String status) {
        return List.of("PENDING", "ACTIVE", "CONFIRMED", "CANCELLED", "COMPLETED", "EXPIRED")
                   .contains(status);
    }

    // ===================== DELETE =====================
    public void deleteBooking(Long bookingId) {
        if (!bookingRepo.existsById(bookingId)) {
            throw new ResourceNotFoundException("Booking not found with id: " + bookingId);
        }
        bookingRepo.deleteById(bookingId);
    }
}