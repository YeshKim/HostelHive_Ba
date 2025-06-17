package com.hostelhive.hostelhive.DTOs;

import java.time.LocalDateTime;

/**
 * DTO for Student Response (without sensitive data)
 */
public class StudentResponseDto {
	 private Long id;
	    private String fullName;
	    private String email;
	    private String phoneNumber;
	    private LocalDateTime registeredAt;
	    
	    // Constructors
	    public StudentResponseDto() {}
	    
	    public StudentResponseDto(Long id, String fullName, String email, String phoneNumber, LocalDateTime registeredAt) {
	        this.id = id;
	        this.fullName = fullName;
	        this.email = email;
	        this.phoneNumber = phoneNumber;
	        this.registeredAt = registeredAt;
	    }
	    
	    // Getters and Setters
	    public Long getId() { return id; }
	    public void setId(Long id) { this.id = id; }
	    
	    public String getFullName() { return fullName; }
	    public void setFullName(String fullName) { this.fullName = fullName; }
	    
	    public String getEmail() { return email; }
	    public void setEmail(String email) { this.email = email; }
	    
	    public String getPhoneNumber() { return phoneNumber; }
	    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
	    
	    public LocalDateTime getRegisteredAt() { return registeredAt; }
	    public void setRegisteredAt(LocalDateTime registeredAt) { this.registeredAt = registeredAt; }
	}

