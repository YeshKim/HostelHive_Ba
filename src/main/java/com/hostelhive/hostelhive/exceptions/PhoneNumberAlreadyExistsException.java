package com.hostelhive.hostelhive.exceptions;

/**
 * Exception thrown when trying to register with a phone number that already exists
 */
public class PhoneNumberAlreadyExistsException extends RuntimeException {
    
    public PhoneNumberAlreadyExistsException(String message) {
        super(message);
    }
    
    public PhoneNumberAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}