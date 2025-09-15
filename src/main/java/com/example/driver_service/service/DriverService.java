package com.example.driver_service.service;

import com.example.driver_service.dto.CreateDriverResponse;
import com.example.driver_service.dto.DriverCreateRequest;
import com.example.driver_service.dto.DriverProfileResponse;
import com.example.driver_service.dto.UpdateDriverProfileRequest;
import com.example.driver_service.entity.Driver;
import com.example.driver_service.entity.Vehicle;
import com.example.driver_service.exception.*;
import com.example.driver_service.repository.DriverRepository;
import com.example.driver_service.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DriverService {

    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;

    @Transactional
    public CreateDriverResponse createDriver(DriverCreateRequest request) {

        validateDriverUniqueness(request);

        Driver driver = Driver.builder()
                              .email(request.email())
                              .password(request.password())
                              .name(request.name())
                              .phoneNumber(request.phoneNumber())
                              .licenseNumber(request.licenseNumber())
                              .build();
        Driver savedDriver = driverRepository.save(driver);

        Vehicle vehicle = Vehicle.builder()
                                 .driver(savedDriver)
                                 .licensePlate(request.vehicle().licensePlate())
                                 .model(request.vehicle().model())
                                 .color(request.vehicle().color())
                                 .build();
        vehicleRepository.save(vehicle);

        return CreateDriverResponse.fromEntity(savedDriver);
    }

    public DriverProfileResponse getDriverProfile(Long driverId) {
        log.info("{}번 기사 프로필 조회를 시작합니다.", driverId);

        Driver driver = driverRepository.findById(driverId)
                                        .orElseThrow(() -> new DriverNotFoundException("해당 ID의 기사를 찾을 수 없습니다. ID: " + driverId));

        log.info("{}번 기사 프로필 조회 성공", driverId);
        return DriverProfileResponse.fromEntity(driver);
    }

    @Transactional
    public DriverProfileResponse updateDriverProfile(Long driverId, UpdateDriverProfileRequest request) {
        log.info("{}번 기사 프로필 수정을 시작합니다.", driverId);

        Driver driver = driverRepository.findById(driverId)
                                        .orElseThrow(() -> new DriverNotFoundException("해당 ID의 기사를 찾을 수 없습니다. ID: " + driverId));

        driver.updateProfile(
                request.phoneNumber(),
                request.profileImageUrl(),
                request.licenseNumber()
        );

        log.info("{}번 기사 프로필 수정 성공", driverId);
        return DriverProfileResponse.fromEntity(driver);
    }

    private void validateDriverUniqueness(DriverCreateRequest request) {
        if (driverRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException("이미 사용중인 이메일입니다.");
        }
        if (driverRepository.existsByPhoneNumber(request.phoneNumber())) {
            throw new PhoneNumberAlreadyExistsException("이미 등록된 휴대폰 번호입니다.");
        }
        if (driverRepository.existsByLicenseNumber(request.licenseNumber())) {
            throw new LicenseNumberAlreadyExistsException("이미 등록된 면허 번호입니다.");
        }
        if (vehicleRepository.existsByLicensePlate(request.vehicle().licensePlate())) {
            throw new LicensePlateAlreadyExistsException("이미 등록된 차량 번호입니다.");
        }
    }
}
