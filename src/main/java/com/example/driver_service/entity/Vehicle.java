package com.example.driver_service.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "vehicles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", unique = true, nullable = false)
    private Driver driver;

    @Column(name = "license_plate", unique = true, nullable = false, length = 20)
    private String licensePlate;

    @Column(nullable = false, length = 50)
    private String model;

    @Column(nullable = false, length = 20)
    private String color;

    @Builder
    public Vehicle(Driver driver, String licensePlate, String model, String color) {
        this.driver = driver;
        this.licensePlate = licensePlate;
        this.model = model;
        this.color = color;
    }
}
