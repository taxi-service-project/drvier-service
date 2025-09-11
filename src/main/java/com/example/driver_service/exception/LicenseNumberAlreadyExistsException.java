package com.example.driver_service.exception;

public class LicenseNumberAlreadyExistsException extends RuntimeException {
    public LicenseNumberAlreadyExistsException(String message) {
        super(message);
    }
}
