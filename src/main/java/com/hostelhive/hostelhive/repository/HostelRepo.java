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
    /**
     * Find hostel by name
     * @param name the name to search for
     * @return Optional containing the hostel if found
     */
    Optional<Hostel> findByName(String name);

    /**
     * Find hostels by location
     * @param location the location to search for
     * @return List of hostels matching the location
     */
    @Query("SELECT h FROM Hostel h WHERE LOWER(h.location) LIKE LOWER(CONCAT('%', :location, '%'))")
    List<Hostel> findByLocationContainingIgnoreCase(@Param("location") String location);

    /**
     * Find hostels by manager ID
     * @param managerId the manager ID to search for
     * @return List of hostels managed by the manager
     */
    List<Hostel> findByManagerId(Long managerId);

    /**
     * Check if hostel exists by name
     * @param name the name to check
     * @return true if hostel exists, false otherwise
     */
    boolean existsByName(String name);

    /**
     * Find verified hostels
     * @return List of verified hostels
     */
    @Query("SELECT h FROM Hostel h WHERE h.isVerified = true")
    List<Hostel> findVerifiedHostels();

    /**
     * Find hostels with price per month less than or equal to the specified value
     * @param maxPrice the maximum price per month
     * @return List of hostels with price per month less than or equal to maxPrice
     */
    List<Hostel> findByPricePerMonthLessThanEqual(Double maxPrice);

    /**
     * Find hostels with price per month greater than or equal to the specified value
     * @param minPrice the minimum price per month
     * @return List of hostels with price per month greater than or equal to minPrice
     */
    List<Hostel> findByPricePerMonthGreaterThanEqual(Double minPrice);

    /**
     * Find hostels with price per month within the specified range
     * @param minPrice the minimum price per month
     * @param maxPrice the maximum price per month
     * @return List of hostels with price per month between minPrice and maxPrice
     */
    List<Hostel> findByPricePerMonthBetween(Double minPrice, Double maxPrice);
}