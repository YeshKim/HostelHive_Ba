package com.hostelhive.hostelhive.repository;

import com.hostelhive.hostelhive.models.Hostel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HostelRepo extends JpaRepository<Hostel, Long> {
    
    // Existing methods
    List<Hostel> findByManagerId(Long managerId);
    List<Hostel> findByLocation(String location);
    List<Hostel> findByIsVerifiedTrue();
    List<Hostel> findByPricePerMonthBetween(Double minPrice, Double maxPrice);
    
    // New methods for additional fields
    List<Hostel> findByRoomType(String roomType);
    List<Hostel> findByPropertyType(String propertyType);
    List<Hostel> findByAvailableRoomsGreaterThan(Integer availableRooms);
    List<Hostel> findByTotalRoomsGreaterThanEqual(Integer totalRooms);
    
    // Combined search methods
    List<Hostel> findByLocationAndRoomType(String location, String roomType);
    List<Hostel> findByLocationAndPricePerMonthLessThanEqual(String location, Double maxPrice);
    List<Hostel> findByRoomTypeAndPricePerMonthBetween(String roomType, Double minPrice, Double maxPrice);
    
    // Custom queries
    @Query("SELECT h FROM Hostel h WHERE h.isVerified = true AND h.availableRooms > 0")
    List<Hostel> findAvailableVerifiedHostels();
    
    @Query("SELECT h FROM Hostel h WHERE h.location = :location AND h.pricePerMonth <= :maxPrice AND h.availableRooms > 0")
    List<Hostel> findAvailableHostelsByLocationAndMaxPrice(@Param("location") String location, @Param("maxPrice") Double maxPrice);
    
    @Query("SELECT h FROM Hostel h WHERE h.amenities LIKE %:amenity%")
    List<Hostel> findByAmenityContaining(@Param("amenity") String amenity);
    
    @Query("SELECT h FROM Hostel h WHERE h.contactEmail = :email")
    Optional<Hostel> findByContactEmail(@Param("email") String email);
    
    @Query("SELECT h FROM Hostel h WHERE h.contactPhone = :phone")
    Optional<Hostel> findByContactPhone(@Param("phone") String phone);
}