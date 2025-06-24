package com.hostelhive.hostelhive.Service;

import com.hostelhive.hostelhive.models.Room;
import com.hostelhive.hostelhive.repository.RoomRepo;
import com.hostelhive.hostelhive.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RoomService {
    private final RoomRepo roomRepo;

    @Autowired
    public RoomService(RoomRepo roomRepo) {
        this.roomRepo = roomRepo;
    }

    /**
     * Create a new room
     * @param room the room details
     * @return the created room
     * @throws IllegalArgumentException if hostel ID is invalid
     */
    public Room createRoom(Room room) {
        if (room.getHostelId() == null) {
            throw new IllegalArgumentException("Hostel ID is required");
        }
        return roomRepo.save(room);
    }

    /**
     * Get room by ID
     * @param roomId the room ID
     * @return the room
     * @throws ResourceNotFoundException if room not found
     */
    @Transactional(readOnly = true)
    public Room getRoomById(Long roomId) {
        return roomRepo.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + roomId));
    }

    /**
     * Get all rooms
     * @return list of all rooms
     */
    @Transactional(readOnly = true)
    public List<Room> getAllRooms() {
        return roomRepo.findAll();
    }

    /**
     * Update room details
     * @param roomId the room ID
     * @param updatedRoom the updated room details
     * @return the updated room
     * @throws ResourceNotFoundException if room not found
     */
    public Room updateRoom(Long roomId, Room updatedRoom) {
        Room room = roomRepo.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + roomId));

        room.setHostelId(updatedRoom.getHostelId());
        room.setRoomNumber(updatedRoom.getRoomNumber());
        room.setRoomType(updatedRoom.getRoomType());
        room.setIsAvailable(updatedRoom.getIsAvailable());
        room.setPrice(updatedRoom.getPrice());

        return roomRepo.save(room);
    }

    /**
     * Delete room
     * @param roomId the room ID
     * @throws ResourceNotFoundException if room not found
     */
    public void deleteRoom(Long roomId) {
        if (!roomRepo.existsById(roomId)) {
            throw new ResourceNotFoundException("Room not found with id: " + roomId);
        }
        roomRepo.deleteById(roomId);
    }

    /**
     * Get rooms by hostel ID
     * @param hostelId the hostel ID
     * @return list of rooms for the hostel
     */
    @Transactional(readOnly = true)
    public List<Room> getRoomsByHostelId(Long hostelId) {
        return roomRepo.findByHostelId(hostelId);
    }

    /**
     * Get available rooms
     * @return list of available rooms
     */
    @Transactional(readOnly = true)
    public List<Room> getAvailableRooms() {
        return roomRepo.findAvailableRooms();
    }

    /**
     * Get rooms by hostel ID and availability
     * @param hostelId the hostel ID
     * @param isAvailable the availability status
     * @return list of rooms matching the criteria
     */
    @Transactional(readOnly = true)
    public List<Room> getRoomsByHostelIdAndAvailability(Long hostelId, Boolean isAvailable) {
        return roomRepo.findByHostelIdAndIsAvailable(hostelId, isAvailable);
    }
}