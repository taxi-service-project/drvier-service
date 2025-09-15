package com.example.driver_service.controller;

import com.example.driver_service.dto.InternalDriverInfoResponse;
import com.example.driver_service.exception.DriverNotFoundException;
import com.example.driver_service.service.DriverService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InternalDriverController.class)
class InternalDriverControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DriverService driverService;

    @Test
    @DisplayName("GET /internal/api/drivers/{driverId} - 성공")
    void getDriverInfoForInternal_Success() throws Exception {
        // given
        long driverId = 1L;
        InternalDriverInfoResponse.VehicleInfo vehicleInfo = new InternalDriverInfoResponse.VehicleInfo("12가3456", "K5");
        InternalDriverInfoResponse mockResponse = new InternalDriverInfoResponse(driverId, "테스트기사", 4.8, vehicleInfo);

        when(driverService.getInternalDriverInfo(driverId)).thenReturn(mockResponse);

        // when & then
        mockMvc.perform(get("/internal/api/drivers/{driverId}", driverId))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(driverId))
               .andExpect(jsonPath("$.name").value("테스트기사"))
               .andExpect(jsonPath("$.vehicle.model").value("K5"));
    }

    @Test
    @DisplayName("GET /internal/api/drivers/{driverId} - 실패 (404 Not Found)")
    void getDriverInfoForInternal_Fail_NotFound() throws Exception {
        // given
        long driverId = 99L;
        when(driverService.getInternalDriverInfo(driverId)).thenThrow(new DriverNotFoundException("기사 없음"));

        // when & then
        mockMvc.perform(get("/internal/api/drivers/{driverId}", driverId))
               .andExpect(status().isNotFound());
    }
}