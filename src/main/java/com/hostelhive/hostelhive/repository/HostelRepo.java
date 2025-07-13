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

    // Existing methods (unchanged)
    List<Hostel> findByManagerId(Long managerId);
    List<Hostel> findByLocation(String location);
    List<Hostel> findByIsVerifiedTrue();
    List<Hostel> findByPricePerMonthBetween(Double minPrice, Double maxPrice);
    List<Hostel> findByRoomType(String roomType);
    List<Hostel> findByPropertyType(String propertyType);
    List<Hostel> findByAvailableRoomsGreaterThan(Integer availableRooms);
    List<Hostel> findByTotalRoomsGreaterThanEqual(Integer totalRooms);
    List<Hostel> findByLocationAndRoomType(String location, String roomType);
    List<Hostel> findByLocationAndPricePerMonthLessThanEqual(String location, Double maxPrice);
    List<Hostel> findByRoomTypeAndPricePerMonthBetween(String roomType, Double minPrice, Double maxPrice);

    @Query("SELECT h FROM Hostel h WHERE h.isVerified = true AND h.availableRooms > 0")
    List<Hostel> findAvailableVerifiedHostels();

    @Query("SELECT h FROM Hostel h WHERE h.location = :location AND h.pricePerMonth <= :maxPrice AND h.availableRooms > 0")
    List<Hostel> findAvailableHostelsByLocationAndMaxPrice(@Param("location") String location, @Param("maxPrice") Double maxPrice);

    @Query("SELECT h FROM Hostel h WHERE h.contactEmail = :email")
    Optional<Hostel> findByContactEmail(@Param("email") String email);

    @Query("SELECT h FROM Hostel h WHERE h.contactPhone = :phone")
    Optional<Hostel> findByContactPhone(@Param("phone") String phone);

    // Updated methods for normalized amenities
    @Query("SELECT h FROM Hostel h JOIN h.amenities a WHERE a.name = :amenity")
    List<Hostel> findByAmenity(@Param("amenity") String amenity);

    @Query("SELECT h FROM Hostel h JOIN h.amenities a WHERE a.name IN :amenities " +
           "GROUP BY h HAVING COUNT(DISTINCT a) = :amenitiesSize")
    List<Hostel> findByAmenitiesContainingAll(@Param("amenities") List<String> amenities,
                                             @Param("amenitiesSize") Long amenitiesSize);

    // Existing methods for other filters
    List<Hostel> findByRoomTypeIn(List<String> roomTypes);

    List<Hostel> findByDistanceBetween(Double minDistance, Double maxDistance);

    // Updated combined filter query with case-insensitive location search
    @Query("SELECT h FROM Hostel h JOIN h.amenities a WHERE " +
           "(:minPrice IS NULL OR h.pricePerMonth >= :minPrice) AND " +
           "(:maxPrice IS NULL OR h.pricePerMonth <= :maxPrice) AND " +
           "(:roomTypes IS NULL OR h.roomType IN :roomTypes) AND " +
           "(:minDistance IS NULL OR h.distance >= :minDistance) AND " +
           "(:maxDistance IS NULL OR h.distance <= :maxDistance) AND " +
           "(:location IS NULL OR LOWER(h.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
           "(:amenities IS NULL OR a.name IN :amenities) " +
           "GROUP BY h HAVING (:amenities IS NULL OR COUNT(DISTINCT a) = :amenitiesSize)")
    List<Hostel> findByFilters(@Param("minPrice") Double minPrice,
                               @Param("maxPrice") Double maxPrice,
                               @Param("roomTypes") List<String> roomTypes,
                               @Param("amenities") List<String> amenities,
                               @Param("amenitiesSize") Long amenitiesSize,
                               @Param("minDistance") Double minDistance,
                               @Param("maxDistance") Double maxDistance,
                               @Param("location") String location);
}