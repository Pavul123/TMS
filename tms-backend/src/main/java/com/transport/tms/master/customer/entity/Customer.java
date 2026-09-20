package com.transport.tms.master.customer.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "customers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    @Id
    @Column(length = 32)
    private String id; // e.g. CUS-00124

    @Column(nullable = false, length = 128)
    private String name;

    @Column(nullable = false, unique = true, length = 32)
    private String phone; // Duplicate protection lookup key

    @Column(length = 32)
    private String alternatePhone;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String address;

    @Column(length = 32)
    private String gstin;

    @Column(length = 64)
    private String creditTerms; // e.g. 15 Days, 30 Days

    @Column(nullable = false, length = 32)
    @Builder.Default
    private String status = "ACTIVE";

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal openingBalance = BigDecimal.ZERO;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
