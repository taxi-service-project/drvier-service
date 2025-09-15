package com.example.driver_service.dto;

import com.example.driver_service.entity.Driver;
import com.example.driver_service.entity.DriverStatus;

public record DriverProfileResponse(
        Long id,
        String email,
        String name,
        String phoneNumber,
        String licenseNumber,
        String profileImageUrl,
        DriverStatus status,
        Double ratingAvg
) {
    public static DriverProfileResponse fromEntity(Driver driver) {
        return new DriverProfileResponse(
                driver.getId(),
                driver.getEmail(),
                driver.getName(),
                driver.getPhoneNumber(),
                driver.getLicenseNumber(),
                driver.getProfileImageUrl(),
                driver.getStatus(),
                driver.getRatingAvg()
        );
    }
}