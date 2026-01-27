package com.example.driver_service.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "drivers")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Driver extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false, unique = true)
    private String driverId;

    @Column(nullable = false, updatable = false, unique = true)
    private String userId;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(unique = true, nullable = false, length = 20)
    private String phoneNumber;

    @Column(unique = true, nullable = false, length = 50)
    private String licenseNumber;

    @Column(nullable = false)
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DriverStatus status;

    @Column(nullable = false)
    private Double ratingAvg;

    @OneToOne(mappedBy = "driver", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Vehicle vehicle;

    @Builder
    public Driver(String userId, String email, String name, String phoneNumber, String licenseNumber, String profileImageUrl) {
        this.driverId = UUID.randomUUID().toString();
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.licenseNumber = licenseNumber;
        this.profileImageUrl = profileImageUrl;
        this.status = DriverStatus.WAITING_APPROVAL;
        this.ratingAvg = 0.0;
    }

    public void updateProfile(String phoneNumber, String profileImageUrl, String licenseNumber) {
        this.phoneNumber = phoneNumber;
        this.profileImageUrl = profileImageUrl;
        this.licenseNumber = licenseNumber;
    }

    public void addVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
        if (vehicle != null) {
            vehicle.setDriver(this);
        }
    }

}

