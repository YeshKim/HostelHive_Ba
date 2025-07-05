package com.hostelhive.hostelhive.Controller;

import com.hostelhive.hostelhive.models.Hostel;
import com.hostelhive.hostelhive.Service.HostelService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hostels")
public class HostelController {
    private final HostelService hostelService;

    @Autowired
    public HostelController(HostelService hostelService) {
        this.hostelService = hostelService;
    }

    @PostMapping("/post-hostel")
    public ResponseEntity<Hostel> createHostel(@Valid @RequestBody Hostel hostel) {
        Hostel createdHostel = hostelService.createHostel(hostel);
        return new ResponseEntity<>(createdHostel, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Hostel> getHostelById(@PathVariable Long id) {
        Hostel hostel = hostelService.getHostelById(id);
        return new ResponseEntity<>(hostel, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Hostel>> getAllHostels() {
        List<Hostel> hostels = hostelService.getAllHostels();
        return new ResponseEntity<>(hostels, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Hostel> updateHostel(@PathVariable Long id, @Valid @RequestBody Hostel hostel) {
        Hostel updatedHostel = hostelService.updateHostel(id, hostel);
        return new ResponseEntity<>(updatedHostel, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHostel(@PathVariable Long id) {
        hostelService.deleteHostel(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Hostel>> searchHostelsByLocation(@RequestParam String location) {
        List<Hostel> hostels = hostelService.searchHostelsByLocation(location);
        return new ResponseEntity<>(hostels, HttpStatus.OK);
    }

    @GetMapping("/verified")
    public ResponseEntity<List<Hostel>> getVerifiedHostels() {
        List<Hostel> hostels = hostelService.getVerifiedHostels();
        return new ResponseEntity<>(hostels, HttpStatus.OK);
    }

    @GetMapping("/price")
    public ResponseEntity<List<Hostel>> getHostelsByPriceRange(
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice) {
        List<Hostel> hostels = hostelService.getHostelsByPriceRange(minPrice, maxPrice);
        return new ResponseEntity<>(hostels, HttpStatus.OK);
    }
}