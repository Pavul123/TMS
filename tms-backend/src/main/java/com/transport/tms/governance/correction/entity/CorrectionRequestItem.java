package com.transport.tms.governance.correction.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "correction_request_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CorrectionRequestItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    @JsonIgnore
    private CorrectionRequest request;

    @Column(nullable = false, length = 64)
    private String fieldName; // e.g. quantity, material, rate

    @Column(columnDefinition = "TEXT")
    private String oldValue;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String requestedValue;
}
