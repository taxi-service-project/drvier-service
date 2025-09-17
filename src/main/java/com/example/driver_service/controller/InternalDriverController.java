package com.example.driver_service.controller;

import com.example.driver_service.dto.response.InternalDriverInfoResponse;
import com.example.driver_service.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/api/drivers")
@RequiredArgsConstructor
public class InternalDriverController {

    private final DriverService driverService;

    @GetMapping("/{driverId}")
    public ResponseEntity<InternalDriverInfoResponse> getDriverInfoForInternal(@PathVariable String driverId) {
        InternalDriverInfoResponse response = driverService.getInternalDriverInfo(driverId);
        return ResponseEntity.ok(response);
    }
}