package com.hostelhive.hostelhive.models;

import java.io.Serializable;

public class PaymentRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long bookingId;         // The ID of the booking to associate with the payment
    private String phoneNumber;     // The phone number to send the STK Push prompt to
    private double amount;          // The payment amount in Kenyan Shillings
    private String accountReference;// A reference for the transaction (e.g., "Booking_<id>")
    private String transactionDesc; // Description of the transaction (e.g., "Hostel Booking Payment")
    private String callbackUrl;     // URL for M-Pesa to send callback responses (optional, defaults to config)

    // Default constructor
    public PaymentRequest() {
        super();
    }

    // Parameterized constructor
    public PaymentRequest(Long bookingId, String phoneNumber, double amount) {
        this.bookingId = bookingId;
        this.phoneNumber = phoneNumber;
        this.amount = amount;
        this.accountReference = "Booking_" + (bookingId != null ? bookingId : "N/A");
        this.transactionDesc = "Hostel Booking Payment";
    }

    // Getters and Setters
    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
        if (bookingId != null) {
            this.accountReference = "Booking_" + bookingId;
        }
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

    public String getAccountReference() {
        return accountReference;
    }

    public void setAccountReference(String accountReference) {
        this.accountReference = accountReference;
    }

    public String getTransactionDesc() {
        return transactionDesc;
    }

    public void setTransactionDesc(String transactionDesc) {
        this.transactionDesc = transactionDesc;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    @Override
    public String toString() {
        return "PaymentRequest{" +
                "bookingId=" + bookingId +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", amount=" + amount +
                ", accountReference='" + accountReference + '\'' +
                ", transactionDesc='" + transactionDesc + '\'' +
                ", callbackUrl='" + callbackUrl + '\'' +
                '}';
    }
}