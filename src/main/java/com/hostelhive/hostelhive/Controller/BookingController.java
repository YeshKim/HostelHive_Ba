package com.hostelhive.hostelhive.Controller;

import com.hostelhive.hostelhive.models.Booking;
import com.hostelhive.hostelhive.Service.BookingService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);
    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<?> createBooking(@Valid @RequestBody Booking booking, BindingResult result) {
        logger.info("Received booking request: {}", booking);
        if (result.hasErrors()) {
            List<String> errors = result.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.toList());
            logger.warn("Validation errors: {}", errors);
            return new ResponseEntity<>(Map.of("errors", errors), HttpStatus.BAD_REQUEST);
        }
        try {
            Booking createdBooking = bookingService.createBooking(booking);
            return new ResponseEntity<>(createdBooking, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid booking data: {}", e.getMessage());
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (DataIntegrityViolationException e) {
            logger.error("Database error: {}", e.getMessage());
            return new ResponseEntity<>(Map.of("error", "Invalid studentId or roomId"), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage(), e);
            return new ResponseEntity<>(Map.of("error", "Internal server error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

   

    // Get booking by ID
    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable Long id) {
        Booking booking = bookingService.getBookingById(id);
        return new ResponseEntity<>(booking, HttpStatus.OK);
    }

    // Get all bookings
    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookings() {
        List<Booking> bookings = bookingService.getAllBookings();
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }

    // Update booking
    @PutMapping("/{id}")
    public ResponseEntity<Booking> updateBooking(@PathVariable Long id, @Valid @RequestBody Booking booking) {
        Booking updatedBooking = bookingService.updateBooking(id, booking);
        return new ResponseEntity<>(updatedBooking, HttpStatus.OK);
    }

    // Delete booking
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Get bookings by student ID
    @GetMapping("/by-student/{studentId}")
    public ResponseEntity<List<Booking>> getBookingsByStudentId(@PathVariable Long studentId) {
        List<Booking> bookings = bookingService.getBookingsByStudentId(studentId);
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }

    // Get bookings by room ID
    @GetMapping("/by-room/{roomId}")
    public ResponseEntity<List<Booking>> getBookingsByRoomId(@PathVariable Long roomId) {
        List<Booking> bookings = bookingService.getBookingsByRoomId(roomId);
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }

    // Get active bookings
    @GetMapping("/active")
    public ResponseEntity<List<Booking>> getActiveBookings() {
        List<Booking> bookings = bookingService.getActiveBookings();
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }

    // Get bookings by student ID and status
    @GetMapping("/by-student/{studentId}/status")
    public ResponseEntity<List<Booking>> getBookingsByStudentIdAndStatus(
            @PathVariable Long studentId,
            @RequestParam String status) {
        List<Booking> bookings = bookingService.getBookingsByStudentIdAndStatus(studentId, status);
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }
}