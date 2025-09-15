package com.example.driver_service.controller;

import com.example.driver_service.dto.*;
import com.example.driver_service.service.DriverService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;

    @PostMapping
    public ResponseEntity<CreateDriverResponse> createDriver(@Valid @RequestBody DriverCreateRequest request) {
        CreateDriverResponse response = driverService.createDriver(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{driverId}")
    public ResponseEntity<DriverProfileResponse> getDriverProfile(@PathVariable Long driverId) {
        DriverProfileResponse response = driverService.getDriverProfile(driverId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{driverId}")
    public ResponseEntity<DriverProfileResponse> updateDriverProfile(
            @PathVariable Long driverId,
            @Valid @RequestBody UpdateDriverProfileRequest request) {

        DriverProfileResponse response = driverService.updateDriverProfile(driverId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{driverId}/vehicle")
    public ResponseEntity<VehicleResponse> getVehicle(@PathVariable Long driverId) {
        VehicleResponse response = driverService.getVehicleByDriverId(driverId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{driverId}/vehicle")
    public ResponseEntity<VehicleResponse> updateVehicle(
            @PathVariable Long driverId,
            @Valid @RequestBody UpdateVehicleRequest request) {

        VehicleResponse response = driverService.updateVehicleInfo(driverId, request);
        return ResponseEntity.ok(response);
    }
}
