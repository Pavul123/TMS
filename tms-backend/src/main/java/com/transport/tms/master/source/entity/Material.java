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

import java.time.LocalDateTime;

@Entity
@Table(name = "materials")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Material {

    @Id
    @Column(length = 32)
    private String id; // MAT-001

    @Column(nullable = false, unique = true, length = 128)
    private String name; // 20 MM, 12 MM, Black M-Sand, Boulders

    @Column(nullable = false, length = 64)
    private String category;

    @Column(nullable = false, length = 32)
    @Builder.Default
    private String standardUnit = "Ton"; // Ton, CFT, Load

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
