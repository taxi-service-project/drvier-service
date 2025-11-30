package com.example.driver_service.repository;

import com.example.driver_service.entity.Driver;
import com.example.driver_service.entity.Vehicle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class VehicleRepositoryTest {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("존재하는 차량 번호로 조회하면 true를 반환한다")
    void givenVehicleExists_whenExistsByLicensePlate_thenReturnsTrue() {
        // given
        Driver driver = Driver.builder()
                .email("test@example.com")
                .name("홍길동")
                .phoneNumber("010-1234-5678")
                .licenseNumber("12-3456-7890")
                .profileImageUrl("url")
                .build();
        entityManager.persist(driver);

        Vehicle vehicle = Vehicle.builder()
                .driver(driver)
                .licensePlate("12가3456")
                .model("K5")
                .color("검정")
                .build();
        entityManager.persist(vehicle);

        // when
        boolean result = vehicleRepository.existsByLicensePlate("12가3456");

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 차량 번호로 조회하면 false를 반환한다")
    void givenVehicleNotExists_whenExistsByLicensePlate_thenReturnsFalse() {
        // given
        // 데이터 없음

        // when
        boolean result = vehicleRepository.existsByLicensePlate("12가3456");

        // then
        assertThat(result).isFalse();
    }
}
