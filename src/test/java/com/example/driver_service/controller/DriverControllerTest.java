package com.example.driver_service.controller;

import com.example.driver_service.dto.*;
import com.example.driver_service.entity.DriverStatus;
import com.example.driver_service.exception.DriverNotFoundException;
import com.example.driver_service.exception.EmailAlreadyExistsException;
import com.example.driver_service.exception.VehicleNotFoundException;
import com.example.driver_service.service.DriverService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(DriverController.class)
class DriverControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DriverService driverService;

    @Test
    @DisplayName("유효한 운전자 생성 요청이 오면 201 Created를 응답한다")
    void givenValidRequest_whenCreateDriver_thenReturns201Created() throws Exception {
        // given
        DriverCreateRequest.VehicleInfo vehicleInfo = new DriverCreateRequest.VehicleInfo("12가3456", "K5", "검정");
        DriverCreateRequest request = new DriverCreateRequest("test@example.com", "password", "홍길동", "010-1234-5678", "12-3456-7890", vehicleInfo);

        CreateDriverResponse response = new CreateDriverResponse(1L, "test@example.com", "홍길동", DriverStatus.WAITING_APPROVAL);
        when(driverService.createDriver(any(DriverCreateRequest.class))).thenReturn(response);

        // when & then
        mockMvc.perform(post("/api/drivers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @DisplayName("필수 입력값이 누락되면 400 BadRequest를 응답한다")
    void givenInvalidRequest_whenCreateDriver_thenReturns400BadRequest() throws Exception {
        // given
        DriverCreateRequest.VehicleInfo vehicleInfo = new DriverCreateRequest.VehicleInfo("12가3456", "K5", "검정");
        // 이메일 누락
        DriverCreateRequest request = new DriverCreateRequest("", "password", "홍길동", "010-1234-5678", "12-3456-7890", vehicleInfo);

        // when & then
        mockMvc.perform(post("/api/drivers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("중복된 이메일로 요청하면 409 Conflict를 응답한다")
    void givenDuplicateEmail_whenCreateDriver_thenReturns409Conflict() throws Exception {
        // given
        DriverCreateRequest.VehicleInfo vehicleInfo = new DriverCreateRequest.VehicleInfo("12가3456", "K5", "검정");
        DriverCreateRequest request = new DriverCreateRequest("test@example.com", "password", "홍길동", "010-1234-5678", "12-3456-7890", vehicleInfo);

        when(driverService.createDriver(any(DriverCreateRequest.class)))
                .thenThrow(new EmailAlreadyExistsException("이미 사용중인 이메일입니다."));

        // when & then
        mockMvc.perform(post("/api/drivers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("이미 사용중인 이메일입니다."));
    }

    @Test
    @DisplayName("GET /api/drivers/{driverId} - 성공")
    void getDriverProfile_Success() throws Exception {
        // given (Arrange)
        long driverId = 1L;
        DriverProfileResponse mockResponse = new DriverProfileResponse(
                driverId,
                "test@example.com",
                "테스트기사",
                "010-1234-5678",
                "12-34-567890-11",
                "http://example.com/profile.jpg",
                DriverStatus.ACTIVE,
                4.8
        );
        // Mockito.when() 구문 사용
        when(driverService.getDriverProfile(driverId)).thenReturn(mockResponse);
        // when & then (Act & Assert)
        mockMvc.perform(get("/api/drivers/{driverId}", driverId))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.id").value(driverId))
               .andExpect(jsonPath("$.name").value("테스트기사"))
               .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("GET /api/drivers/{driverId} - 실패 (404 Not Found)")
    void getDriverProfile_Fail_DriverNotFound() throws Exception {
        // given (Arrange)
        long nonExistentDriverId = 99L;
        // Exception 발생 시에는 .thenThrow() 사용
        when(driverService.getDriverProfile(anyLong()))
                .thenThrow(new DriverNotFoundException("해당 ID의 기사를 찾을 수 없습니다. ID: " + nonExistentDriverId));

        // when & then (Act & Assert)
        mockMvc.perform(get("/api/drivers/{driverId}", nonExistentDriverId))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("PUT /api/drivers/{driverId} - 성공")
    void updateDriverProfile_Success() throws Exception {
        // given
        long driverId = 1L;
        UpdateDriverProfileRequest request = new UpdateDriverProfileRequest(
                "010-1111-1111",
                "http://example.com/new.jpg",
                "11-11-111111-11"
        );

        DriverProfileResponse mockResponse = new DriverProfileResponse(
                driverId, "test@example.com", "테스트기사",
                request.phoneNumber(), request.licenseNumber(), request.profileImageUrl(),
                DriverStatus.ACTIVE, 4.8
        );

        when(driverService.updateDriverProfile(anyLong(), any(UpdateDriverProfileRequest.class)))
                .thenReturn(mockResponse);

        // when & then
        mockMvc.perform(put("/api/drivers/{driverId}", driverId)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.phoneNumber").value("010-1111-1111"))
               .andExpect(jsonPath("$.profileImageUrl").value("http://example.com/new.jpg"));
    }

    @Test
    @DisplayName("PUT /api/drivers/{driverId} - 실패 (유효성 검증 실패)")
    void updateDriverProfile_Fail_InvalidRequest() throws Exception {
        // given
        long driverId = 1L;
        UpdateDriverProfileRequest invalidRequest = new UpdateDriverProfileRequest(
                "", // Blank 값
                "http://example.com/new.jpg",
                "11-11-111111-11"
        );

        // when & then
        mockMvc.perform(put("/api/drivers/{driverId}", driverId)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(invalidRequest)))
               .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/drivers/{driverId}/vehicle - 성공")
    void getVehicle_Success() throws Exception {
        // given
        long driverId = 1L;
        VehicleResponse mockResponse = new VehicleResponse(1L, "12가3456", "쏘나타", "검정");

        when(driverService.getVehicleByDriverId(driverId)).thenReturn(mockResponse);

        // when & then
        mockMvc.perform(get("/api/drivers/{driverId}/vehicle", driverId))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.licensePlate").value("12가3456"))
               .andExpect(jsonPath("$.model").value("쏘나타"));
    }

    @Test
    @DisplayName("GET /api/drivers/{driverId}/vehicle - 실패 (404 Not Found)")
    void getVehicle_Fail_NotFound() throws Exception {
        // given
        long driverId = 99L;
        when(driverService.getVehicleByDriverId(driverId))
                .thenThrow(new VehicleNotFoundException("해당 기사의 차량 정보를 찾을 수 없습니다."));

        // when & then
        mockMvc.perform(get("/api/drivers/{driverId}/vehicle", driverId))
               .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /api/drivers/{driverId}/vehicle - 성공")
    void updateVehicle_Success() throws Exception {
        // given
        long driverId = 1L;
        UpdateVehicleRequest request = new UpdateVehicleRequest("K5", "흰색");
        VehicleResponse mockResponse = new VehicleResponse(1L, "12가3456", "K5", "흰색");

        when(driverService.updateVehicleInfo(anyLong(), any(UpdateVehicleRequest.class)))
                .thenReturn(mockResponse);

        // when & then
        mockMvc.perform(put("/api/drivers/{driverId}/vehicle", driverId)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.model").value("K5"))
               .andExpect(jsonPath("$.color").value("흰색"));
    }

    @Test
    @DisplayName("PUT /api/drivers/{driverId}/vehicle - 실패 (유효성 검증 실패)")
    void updateVehicle_Fail_InvalidRequest() throws Exception {
        // given
        long driverId = 1L;
        UpdateVehicleRequest invalidRequest = new UpdateVehicleRequest("", "흰색"); // 모델명이 비어있음

        // when & then
        mockMvc.perform(put("/api/drivers/{driverId}/vehicle", driverId)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(invalidRequest)))
               .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/drivers/{driverId}/status - 성공 (204 No Content)")
    void updateDriverStatus_Success() throws Exception {
        // given
        long driverId = 1L;
        UpdateDriverStatusRequest request = new UpdateDriverStatusRequest("AVAILABLE");

        // when & then
        mockMvc.perform(put("/api/drivers/{driverId}/status", driverId)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isNoContent());

        verify(driverService).updateDriverStatus(eq(driverId), any(UpdateDriverStatusRequest.class));
    }

    @Test
    @DisplayName("PUT /api/drivers/{driverId}/status - 실패 (유효성 검증 실패)")
    void updateDriverStatus_Fail_InvalidStatus() throws Exception {
        // given
        long driverId = 1L;
        UpdateDriverStatusRequest request = new UpdateDriverStatusRequest("INVALID_STATUS");

        // when & then
        mockMvc.perform(put("/api/drivers/{driverId}/status", driverId)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isBadRequest());
    }
}
