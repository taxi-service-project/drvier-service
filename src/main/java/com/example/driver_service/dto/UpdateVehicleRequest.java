package com.example.driver_service.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateVehicleRequest(
        @NotBlank(message = "차량 모델명은 필수입니다.")
        String model,

        @NotBlank(message = "차량 색상은 필수입니다.")
        String color
) {
}