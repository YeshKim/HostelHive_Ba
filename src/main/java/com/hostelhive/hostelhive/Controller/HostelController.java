package com.hostelhive.hostelhive.Controller;

import com.hostelhive.hostelhive.models.Hostel;
import com.hostelhive.hostelhive.Service.HostelService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/hostels")
@CrossOrigin(origins = "*")
public class HostelController {

    @Autowired
    private HostelService hostelService;

    @PostMapping("/post-hostel")
    public ResponseEntity<?> createHostel(@Valid @RequestBody Hostel hostel) {
        try {
            // Validate required fields
            if (hostel.getName() == null || hostel.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Property name is required");
            }
            
            if (hostel.getAddress() == null || hostel.getAddress().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Address is required");
            }
            
            if (hostel.getLocation() == null || hostel.getLocation().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Location is required");
            }
            
            if (hostel.getPricePerMonth() == null || hostel.getPricePerMonth() <= 0) {
                return ResponseEntity.badRequest().body("Valid price per month is required");
            }
            
            if (hostel.getManagerId() == null) {
                return ResponseEntity.badRequest().body("Manager ID is required");
            }

            // Validate room numbers
            if (hostel.getTotalRooms() != null && hostel.getAvailableRooms() != null) {
                if (hostel.getAvailableRooms() > hostel.getTotalRooms()) {
                    return ResponseEntity.badRequest().body("Available rooms cannot exceed total rooms");
                }
            }

            // Validate phone numbers format (optional)
            if (hostel.getContactPhone() != null && !isValidPhoneNumber(hostel.getContactPhone())) {
                return ResponseEntity.badRequest().body("Invalid contact phone format");
            }

            // Set default values
            if (hostel.getIsVerified() == null) {
                hostel.setIsVerified(false);
            }

            // Save hostel
            Hostel savedHostel = hostelService.save(hostel);
            return ResponseEntity.ok(savedHostel);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating hostel: " + e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Hostel>> getAllHostels() {
        try {
            List<Hostel> hostels = hostelService.findAll();
            return ResponseEntity.ok(hostels);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Hostel> getHostelById(@PathVariable Long id) {
        try {
            Optional<Hostel> hostel = hostelService.findById(id);
            if (hostel.isPresent()) {
                return ResponseEntity.ok(hostel.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/manager/{managerId}")
    public ResponseEntity<List<Hostel>> getHostelsByManager(@PathVariable Long managerId) {
        try {
            List<Hostel> hostels = hostelService.findByManagerId(managerId);
            return ResponseEntity.ok(hostels);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateHostel(@PathVariable Long id, @Valid @RequestBody Hostel hostel) {
        try {
            Optional<Hostel> existingHostel = hostelService.findById(id);
            if (existingHostel.isPresent()) {
                hostel.setId(id);
                Hostel updatedHostel = hostelService.save(hostel);
                return ResponseEntity.ok(updatedHostel);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating hostel: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteHostel(@PathVariable Long id) {
        try {
            if (hostelService.findById(id).isPresent()) {
                hostelService.deleteById(id);
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting hostel: " + e.getMessage());
        }
    }

    // Helper method for phone validation
    private boolean isValidPhoneNumber(String phone) {
        // Basic validation for Kenyan phone numbers
        return phone.matches("^\\+254[0-9]{9}$");
    }
}