package com.hostelhive.hostelhive.DTOs;

public class RegisterUserDto {
    private String email;
    private String password;
    private String fullName;
    private String phoneNumber;
    private String role;

    // No-arg constructor
    public RegisterUserDto() {
    }

    // All-args constructor
    public RegisterUserDto(String email, String password, String fullName, String phoneNumber, String role) {
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }

    // Getters and Setters

    public String getEmail() {
        return email;
    }

    public RegisterUserDto setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public RegisterUserDto setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getFullName() {
        return fullName;
    }

    public RegisterUserDto setFullName(String fullName) {
        this.fullName = fullName;
        return this;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public RegisterUserDto setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public String getRole() {
        return role;
    }

    public RegisterUserDto setRole(String role) {
        this.role = role;
        return this;
    }
}
