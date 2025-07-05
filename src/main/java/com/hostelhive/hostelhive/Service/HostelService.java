package com.hostelhive.hostelhive.Service;

import com.hostelhive.hostelhive.models.Hostel;
import com.hostelhive.hostelhive.repository.HostelRepo;
import com.hostelhive.hostelhive.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class HostelService {
    private final HostelRepo hostelRepo;

    @Autowired
    public HostelService(HostelRepo hostelRepo) {
        this.hostelRepo = hostelRepo;
    }

    /**
     * Create a new hostel
     * @param hostel the hostel details
     * @return the created hostel
     * @throws IllegalArgumentException if hostel name already exists
     */
    public Hostel createHostel(Hostel hostel) {
        if (hostelRepo.existsByName(hostel.getName())) {
            throw new IllegalArgumentException("Hostel with name " + hostel.getName() + " already exists");
        }
        if (hostel.getImagesBase64() == null) {
            hostel.setImagesBase64(new ArrayList<>());
        }
        return hostelRepo.save(hostel);
    }

    /**
     * Get hostel by ID
     * @param hostelId the hostel ID
     * @return the hostel
     * @throws ResourceNotFoundException if hostel not found
     */
    @Transactional(readOnly = true)
    public Hostel getHostelById(Long hostelId) {
        return hostelRepo.findById(hostelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hostel not found with id: " + hostelId));
    }

    /**
     * Get all hostels
     * @return list of all hostels
     */
    @Transactional(readOnly = true)
    public List<Hostel> getAllHostels() {
        return hostelRepo.findAll();
    }

    /**
     * Update hostel details
     * @param hostelId the hostel ID
     * @param updatedHostel the updated hostel details
     * @return the updated hostel
     * @throws ResourceNotFoundException if hostel not found
     */
    public Hostel updateHostel(Long hostelId, Hostel updatedHostel) {
        Hostel hostel = hostelRepo.findById(hostelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hostel not found with id: " + hostelId));

        hostel.setName(updatedHostel.getName());
        hostel.setAddress(updatedHostel.getAddress());
        hostel.setLocation(updatedHostel.getLocation());
        hostel.setDescription(updatedHostel.getDescription());
        hostel.setAmenities(updatedHostel.getAmenities());
        hostel.setPricePerMonth(updatedHostel.getPricePerMonth());
        hostel.setManagerId(updatedHostel.getManagerId());
        hostel.setIsVerified(updatedHostel.getIsVerified());
        if (updatedHostel.getImagesBase64() != null) {
            hostel.setImagesBase64(updatedHostel.getImagesBase64());
        }

        return hostelRepo.save(hostel);
    }

    /**
     * Delete hostel
     * @param hostelId the hostel ID
     * @throws ResourceNotFoundException if hostel not found
     */
    public void deleteHostel(Long hostelId) {
        if (!hostelRepo.existsById(hostelId)) {
            throw new ResourceNotFoundException("Hostel not found with id: " + hostelId);
        }
        hostelRepo.deleteById(hostelId);
    }

    /**
     * Search hostels by location
     * @param location the location to search for
     * @return list of hostels matching the location
     */
    @Transactional(readOnly = true)
    public List<Hostel> searchHostelsByLocation(String location) {
        return hostelRepo.findByLocationContainingIgnoreCase(location);
    }

    /**
     * Get verified hostels
     * @return list of verified hostels
     */
    @Transactional(readOnly = true)
    public List<Hostel> getVerifiedHostels() {
        return hostelRepo.findVerifiedHostels();
    }

    /**
     * Get hostels by price range
     * @param minPrice the minimum price per month (optional)
     * @param maxPrice the maximum price per month (optional)
     * @return list of hostels within the specified price range
     */
    @Transactional(readOnly = true)
    public List<Hostel> getHostelsByPriceRange(Double minPrice, Double maxPrice) {
        if (minPrice == null && maxPrice == null) {
            return hostelRepo.findAll();
        } else if (minPrice == null) {
            return hostelRepo.findByPricePerMonthLessThanEqual(maxPrice);
        } else if (maxPrice == null) {
            return hostelRepo.findByPricePerMonthGreaterThanEqual(minPrice);
        } else {
            return hostelRepo.findByPricePerMonthBetween(minPrice, maxPrice);
        }
    }
}