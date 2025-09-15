package com.example.driver_service.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateDriverProfileRequest(
        @NotBlank(message = "휴대폰 번호는 필수입니다.")
        String phoneNumber,

        @NotBlank(message = "프로필 이미지 URL은 필수입니다.")
        String profileImageUrl,

        @NotBlank(message = "면허 번호는 필수입니다.")
        String licenseNumber
) {
}