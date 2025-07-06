package com.hostelhive.hostelhive.Controller;

import com.hostelhive.hostelhive.models.Amenity;
import com.hostelhive.hostelhive.Service.AmenityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/amenities")
@CrossOrigin(origins = "*")
public class AmenityController {

    @Autowired
    private AmenityService amenityService;

    @GetMapping
    public ResponseEntity<List<Amenity>> getAllAmenities() {
        try {
            List<Amenity> amenities = amenityService.getAllAmenities();
            return ResponseEntity.ok(amenities);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}