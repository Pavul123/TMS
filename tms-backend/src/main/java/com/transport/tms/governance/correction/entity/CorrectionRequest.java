package com.transport.tms.governance.correction.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "correction_requests")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CorrectionRequest {

    @Id
    @Column(length = 32)
    private String id; // e.g. CRQ-0001

    @Column(nullable = false, length = 32)
    private String entityType; // TRIP, INVOICE, PAYMENT, CUSTOMER, DIESEL

    @Column(nullable = false, length = 32)
    private String entityId; // e.g. TRP-01483

    @Column(nullable = false, length = 128)
    private String entityIdentifier; // Display title, e.g. "Trip TRP-01483 (K Engineering)"

    @Column(nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Column(nullable = false, length = 32)
    @Builder.Default
    private String status = "PENDING"; // PENDING, APPROVED, REJECTED, CANCELLED

    @Column(nullable = false, length = 128)
    private String requestedBy;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime requestedAt;

    @Column(length = 128)
    private String reviewedBy;

    private LocalDateTime reviewedAt;

    @Column(columnDefinition = "TEXT")
    private String reviewComment;

    @Version
    @Column(nullable = false)
    @Builder.Default
    private Long version = 0L;

    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    private List<CorrectionRequestItem> items = new ArrayList<>();
}
