package com.transport.tms.master.driver.entity;

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
import java.time.LocalDateTime;

@Entity
@Table(name = "drivers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Driver {

    @Id
    @Column(length = 32)
    private String id; // e.g. DRV-0012

    @Column(nullable = false, length = 128)
    private String name;

    @Column(nullable = false, length = 32)
    private String phone;

    @Column(length = 64)
    private String licenseNumber;

    @Column(length = 32)
    private String assignedVehicle;

    @Column(nullable = false, length = 32)
    @Builder.Default
    private String status = "AVAILABLE"; // AVAILABLE, ON_TRIP, INACTIVE

    @Column(nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal advanceBalance = BigDecimal.ZERO;

    @Column(nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal totalEarnings = BigDecimal.ZERO;

    @Column(nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal totalSettled = BigDecimal.ZERO;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
