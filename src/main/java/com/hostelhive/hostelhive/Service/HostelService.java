package com.hostelhive.hostelhive.Service;

import com.hostelhive.hostelhive.models.Hostel;
import com.hostelhive.hostelhive.models.Room;
import com.hostelhive.hostelhive.repository.HostelRepo;
import com.hostelhive.hostelhive.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class HostelService {

    private final HostelRepo hostelRepo;
    private final RoomService roomService;

    @Autowired
    public HostelService(HostelRepo hostelRepo, RoomService roomService) {
        this.hostelRepo = hostelRepo;
        this.roomService = roomService;
    }

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
        if (hostel.getName() == null || hostel.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Hostel name is required");
        }
        if (hostel.getTotalRooms() == null || hostel.getTotalRooms() <= 0) {
            throw new IllegalArgumentException("Total rooms must be greater than 0");
        }
        if (hostel.getAvailableRooms() != null && hostel.getAvailableRooms() > hostel.getTotalRooms()) {
            throw new IllegalArgumentException("Available rooms cannot exceed total rooms");
        }

        // Save the hostel first to get its ID
        Hostel savedHostel = hostelRepo.save(hostel);

        // Create rooms based on totalRooms and availableRooms
        int totalRooms = savedHostel.getTotalRooms();
        int availableRooms = savedHostel.getAvailableRooms() != null ? savedHostel.getAvailableRooms() : 0;

        // Delete existing rooms if updating an existing hostel
        if (savedHostel.getId() != null) {
            List<Room> existingRooms = roomService.getRoomsByHostelId(savedHostel.getId());
            for (Room room : existingRooms) {
                roomService.deleteRoom(room.getId()); // Fixed syntax: Added parentheses and parameter
            }
        }

        // Create new rooms
        for (int i = 1; i <= totalRooms; i++) {
            Room room = new Room();
            room.setHostelId(savedHostel.getId());
            room.setRoomNumber("Room " + i);
            room.setRoomType(savedHostel.getRoomType() != null ? savedHostel.getRoomType() : "single");
            room.setIsAvailable(i <= availableRooms);
            room.setPrice(savedHostel.getPricePerMonth() != null ? savedHostel.getPricePerMonth() : 0.0);
            roomService.createRoom(room);
        }

        return savedHostel;
    }

    public void deleteById(Long id) {
        if (!hostelRepo.existsById(id)) {
            throw new ResourceNotFoundException("Hostel not found with id: " + id);
        }
        // Delete associated rooms
        List<Room> rooms = roomService.getRoomsByHostelId(id);
        for (Room room : rooms) {
            roomService.deleteRoom(room.getId()); // Fixed syntax: Added parentheses and parameter
        }
        hostelRepo.deleteById(id);
    }

    public boolean bookRoom(Long hostelId) {
        Optional<Hostel> hostelOpt = findById(hostelId);
        if (hostelOpt.isPresent()) {
            Hostel hostel = hostelOpt.get();
            if (hostel.getAvailableRooms() != null && hostel.getAvailableRooms() > 0) {
                hostel.setAvailableRooms(hostel.getAvailableRooms() - 1);
                // Update room availability
                List<Room> availableRooms = roomService.getRoomsByHostelIdAndAvailability(hostelId, true);
                if (!availableRooms.isEmpty()) {
                    Room roomToBook = availableRooms.get(0); // Book the first available room
                    roomToBook.setIsAvailable(false);
                    roomService.updateRoom(roomToBook.getId(), roomToBook);
                }
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
                // Update room availability
                List<Room> unavailableRooms = roomService.getRoomsByHostelIdAndAvailability(hostelId, false);
                if (!unavailableRooms.isEmpty()) {
                    Room roomToRelease = unavailableRooms.get(0); // Release the first unavailable room
                    roomToRelease.setIsAvailable(true);
                    roomService.updateRoom(roomToRelease.getId(), roomToRelease);
                }
                save(hostel);
                return true;
            }
        }
        return false;
    }

    public List<Hostel> getHostelsByPriceRange(Double minPrice, Double maxPrice) {
        return hostelRepo.findByPricePerMonthBetween(minPrice, maxPrice);
    }

    public List<Hostel> getHostelsByRoomType(List<String> roomTypes) {
        return hostelRepo.findByRoomTypeIn(roomTypes);
    }

    public List<Hostel> getHostelsByAmenities(List<String> amenities) {
        if (amenities == null || amenities.isEmpty()) {
            return hostelRepo.findAll();
        }
        return hostelRepo.findByAmenitiesContainingAll(amenities, (long) amenities.size());
    }

    public List<Hostel> getHostelsByDistanceRange(Double minDistance, Double maxDistance) {
        return hostelRepo.findByDistanceBetween(minDistance, maxDistance);
    }

    public List<Hostel> getHostelsByFilters(Double minPrice, Double maxPrice, List<String> roomTypes,
                                            List<String> amenities, Double minDistance, Double maxDistance,
                                            String location) {
        return hostelRepo.findByFilters(
                minPrice != null ? minPrice : null,
                maxPrice != null ? maxPrice : null,
                roomTypes != null && !roomTypes.isEmpty() ? roomTypes : null,
                amenities != null && !amenities.isEmpty() ? amenities : null,
                amenities != null && !amenities.isEmpty() ? (long) amenities.size() : null,
                minDistance != null ? minDistance : null,
                maxDistance != null ? maxDistance : null,
                location
        );
    }

    public List<Hostel> findByFilters(Double minPrice, Double maxPrice, List<String> roomTypes,
                                      List<String> amenities, Long amenitiesSize,
                                      Double minDistance, Double maxDistance, String location) {
        return hostelRepo.findByFilters(minPrice, maxPrice, roomTypes, amenities,
                amenitiesSize, minDistance, maxDistance, location);
    }
}