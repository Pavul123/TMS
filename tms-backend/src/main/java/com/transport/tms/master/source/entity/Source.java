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
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "sources")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Source {

    @Id
    @Column(length = 32)
    private String id; // e.g. SRC-001

    @Column(nullable = false, length = 128)
    private String name; // Crusher / Quarry Name

    @Column(nullable = false, length = 128)
    private String location;

    @Column(length = 128)
    private String contactPerson;

    @Column(length = 32)
    private String phone;

    @Column(nullable = false, length = 64)
    private String material;

    @Column(nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal pricePerTon = BigDecimal.ZERO;

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
