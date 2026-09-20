package com.transport.tms.master.source.entity;

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
@Table(name = "fuel_stations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FuelStation {

    @Id
    @Column(length = 32)
    private String id; // PMP-001

    @Column(nullable = false, length = 128)
    private String name;

    @Column(nullable = false, length = 128)
    private String location;

    @Column(length = 128)
    private String contactPerson;

    @Column(length = 32)
    private String phone;

    @Column(nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(nullable = false, length = 32)
    @Builder.Default
    private String status = "ACTIVE";

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
