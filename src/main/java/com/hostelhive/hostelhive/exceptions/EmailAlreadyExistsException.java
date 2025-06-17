package com.hostelhive.hostelhive.exceptions;

/**
 * Exception thrown when trying to register with an email that already exists
 */
 public class EmailAlreadyExistsException extends RuntimeException {
    
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
    
    public EmailAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}