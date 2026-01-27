package com.example.driver_service.repository;

import com.example.driver_service.entity.Driver;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class DriverRepositoryTest {

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("존재하는 이메일로 조회하면 true를 반환한다")
    void givenDriverExists_whenExistsByEmail_thenReturnsTrue() {
        // given
        Driver driver = Driver.builder()
                              .userId("user-1001")
                              .email("test@example.com")
                              .name("홍길동")
                              .phoneNumber("010-1234-5678")
                              .licenseNumber("12-3456-7890")
                              .profileImageUrl("https://profile.url/img.jpg")
                              .build();

        entityManager.persist(driver);
        entityManager.flush();

        // when
        boolean result = driverRepository.existsByEmail("test@example.com");

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 조회하면 false를 반환한다")
    void givenDriverNotExists_whenExistsByEmail_thenReturnsFalse() {
        // given
        // 데이터 저장 없음

        // when
        boolean result = driverRepository.existsByEmail("ghost@example.com");

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("존재하는 휴대폰 번호로 조회하면 true를 반환한다")
    void givenDriverExists_whenExistsByPhoneNumber_thenReturnsTrue() {
        // given
        Driver driver = Driver.builder()
                              .userId("user-1002")
                              .email("phone@example.com")
                              .name("김철수")
                              .phoneNumber("010-9876-5432")
                              .licenseNumber("99-8888-7777")
                              .profileImageUrl("url")
                              .build();

        entityManager.persist(driver);
        entityManager.flush();

        // when
        boolean result = driverRepository.existsByPhoneNumber("010-9876-5432");

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("존재하는 면허 번호로 조회하면 true를 반환한다")
    void givenDriverExists_whenExistsByLicenseNumber_thenReturnsTrue() {
        // given
        Driver driver = Driver.builder()
                              .userId("user-1003")
                              .email("license@example.com")
                              .name("이영희")
                              .phoneNumber("010-5555-4444")
                              .licenseNumber("11-2222-3333")
                              .profileImageUrl("url")
                              .build();

        entityManager.persist(driver);
        entityManager.flush();

        // when
        boolean result = driverRepository.existsByLicenseNumber("11-2222-3333");

        // then
        assertThat(result).isTrue();
    }
}