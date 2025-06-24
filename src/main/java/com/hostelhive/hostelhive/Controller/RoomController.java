package com.hostelhive.hostelhive.Controller;

import com.hostelhive.hostelhive.models.Room;
import com.hostelhive.hostelhive.Service.RoomService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {
    private final RoomService roomService;

    @Autowired
    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    // Create a new room
    @PostMapping
    public ResponseEntity<Room> createRoom(@Valid @RequestBody Room room) {
        Room createdRoom = roomService.createRoom(room);
        return new ResponseEntity<>(createdRoom, HttpStatus.CREATED);
    }

    // Get room by ID
    @GetMapping("/{id}")
    public ResponseEntity<Room> getRoomById(@PathVariable Long id) {
        Room room = roomService.getRoomById(id);
        return new ResponseEntity<>(room, HttpStatus.OK);
    }

    // Get all rooms
    @GetMapping
    public ResponseEntity<List<Room>> getAllRooms() {
        List<Room> rooms = roomService.getAllRooms();
        return new ResponseEntity<>(rooms, HttpStatus.OK);
    }

    // Update room
    @PutMapping("/{id}")
    public ResponseEntity<Room> updateRoom(@PathVariable Long id, @Valid @RequestBody Room room) {
        Room updatedRoom = roomService.updateRoom(id, room);
        return new ResponseEntity<>(updatedRoom, HttpStatus.OK);
    }

    // Delete room
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Get rooms by hostel ID
    @GetMapping("/by-hostel/{hostelId}")
    public ResponseEntity<List<Room>> getRoomsByHostelId(@PathVariable Long hostelId) {
        List<Room> rooms = roomService.getRoomsByHostelId(hostelId);
        return new ResponseEntity<>(rooms, HttpStatus.OK);
    }

    // Get available rooms
    @GetMapping("/available")
    public ResponseEntity<List<Room>> getAvailableRooms() {
        List<Room> rooms = roomService.getAvailableRooms();
        return new ResponseEntity<>(rooms, HttpStatus.OK);
    }

    // Get rooms by hostel ID and availability
    @GetMapping("/by-hostel/{hostelId}/availability")
    public ResponseEntity<List<Room>> getRoomsByHostelIdAndAvailability(
            @PathVariable Long hostelId,
            @RequestParam Boolean isAvailable) {
        List<Room> rooms = roomService.getRoomsByHostelIdAndAvailability(hostelId, isAvailable);
        return new ResponseEntity<>(rooms, HttpStatus.OK);
    }
}