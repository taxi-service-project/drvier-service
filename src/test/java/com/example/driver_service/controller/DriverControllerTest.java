package com.example.driver_service.controller;

import com.example.driver_service.dto.CreateDriverResponse;
import com.example.driver_service.dto.DriverCreateRequest;
import com.example.driver_service.entity.DriverStatus;
import com.example.driver_service.exception.EmailAlreadyExistsException;
import com.example.driver_service.service.DriverService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
}
