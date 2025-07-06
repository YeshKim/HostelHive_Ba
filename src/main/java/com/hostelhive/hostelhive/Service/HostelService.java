package com.hostelhive.hostelhive.Service;

import com.hostelhive.hostelhive.models.Hostel;
import com.hostelhive.hostelhive.models.User;
import com.hostelhive.hostelhive.repository.HostelRepo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HostelService {

    @Autowired
    private HostelRepo hostelRepo;

    public List<Hostel> findAll() {
        return hostelRepo.findAll();
    }

    public Optional<Hostel> findById(Long id) {
        return hostelRepo.findById(id);
    }

    public List<Hostel> findByManagerId(Long managerId) {
        return hostelRepo.findByManagerId(managerId);
    }

    public List<Hostel> findByLocation(String location) {
        return hostelRepo.findByLocation(location);
    }

    public List<Hostel> findByRoomType(String roomType) {
        return hostelRepo.findByRoomType(roomType);
    }

    public List<Hostel> findByPriceRange(Double minPrice, Double maxPrice) {
        return hostelRepo.findByPricePerMonthBetween(minPrice, maxPrice);
    }

    public List<Hostel> findAvailableHostels() {
        return hostelRepo.findByAvailableRoomsGreaterThan(0);
    }

    public List<Hostel> findVerifiedHostels() {
        return hostelRepo.findByIsVerifiedTrue();
    }

    public Hostel save(Hostel hostel) {
        return hostelRepo.save(hostel);
    }

    public void deleteById(Long id) {
        hostelRepo.deleteById(id);
    }

    // Business logic methods
    public boolean bookRoom(Long hostelId) {
        Optional<Hostel> hostelOpt = findById(hostelId);
        if (hostelOpt.isPresent()) {
            Hostel hostel = hostelOpt.get();
            if (hostel.getAvailableRooms() != null && hostel.getAvailableRooms() > 0) {
                hostel.setAvailableRooms(hostel.getAvailableRooms() - 1);
                save(hostel);
                return true;
            }
        }
        return false;
    }

    public boolean releaseRoom(Long hostelId) {
        Optional<Hostel> hostelOpt = findById(hostelId);
        if (hostelOpt.isPresent()) {
            Hostel hostel = hostelOpt.get();
            if (hostel.getAvailableRooms() != null && hostel.getTotalRooms() != null && 
                hostel.getAvailableRooms() < hostel.getTotalRooms()) {
                hostel.setAvailableRooms(hostel.getAvailableRooms() + 1);
                save(hostel);
                return true;
            }
        }
        return false;
    }
}