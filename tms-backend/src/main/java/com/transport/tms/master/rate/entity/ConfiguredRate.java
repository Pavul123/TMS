package com.transport.tms.master.rate.entity;

import jakarta.persistence.*;
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
@Table(name = "configured_rates")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfiguredRate {

    @Id
    @Column(length = 32)
    private String id; // RAT-001

    @Column(nullable = false, length = 32)
    private String rateType; // CUSTOMER, CRUSHER, TRANSPORT

    @Column(length = 32)
    private String customerId;

    @Column(length = 32)
    private String sourceId;

    @Column(nullable = false, length = 128)
    private String material;

    @Column(nullable = false, length = 128)
    private String loadingLocation;

    @Column(nullable = false, length = 128)
    private String deliveryLocation;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal rate;

    @Column(nullable = false, length = 32)
    @Builder.Default
    private String unit = "Ton";

    @Column(nullable = false)
    private LocalDate effectiveFrom;

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
