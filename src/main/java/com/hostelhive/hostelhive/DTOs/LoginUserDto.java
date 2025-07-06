package com.hostelhive.hostelhive.DTOs;

public class LoginUserDto {
    private String email;
    private String password;

    // No-arg constructor
    public LoginUserDto() {
    }

    // All-args constructor
    public LoginUserDto(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Getter and Setter for email
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Getter and Setter for password
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
