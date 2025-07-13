package com.hostelhive.hostelhive.Service;

import com.hostelhive.hostelhive.models.Amenity;
import com.hostelhive.hostelhive.repository.AmenityRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AmenityService {

    @Autowired
    private AmenityRepo amenityRepo;

    public List<Amenity> getAllAmenities() {
        return amenityRepo.findAll();
    }

    public List<Amenity> findByNames(List<String> names) {
        return amenityRepo.findByNameIn(names);
    }

	public static List<Amenity> findAll() {
		// TODO Auto-generated method stub
		return null;
	}
}