package com.example.driver_service.service;

import com.example.driver_service.dto.request.DriverCreateRequest;
import com.example.driver_service.dto.request.UpdateDriverProfileRequest;
import com.example.driver_service.dto.request.UpdateDriverStatusRequest;
import com.example.driver_service.dto.request.UpdateVehicleRequest;
import com.example.driver_service.dto.response.CreateDriverResponse;
import com.example.driver_service.dto.response.DriverProfileResponse;
import com.example.driver_service.dto.response.InternalDriverInfoResponse;
import com.example.driver_service.dto.response.VehicleResponse;
import com.example.driver_service.entity.Driver;
import com.example.driver_service.entity.Vehicle;
import com.example.driver_service.exception.*;
import com.example.driver_service.repository.DriverRepository;
import com.example.driver_service.repository.VehicleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DriverServiceTest {

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private DriverService driverService;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private HashOperations<String, Object, Object> hashOperations;

    @Test
    @DisplayName("유효한 요청이 오면 운전자를 성공적으로 생성한다")
    void givenValidRequest_whenCreateDriver_thenCreatesDriverSuccessfully() {
        // given
        DriverCreateRequest.VehicleInfo vehicleInfo = new DriverCreateRequest.VehicleInfo("12가3456", "K5", "검정");
        DriverCreateRequest request = new DriverCreateRequest("test@example.com", "password", "홍길동", "010-1234-5678", "12-3456-7890", vehicleInfo);

        when(driverRepository.existsByEmail(any())).thenReturn(false);
        when(driverRepository.existsByPhoneNumber(any())).thenReturn(false);
        when(driverRepository.existsByLicenseNumber(any())).thenReturn(false);
        when(vehicleRepository.existsByLicensePlate(any())).thenReturn(false);

        Driver savedDriver = Driver.builder().email(request.email()).build();
        when(driverRepository.save(any(Driver.class))).thenReturn(savedDriver);

        // when
        CreateDriverResponse response = driverService.createDriver(request);

        // then
        assertThat(response.email()).isEqualTo(request.email());
        verify(driverRepository).save(any(Driver.class));
        verify(vehicleRepository).save(any(Vehicle.class));
    }

    @Test
    @DisplayName("이미 존재하는 이메일이면 EmailAlreadyExistsException을 던진다")
    void givenDuplicateEmail_whenCreateDriver_thenThrowsEmailAlreadyExistsException() {
        // given
        DriverCreateRequest.VehicleInfo vehicleInfo = new DriverCreateRequest.VehicleInfo("12가3456", "K5", "검정");
        DriverCreateRequest request = new DriverCreateRequest("test@example.com", "password", "홍길동", "010-1234-5678", "12-3456-7890", vehicleInfo);

        when(driverRepository.existsByEmail("test@example.com")).thenReturn(true);

        // when & then
        assertThrows(EmailAlreadyExistsException.class, () -> {
            driverService.createDriver(request);
        });
    }

    @Test
    @DisplayName("이미 존재하는 휴대폰 번호이면 PhoneNumberAlreadyExistsException을 던진다")
    void givenDuplicatePhoneNumber_whenCreateDriver_thenThrowsPhoneNumberAlreadyExistsException() {
        // given
        DriverCreateRequest.VehicleInfo vehicleInfo = new DriverCreateRequest.VehicleInfo("12가3456", "K5", "검정");
        DriverCreateRequest request = new DriverCreateRequest("test@example.com", "password", "홍길동", "010-1234-5678", "12-3456-7890", vehicleInfo);

        when(driverRepository.existsByPhoneNumber("010-1234-5678")).thenReturn(true);

        // when & then
        assertThrows(PhoneNumberAlreadyExistsException.class, () -> {
            driverService.createDriver(request);
        });
    }

    @Test
    @DisplayName("이미 존재하는 면허 번호이면 LicenseNumberAlreadyExistsException을 던진다")
    void givenDuplicateLicenseNumber_whenCreateDriver_thenThrowsLicenseNumberAlreadyExistsException() {
        // given
        DriverCreateRequest.VehicleInfo vehicleInfo = new DriverCreateRequest.VehicleInfo("12가3456", "K5", "검정");
        DriverCreateRequest request = new DriverCreateRequest("test@example.com", "password", "홍길동", "010-1234-5678", "12-3456-7890", vehicleInfo);

        when(driverRepository.existsByLicenseNumber("12-3456-7890")).thenReturn(true);

        // when & then
        assertThrows(LicenseNumberAlreadyExistsException.class, () -> {
            driverService.createDriver(request);
        });
    }

    @Test
    @DisplayName("이미 존재하는 차량 번호이면 LicensePlateAlreadyExistsException을 던진다")
    void givenDuplicateLicensePlate_whenCreateDriver_thenThrowsLicensePlateAlreadyExistsException() {
        // given
        DriverCreateRequest.VehicleInfo vehicleInfo = new DriverCreateRequest.VehicleInfo("12가3456", "K5", "검정");
        DriverCreateRequest request = new DriverCreateRequest("test@example.com", "password", "홍길동", "010-1234-5678", "12-3456-7890", vehicleInfo);

        when(vehicleRepository.existsByLicensePlate("12가3456")).thenReturn(true);

        // when & then
        assertThrows(LicensePlateAlreadyExistsException.class, () -> {
            driverService.createDriver(request);
        });
    }

    @Test
    @DisplayName("기사 프로필 조회 성공")
    void getDriverProfile_Success() {
        // given (Arrange)
        long driverId = 1L;
        Driver mockDriver = Driver.builder()
                                  .email("test@example.com")
                                  .name("테스트기사")
                                  .phoneNumber("010-1234-5678")
                                  .licenseNumber("12-34-567890-11")
                                  .profileImageUrl("http://example.com/profile.jpg")
                                  .build();
        ReflectionTestUtils.setField(mockDriver, "id", driverId);

        // Mockito.when() 구문 사용
        when(driverRepository.findById(driverId)).thenReturn(Optional.of(mockDriver));

        // when (Act)
        DriverProfileResponse response = driverService.getDriverProfile(driverId);

        // then (Assert)
        assertThat(response.id()).isEqualTo(driverId);
        assertThat(response.name()).isEqualTo("테스트기사");
        verify(driverRepository).findById(driverId);
    }

    @Test
    @DisplayName("기사 프로필 조회 실패 - 존재하지 않는 기사")
    void getDriverProfile_Fail_DriverNotFound() {
        // given (Arrange)
        long nonExistentDriverId = 99L;
        when(driverRepository.findById(nonExistentDriverId)).thenReturn(Optional.empty());

        // when & then (Act & Assert)
        assertThrows(DriverNotFoundException.class, () -> {
            driverService.getDriverProfile(nonExistentDriverId);
        });

        verify(driverRepository).findById(nonExistentDriverId);
    }

    @Test
    @DisplayName("기사 프로필 수정 성공")
    void updateDriverProfile_Success() {
        // given
        long driverId = 1L;
        Driver mockDriver = Driver.builder()
                                  .email("test@example.com")
                                  .name("테스트기사")
                                  .phoneNumber("010-0000-0000") // 변경 전 번호
                                  .licenseNumber("00-00-000000-00")
                                  .profileImageUrl("http://example.com/old.jpg")
                                  .build();
        ReflectionTestUtils.setField(mockDriver, "id", driverId);

        UpdateDriverProfileRequest request = new UpdateDriverProfileRequest(
                "010-1111-1111", // 변경 후 번호
                "http://example.com/new.jpg",
                "11-11-111111-11"
        );

        when(driverRepository.findById(driverId)).thenReturn(Optional.of(mockDriver));

        // when
        DriverProfileResponse response = driverService.updateDriverProfile(driverId, request);

        // then
        assertThat(response.id()).isEqualTo(driverId);
        assertThat(response.phoneNumber()).isEqualTo("010-1111-1111"); // 변경된 정보 확인
        assertThat(response.profileImageUrl()).isEqualTo("http://example.com/new.jpg");
        assertThat(response.licenseNumber()).isEqualTo("11-11-111111-11");
        verify(driverRepository).findById(driverId);
    }

    @Test
    @DisplayName("기사 차량 정보 조회 성공")
    void getVehicleByDriverId_Success() {
        // given
        long driverId = 1L;
        Driver mockDriver = Driver.builder().build(); // Vehicle 생성에 필요
        ReflectionTestUtils.setField(mockDriver, "id", driverId);

        Vehicle mockVehicle = Vehicle.builder()
                                     .driver(mockDriver)
                                     .licensePlate("12가3456")
                                     .model("쏘나타")
                                     .color("검정")
                                     .build();

        when(vehicleRepository.findByDriverId(driverId)).thenReturn(Optional.of(mockVehicle));

        // when
        VehicleResponse response = driverService.getVehicleByDriverId(driverId);

        // then
        assertThat(response.licensePlate()).isEqualTo("12가3456");
        assertThat(response.model()).isEqualTo("쏘나타");
        verify(vehicleRepository).findByDriverId(driverId);
    }

    @Test
    @DisplayName("기사 차량 정보 조회 실패 - 차량 정보 없음")
    void getVehicleByDriverId_Fail_NotFound() {
        // given
        long driverId = 99L;
        when(vehicleRepository.findByDriverId(driverId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(VehicleNotFoundException.class, () -> {
            driverService.getVehicleByDriverId(driverId);
        });
        verify(vehicleRepository).findByDriverId(driverId);
    }

    @Test
    @DisplayName("기사 차량 정보 수정 성공")
    void updateVehicleInfo_Success() {
        // given
        long driverId = 1L;
        Driver mockDriver = Driver.builder().build();
        ReflectionTestUtils.setField(mockDriver, "id", driverId);

        Vehicle mockVehicle = Vehicle.builder()
                                     .driver(mockDriver)
                                     .licensePlate("12가3456")
                                     .model("쏘나타") // 변경 전 모델
                                     .color("검정")   // 변경 전 색상
                                     .build();

        UpdateVehicleRequest request = new UpdateVehicleRequest("K5", "흰색");

        when(vehicleRepository.findByDriverId(driverId)).thenReturn(Optional.of(mockVehicle));

        // when
        VehicleResponse response = driverService.updateVehicleInfo(driverId, request);

        // then
        assertThat(response.model()).isEqualTo("K5"); // 변경 확인
        assertThat(response.color()).isEqualTo("흰색"); // 변경 확인
        verify(vehicleRepository).findByDriverId(driverId);
    }

    @Test
    @DisplayName("기사 운행 상태 변경 성공 - AVAILABLE")
    void updateDriverStatus_Success_Available() {
        // given
        long driverId = 1L;
        UpdateDriverStatusRequest request = new UpdateDriverStatusRequest("AVAILABLE");
        String redisKey = "driver_status:" + driverId;

        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(driverRepository.existsById(driverId)).thenReturn(true);

        // when
        driverService.updateDriverStatus(driverId, request);

        // then
        verify(driverRepository).existsById(driverId);
        verify(hashOperations).put(redisKey, "isAvailable", "1");
    }

    @Test
    @DisplayName("기사 운행 상태 변경 실패 - 존재하지 않는 기사")
    void updateDriverStatus_Fail_DriverNotFound() {
        // given
        long driverId = 99L;
        UpdateDriverStatusRequest request = new UpdateDriverStatusRequest("OFFLINE");

        when(driverRepository.existsById(driverId)).thenReturn(false);

        // when & then
        assertThrows(DriverNotFoundException.class, () -> {
            driverService.updateDriverStatus(driverId, request);
        });

        verify(redisTemplate, never()).opsForHash();
        verify(hashOperations, never()).put(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("내부용 기사 정보 조회 성공")
    void getInternalDriverInfo_Success() {
        // given
        String driverUuid = "a1b2c3d4-e5f6-7890-1234-567890abcdef";
        Long driverInternalId = 1L;

        Driver mockDriver = Driver.builder().name("테스트기사").build();
        ReflectionTestUtils.setField(mockDriver, "id", driverInternalId);
        ReflectionTestUtils.setField(mockDriver, "driverId", driverUuid);

        Vehicle mockVehicle = Vehicle.builder()
                                     .driver(mockDriver).model("K5").licensePlate("12가3456").build();

        when(driverRepository.findByDriverId(driverUuid)).thenReturn(Optional.of(mockDriver));
        when(vehicleRepository.findByDriverId(driverInternalId)).thenReturn(Optional.of(mockVehicle));

        // when
        InternalDriverInfoResponse response = driverService.getInternalDriverInfo(driverUuid);

        // then
        assertThat(response.driverId()).isEqualTo(driverUuid);
        assertThat(response.name()).isEqualTo("테스트기사");
        assertThat(response.vehicle().model()).isEqualTo("K5");

        verify(driverRepository).findByDriverId(driverUuid);
        verify(vehicleRepository).findByDriverId(driverInternalId);
    }

    @Test
    @DisplayName("내부용 기사 정보 조회 실패 - 기사 정보 없음")
    void getInternalDriverInfo_Fail_DriverNotFound() {
        // given
        String driverUuid = "non-existent-uuid";
        when(driverRepository.findByDriverId(driverUuid)).thenReturn(Optional.empty());

        // when & then
        assertThrows(DriverNotFoundException.class, () -> {
            driverService.getInternalDriverInfo(driverUuid);
        });

        verify(vehicleRepository, never()).findByDriverId(anyLong());
    }
}
