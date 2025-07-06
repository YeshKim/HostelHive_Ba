package com.hostelhive.hostelhive.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hostels")
public class Hostel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Column(name = "name", nullable = false)
    private String name;

    @NotBlank(message = "Address is required")
    @Column(name = "address", nullable = false)
    private String address;

    @NotBlank(message = "Location is required")
    @Column(name = "location", nullable = false)
    private String location;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @ManyToMany
    @JoinTable(
        name = "hostel_amenities",
        joinColumns = @JoinColumn(name = "hostel_id"),
        inverseJoinColumns = @JoinColumn(name = "amenity_id")
    )
    private List<Amenity> amenities = new ArrayList<>();

    @Column(name = "price_per_month", nullable = false)
    private Double pricePerMonth;

    @Column(name = "manager_id", nullable = false)
    private Long managerId;

    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @ElementCollection
    @Column(name = "images_base64", columnDefinition = "text")
    private List<String> imagesBase64 = new ArrayList<>();

    @Column(name = "property_type")
    private String propertyType;

    @Column(name = "total_rooms")
    private Integer totalRooms;

    @Column(name = "available_rooms")
    private Integer availableRooms;

    @Column(name = "room_type")
    private String roomType;

    @Column(name = "deposit_amount")
    private Double depositAmount;

    @Column(name = "contact_phone")
    private String contactPhone;

    @Column(name = "alternate_phone")
    private String alternatePhone;

    @Column(name = "contact_email")
    private String contactEmail;

    // New field for distance
    @Column(name = "distance")
    private Double distance; // Distance in kilometers to a reference point (e.g., university)

    // Default constructor
    public Hostel() {}

    // Constructor with required fields
    public Hostel(String name, String address, String location, Double pricePerMonth, Long managerId) {
        this.name = name;
        this.address = address;
        this.location = location;
        this.pricePerMonth = pricePerMonth;
        this.managerId = managerId;
    }

    // Getters and Setters (existing ones unchanged)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String get_Address() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Amenity> getAmenities() {
        return amenities;
    }

    public void setAmenities(List<Amenity> amenities) {
        this.amenities = amenities;
    }

    public Double getPricePerMonth() {
        return pricePerMonth;
    }

    public void setPricePerMonth(Double pricePerMonth) {
        this.pricePerMonth = pricePerMonth;
    }

    public Long getManagerId() {
        return managerId;
    }

    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }

    public Boolean getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<String> getImagesBase64() {
        return imagesBase64;
    }

    public void setImagesBase64(List<String> imagesBase64) {
        this.imagesBase64 = imagesBase64;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public Integer getTotalRooms() {
        return totalRooms;
    }

    public void setTotalRooms(Integer totalRooms) {
        this.totalRooms = totalRooms;
    }

    public Integer getAvailableRooms() {
        return availableRooms;
    }

    public void setAvailableRooms(Integer availableRooms) {
        this.availableRooms = availableRooms;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public Double getDepositAmount() {
        return depositAmount;
    }

    public void setDepositAmount(Double depositAmount) {
        this.depositAmount = depositAmount;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getAlternatePhone() {
        return alternatePhone;
    }

    public void setAlternatePhone(String alternatePhone) {
        this.alternatePhone = alternatePhone;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    // New getter and setter for distance
    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "Hostel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", location='" + location + '\'' +
                ", propertyType='" + propertyType + '\'' +
                ", totalRooms=" + totalRooms +
                ", availableRooms=" + availableRooms +
                ", roomType='" + roomType + '\'' +
                ", pricePerMonth=" + pricePerMonth +
                ", depositAmount=" + depositAmount +
                ", contactPhone='" + contactPhone + '\'' +
                ", contactEmail='" + contactEmail + '\'' +
                ", managerId=" + managerId +
                ", isVerified=" + isVerified +
                ", createdAt=" + createdAt +
                ", distance=" + distance +
                ", imagesBase64=" + (imagesBase64 != null ? imagesBase64.size() + " images" : "null") +
                '}';
    }
}