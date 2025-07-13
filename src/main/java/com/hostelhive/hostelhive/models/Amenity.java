package com.hostelhive.hostelhive.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "amenities")

public class Amenity {
 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;
 
 private String name; // Make sure this field exists and has getter/setter
 
 // Getters and setters
 public String getName() { return name; }
 public void setName(String name) { this.name = name; }
}


