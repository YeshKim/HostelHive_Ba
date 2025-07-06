package com.hostelhive.hostelhive.repository;

import com.hostelhive.hostelhive.models.Amenity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AmenityRepo extends JpaRepository<Amenity, Long> {
    List<Amenity> findByNameIn(List<String> names);
}