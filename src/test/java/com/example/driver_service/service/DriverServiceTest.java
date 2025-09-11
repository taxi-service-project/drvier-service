package com.example.driver_service.service;

import com.example.driver_service.dto.CreateDriverResponse;
import com.example.driver_service.dto.DriverCreateRequest;
import com.example.driver_service.entity.Driver;
import com.example.driver_service.entity.Vehicle;
import com.example.driver_service.exception.EmailAlreadyExistsException;
import com.example.driver_service.exception.LicenseNumberAlreadyExistsException;
import com.example.driver_service.exception.LicensePlateAlreadyExistsException;
import com.example.driver_service.exception.PhoneNumberAlreadyExistsException;
import com.example.driver_service.repository.DriverRepository;
import com.example.driver_service.repository.VehicleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DriverServiceTest {

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private DriverService driverService;

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
}
