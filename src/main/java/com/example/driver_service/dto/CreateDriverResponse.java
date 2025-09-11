package com.example.driver_service.dto;

import com.example.driver_service.entity.Driver;
import com.example.driver_service.entity.DriverStatus;

public record CreateDriverResponse(
        Long id,
        String email,
        String name,
        DriverStatus status
) {
    public static CreateDriverResponse fromEntity(Driver driver) {
        return new CreateDriverResponse(
                driver.getId(),
                driver.getEmail(),
                driver.getName(),
                driver.getStatus()
        );
    }
}
