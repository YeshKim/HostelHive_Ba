package com.hostelhive.hostelhive.repository;

import com.hostelhive.hostelhive.models.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepo extends JpaRepository<Room, Long> {
    /**
     * Find rooms by hostel ID
     * @param hostelId the hostel ID to search for
     * @return List of rooms associated with the hostel
     */
    List<Room> findByHostelId(Long hostelId);

    /**
     * Find available rooms
     * @return List of rooms that are available
     */
    @Query("SELECT r FROM Room r WHERE r.isAvailable = true")
    List<Room> findAvailableRooms();

    /**
     * Find rooms by hostel ID and availability
     * @param hostelId the hostel ID
     * @param isAvailable the availability status
     * @return List of rooms matching the criteria
     */
    @Query("SELECT r FROM Room r WHERE r.hostelId = :hostelId AND r.isAvailable = :isAvailable")
    List<Room> findByHostelIdAndIsAvailable(@Param("hostelId") Long hostelId, @Param("isAvailable") Boolean isAvailable);
}