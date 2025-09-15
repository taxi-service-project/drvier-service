package com.example.driver_service.dto;

import com.example.driver_service.entity.Driver;
import com.example.driver_service.entity.Vehicle;

public record InternalDriverInfoResponse(
        Long id,
        String name,
        Double ratingAvg,
        VehicleInfo vehicle
) {
    // 내부 정적 레코드로 차량 정보 표현
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
                driver.getId(),
                driver.getName(),
                driver.getRatingAvg(),
                VehicleInfo.fromEntity(vehicle)
        );
    }
}