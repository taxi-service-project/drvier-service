package com.example.driver_service.repository;

import com.example.driver_service.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    boolean existsByLicensePlate(String licensePlate);

    Optional<Vehicle> findByDriverId(Long driverId);

}
