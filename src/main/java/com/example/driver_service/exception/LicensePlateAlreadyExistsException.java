package com.example.driver_service.exception;

public class LicensePlateAlreadyExistsException extends RuntimeException {
    public LicensePlateAlreadyExistsException(String message) {
        super(message);
    }
}
