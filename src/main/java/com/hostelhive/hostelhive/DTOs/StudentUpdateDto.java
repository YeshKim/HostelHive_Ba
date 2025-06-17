package com.hostelhive.hostelhive.DTOs;

import jakarta.validation.constraints.Size;

/**
 * DTO for Student Profile Update
 */
public class StudentUpdateDto {
	 @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
	    private String fullName;
	    
	    @Size(min = 10, max = 15, message = "Phone number must be between 10 and 15 characters")
	    private String phoneNumber;
	    
	    // Constructors
	    public StudentUpdateDto() {}
	    
	    public StudentUpdateDto(String fullName, String phoneNumber) {
	        this.fullName = fullName;
	        this.phoneNumber = phoneNumber;
	    }
	    
	    // Getters and Setters
	    public String getFullName() { return fullName; }
	    public void setFullName(String fullName) { this.fullName = fullName; }
	    
	    public String getPhoneNumber() { return phoneNumber; }
	    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
	}

