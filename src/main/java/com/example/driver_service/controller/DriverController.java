package com.example.driver_service.controller;

import com.example.driver_service.dto.CreateDriverResponse;
import com.example.driver_service.dto.DriverCreateRequest;
import com.example.driver_service.service.DriverService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
