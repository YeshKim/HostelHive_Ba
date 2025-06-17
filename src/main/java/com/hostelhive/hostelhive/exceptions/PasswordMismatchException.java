package com.hostelhive.hostelhive.exceptions;

/**
 * Exception thrown when passwords don't match during registration
 */
public class PasswordMismatchException extends RuntimeException {
    
    public PasswordMismatchException(String message) {
        super(message);
    }
    
    public PasswordMismatchException(String message, Throwable cause) {
        super(message, cause);
    }
}