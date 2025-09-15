package com.example.driver_service.service;

import com.example.driver_service.dto.*;
import com.example.driver_service.entity.Driver;
import com.example.driver_service.entity.Vehicle;
import com.example.driver_service.exception.*;
import com.example.driver_service.repository.DriverRepository;
import com.example.driver_service.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DriverService {

    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String REDIS_KEY_PREFIX = "driver_status:";
    private static final String HASH_KEY_IS_AVAILABLE = "isAvailable";

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

    public VehicleResponse getVehicleByDriverId(Long driverId) {
        log.info("{}번 기사의 차량 정보 조회를 시작합니다.", driverId);

        Vehicle vehicle = vehicleRepository.findByDriverId(driverId)
                                           .orElseThrow(() -> new VehicleNotFoundException("해당 기사의 차량 정보를 찾을 수 없습니다. 기사 ID: " + driverId));

        log.info("{}번 기사의 차량 정보 조회 성공", driverId);
        return VehicleResponse.fromEntity(vehicle);
    }

    @Transactional
    public VehicleResponse updateVehicleInfo(Long driverId, UpdateVehicleRequest request) {
        log.info("{}번 기사의 차량 정보 수정을 시작합니다.", driverId);

        Vehicle vehicle = vehicleRepository.findByDriverId(driverId)
                                           .orElseThrow(() -> new VehicleNotFoundException("해당 기사의 차량 정보를 찾을 수 없습니다. 기사 ID: " + driverId));

        vehicle.updateInfo(request.model(), request.color());

        log.info("{}번 기사의 차량 정보 수정 성공", driverId);
        return VehicleResponse.fromEntity(vehicle);
    }

    public void updateDriverStatus(Long driverId, UpdateDriverStatusRequest request) {
        log.info("{}번 기사의 실시간 운행 상태 변경을 시작합니다. 상태: {}", driverId, request.status());

        if (!driverRepository.existsById(driverId)) {
            throw new DriverNotFoundException("해당 ID의 기사를 찾을 수 없습니다. ID: " + driverId);
        }

        String redisKey = REDIS_KEY_PREFIX + driverId;
        String isAvailableValue = "AVAILABLE".equalsIgnoreCase(request.status()) ? "1" : "0";

        redisTemplate.opsForHash().put(redisKey, HASH_KEY_IS_AVAILABLE, isAvailableValue);

        log.info("{}번 기사 운행 상태 Redis 업데이트 성공. Key: {}, Value: {}", driverId, redisKey, isAvailableValue);
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
