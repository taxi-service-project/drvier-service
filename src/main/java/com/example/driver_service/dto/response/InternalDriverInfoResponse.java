package com.example.driver_service.dto.response;

import com.example.driver_service.entity.Driver;
import com.example.driver_service.entity.Vehicle;

public record InternalDriverInfoResponse(
        String driverId,
        String name,
        Double ratingAvg,
        VehicleInfo vehicle
) {
    public record VehicleInfo(
            String licensePlate,
            String model
    ) {
        public static VehicleInfo fromEntity(Vehicle vehicle) {
            return new VehicleInfo(
                    vehicle.getLicensePlate(),
                    vehicle.getModel()
            );
        }
    }

    public static InternalDriverInfoResponse of(Driver driver, Vehicle vehicle) {
        return new InternalDriverInfoResponse(
                driver.getDriverId(),
                driver.getName(),
                driver.getRatingAvg(),
                VehicleInfo.fromEntity(vehicle)
        );
    }
}