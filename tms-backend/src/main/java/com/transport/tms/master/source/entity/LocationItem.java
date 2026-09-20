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
@Table(name = "locations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationItem {

    @Id
    @Column(length = 32)
    private String id; // LOC-001

    @Column(nullable = false, length = 128)
    private String name;

    @Column(nullable = false, length = 32)
    private String type; // Site, Yard, Quarry, Crusher

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(precision = 10, scale = 2)
    private BigDecimal distanceKm;

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
