package com.hostelhive.hostelhive.Controller;

import com.hostelhive.hostelhive.models.Amenity;
import com.hostelhive.hostelhive.models.Hostel;
import com.hostelhive.hostelhive.models.HostelDTO;
import com.hostelhive.hostelhive.Service.AmenityService;
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

    @Autowired
    private AmenityService amenityService;

    // Create a new hostel
    @PostMapping("/post-hostel")
    public ResponseEntity<?> createHostel(@Valid @RequestBody HostelDTO hostelDTO) {
        try {
            // Validate required fields
            if (hostelDTO.getName() == null || hostelDTO.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Property name is required");
            }
            if (hostelDTO.getAddress() == null || hostelDTO.getAddress().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Address is required");
            }
            if (hostelDTO.getLocation() == null || hostelDTO.getLocation().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Location is required");
            }
            if (hostelDTO.getPricePerMonth() == null || hostelDTO.getPricePerMonth() <= 0) {
                return ResponseEntity.badRequest().body("Valid price per month is required");
            }
            if (hostelDTO.getManagerId() == null) {
                return ResponseEntity.badRequest().body("Manager ID is required");
            }
            if (hostelDTO.getTotalRooms() != null && hostelDTO.getAvailableRooms() != null) {
                if (hostelDTO.getAvailableRooms() > hostelDTO.getTotalRooms()) {
                    return ResponseEntity.badRequest().body("Available rooms cannot exceed total rooms");
                }
            }
            if (hostelDTO.getContactPhone() != null && !isValidPhoneNumber(hostelDTO.getContactPhone())) {
                return ResponseEntity.badRequest().body("Invalid contact phone format");
            }
            // Validate and map amenities
            List<String> amenityNames = hostelDTO.getAmenities();
            if (amenityNames == null || amenityNames.isEmpty()) {
                return ResponseEntity.badRequest().body("At least one amenity is required");
            }
            List<Amenity> amenities = amenityService.findByNames(amenityNames);
            if (amenities.size() != amenityNames.size()) {
                return ResponseEntity.badRequest().body("One or more amenities are invalid");
            }

            // Map DTO to Hostel entity
            Hostel hostel = new Hostel();
            hostel.setName(hostelDTO.getName());
            hostel.setLocation(hostelDTO.getLocation());
            hostel.setPropertyType(hostelDTO.getPropertyType());
            hostel.setRoomType(hostelDTO.getRoomType());
            hostel.setAddress(hostelDTO.getAddress());
            hostel.setTotalRooms(hostelDTO.getTotalRooms());
            hostel.setAvailableRooms(hostelDTO.getAvailableRooms());
            hostel.setPricePerMonth(hostelDTO.getPricePerMonth());
            hostel.setDepositAmount(hostelDTO.getDepositAmount());
            hostel.setAmenities(amenities);
            hostel.setDescription(hostelDTO.getDescription());
            hostel.setContactPhone(hostelDTO.getContactPhone());
            hostel.setAlternatePhone(hostelDTO.getAlternatePhone());
            hostel.setContactEmail(hostelDTO.getContactEmail());
            hostel.setImagesBase64(hostelDTO.getImagesBase64());
            hostel.setManagerId(hostelDTO.getManagerId());
            hostel.setIsVerified(hostelDTO.getIsVerified() != null ? hostelDTO.getIsVerified() : false);
            hostel.setDistance(hostelDTO.getDistance() != null ? hostelDTO.getDistance() : 0.0);

            Hostel savedHostel = hostelService.save(hostel);
            return ResponseEntity.ok(savedHostel);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating hostel: " + e.getMessage());
        }
    }

    // Get all hostels
    @GetMapping("/all")
    public ResponseEntity<List<Hostel>> getAllHostels() {
        try {
            List<Hostel> hostels = hostelService.findAll();
            return ResponseEntity.ok(hostels);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Get hostel by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getHostelById(@PathVariable Long id) {
        try {
            Optional<Hostel> hostel = hostelService.findById(id);
            if (hostel.isPresent()) {
                return ResponseEntity.ok(hostel.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Hostel not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving hostel: " + e.getMessage());
        }
    }

    // Update a hostel
    @PutMapping("/{id}")
    public ResponseEntity<?> updateHostel(@PathVariable Long id, @Valid @RequestBody HostelDTO hostelDTO) {
        try {
            Optional<Hostel> existingHostel = hostelService.findById(id);
            if (!existingHostel.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Hostel not found");
            }

            // Validate required fields
            if (hostelDTO.getName() == null || hostelDTO.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Property name is required");
            }
            if (hostelDTO.getAddress() == null || hostelDTO.getAddress().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Address is required");
            }
            if (hostelDTO.getLocation() == null || hostelDTO.getLocation().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Location is required");
            }
            if (hostelDTO.getPricePerMonth() == null || hostelDTO.getPricePerMonth() <= 0) {
                return ResponseEntity.badRequest().body("Valid price per month is required");
            }
            if (hostelDTO.getManagerId() == null) {
                return ResponseEntity.badRequest().body("Manager ID is required");
            }
            if (hostelDTO.getTotalRooms() != null && hostelDTO.getAvailableRooms() != null) {
                if (hostelDTO.getAvailableRooms() > hostelDTO.getTotalRooms()) {
                    return ResponseEntity.badRequest().body("Available rooms cannot exceed total rooms");
                }
            }
            if (hostelDTO.getContactPhone() != null && !isValidPhoneNumber(hostelDTO.getContactPhone())) {
                return ResponseEntity.badRequest().body("Invalid contact phone format");
            }
            // Validate and map amenities
            List<String> amenityNames = hostelDTO.getAmenities();
            if (amenityNames == null || amenityNames.isEmpty()) {
                return ResponseEntity.badRequest().body("At least one amenity is required");
            }
            List<Amenity> amenities = amenityService.findByNames(amenityNames);
            if (amenities.size() != amenityNames.size()) {
                return ResponseEntity.badRequest().body("One or more amenities are invalid");
            }

            // Map DTO to Hostel entity
            Hostel hostel = existingHostel.get();
            hostel.setName(hostelDTO.getName());
            hostel.setLocation(hostelDTO.getLocation());
            hostel.setPropertyType(hostelDTO.getPropertyType());
            hostel.setRoomType(hostelDTO.getRoomType());
            hostel.setAddress(hostelDTO.getAddress());
            hostel.setTotalRooms(hostelDTO.getTotalRooms());
            hostel.setAvailableRooms(hostelDTO.getAvailableRooms());
            hostel.setPricePerMonth(hostelDTO.getPricePerMonth());
            hostel.setDepositAmount(hostelDTO.getDepositAmount());
            hostel.setAmenities(amenities);
            hostel.setDescription(hostelDTO.getDescription());
            hostel.setContactPhone(hostelDTO.getContactPhone());
            hostel.setAlternatePhone(hostelDTO.getAlternatePhone());
            hostel.setContactEmail(hostelDTO.getContactEmail());
            hostel.setImagesBase64(hostelDTO.getImagesBase64());
            hostel.setManagerId(hostelDTO.getManagerId());
            hostel.setIsVerified(hostelDTO.getIsVerified() != null ? hostelDTO.getIsVerified() : hostel.getIsVerified());
            hostel.setDistance(hostelDTO.getDistance() != null ? hostelDTO.getDistance() : hostel.getDistance());

            Hostel updatedHostel = hostelService.save(hostel);
            return ResponseEntity.ok(updatedHostel);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating hostel: " + e.getMessage());
        }
    }

    // Approve a hostel
    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveHostel(@PathVariable Long id) {
        try {
            Optional<Hostel> existingHostel = hostelService.findById(id);
            if (!existingHostel.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Hostel not found");
            }

            Hostel hostel = existingHostel.get();
            if (hostel.getIsVerified()) {
                return ResponseEntity.badRequest().body("Hostel is already verified");
            }

            hostel.setIsVerified(true);
            Hostel updatedHostel = hostelService.save(hostel);
            return ResponseEntity.ok(updatedHostel);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error approving hostel: " + e.getMessage());
        }
    }

    // Delete a hostel
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteHostel(@PathVariable Long id) {
        try {
            Optional<Hostel> hostel = hostelService.findById(id);
            if (!hostel.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Hostel not found");
            }
            hostelService.deleteById(id);
            return ResponseEntity.ok("Hostel deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting hostel: " + e.getMessage());
        }
    }
 // Get hostels by price range
    @GetMapping("/price")
    public ResponseEntity<?> getHostelsByPriceRange(
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice) {
        try {
            List<Hostel> hostels;
            if (minPrice != null && maxPrice != null) {
                hostels = hostelService.findByPriceRange(minPrice, maxPrice);
          
            } else {
                return ResponseEntity.badRequest().body("At least one of minPrice or maxPrice is required");
            }
            return ResponseEntity.ok(hostels);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving hostels by price: " + e.getMessage());
        }
    }

    // Get hostels by manager ID
    @GetMapping("/manager/{managerId}")
    public ResponseEntity<?> getHostelsByManagerId(@PathVariable Long managerId) {
        try {
            List<Hostel> hostels = hostelService.findByManagerId(managerId);
            return ResponseEntity.ok(hostels);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving hostels: " + e.getMessage());
        }
    }

    private boolean isValidPhoneNumber(String phone) {
        return phone != null && phone.matches("^\\+254[0-9]{9}$");
    }
}