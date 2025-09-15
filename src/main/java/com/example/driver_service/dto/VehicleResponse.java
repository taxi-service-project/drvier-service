package com.example.driver_service.dto;

import com.example.driver_service.entity.Vehicle;

public record VehicleResponse(
        Long id,
        String licensePlate,
        String model,
        String color
) {
    public static VehicleResponse fromEntity(Vehicle vehicle) {
        return new VehicleResponse(
                vehicle.getId(),
                vehicle.getLicensePlate(),
                vehicle.getModel(),
                vehicle.getColor()
        );
    }
}