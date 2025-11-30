package com.example.driver_service.controller;

import com.example.driver_service.dto.request.DriverCreateRequest;
import com.example.driver_service.dto.request.UpdateDriverProfileRequest;
import com.example.driver_service.dto.request.UpdateDriverStatusRequest;
import com.example.driver_service.dto.request.UpdateVehicleRequest;
import com.example.driver_service.dto.response.CreateDriverResponse;
import com.example.driver_service.dto.response.DriverProfileResponse;
import com.example.driver_service.dto.response.VehicleResponse;
import com.example.driver_service.service.DriverService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;

    @PostMapping
    public Mono<ResponseEntity<CreateDriverResponse>> createDriver(@Valid @RequestBody DriverCreateRequest request) {
        return driverService.createDriver(request)
                            .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
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

    @PutMapping("/{driverId}/status")
    public ResponseEntity<Void> updateDriverStatus(
            @PathVariable Long driverId,
            @Valid @RequestBody UpdateDriverStatusRequest request) {

        driverService.updateDriverStatus(driverId, request);
        return ResponseEntity.noContent().build();
    }
}
