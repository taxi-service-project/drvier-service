package com.example.driver_service.dto;

import com.example.driver_service.validation.DriverStatusValue;
import jakarta.validation.constraints.NotBlank;

public record UpdateDriverStatusRequest(
        @NotBlank
        @DriverStatusValue // Custom Validator 적용
        String status
) {
}