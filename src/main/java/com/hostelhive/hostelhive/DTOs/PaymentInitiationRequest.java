package com.hostelhive.hostelhive.DTOs;

public class PaymentInitiationRequest {
    private Long bookingId;
    private String phoneNumber;
    private double amount;

    // Default constructor
    public PaymentInitiationRequest() {}

    // Getters and Setters
    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}