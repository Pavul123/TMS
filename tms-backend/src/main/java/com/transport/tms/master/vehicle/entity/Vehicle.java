package com.transport.tms.master.vehicle.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehicles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {

    @Id
    @Column(length = 32)
    private String registration; // e.g. TN 58 AB 2345

    @Column(nullable = false, length = 64)
    private String type; // 10-Wheel Tipper, 12-Wheel Tipper, Trailer

    @Column(nullable = false, length = 32)
    @Builder.Default
    private String ownership = "OWN"; // OWN, RENTED

    @Column(nullable = false, length = 32)
    private String capacity; // e.g. 18 Ton, 20 Ton

    @Column(nullable = false, length = 32)
    private String fuelCapacity; // e.g. 180 L

    @Column(nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal currentKm = BigDecimal.ZERO;

    private LocalDate lastMaintenanceDate;
    private BigDecimal maintenanceFee;
    private LocalDate insuranceExpiry;
    private LocalDate fcExpiry;
    private LocalDate permitExpiry;

    @Column(length = 32)
    private String assignedDriverId;

    @Column(length = 128)
    private String assignedDriverName;

    @Column(nullable = false, length = 32)
    @Builder.Default
    private String status = "AVAILABLE"; // AVAILABLE, ON_TRIP, MAINTENANCE, INACTIVE

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
