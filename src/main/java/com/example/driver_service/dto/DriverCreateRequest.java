package com.example.driver_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record DriverCreateRequest(
        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "유효한 이메일 형식이 아닙니다.")
        String email,

        @NotBlank(message = "비밀번호는 필수입니다.")
        String password,

        @NotBlank(message = "이름은 필수입니다.")
        String name,

        @NotBlank(message = "휴대폰 번호는 필수입니다.")
        String phoneNumber,

        @NotBlank(message = "면허 번호는 필수입니다.")
        String licenseNumber,

        @Valid
        VehicleInfo vehicle
) {
    public record VehicleInfo(
            @NotBlank(message = "차량 번호는 필수입니다.")
            String licensePlate,

            @NotBlank(message = "차량 모델은 필수입니다.")
            String model,

            @NotBlank(message = "차량 색상은 필수입니다.")
            String color
    ) {}
}
