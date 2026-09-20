package com.transport.tms.governance.audit.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 32)
    private String entityType; // TRIP, INVOICE, PAYMENT, USER, CUSTOMER, RATE

    @Column(nullable = false, length = 32)
    private String entityId;

    @Column(nullable = false, length = 32)
    private String action; // CREATE, UPDATE, APPROVE, REJECT, CANCEL, REVERSE

    @Column(length = 64)
    private String fieldName;

    @Column(columnDefinition = "TEXT")
    private String oldValue;

    @Column(columnDefinition = "TEXT")
    private String newValue;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Column(nullable = false, length = 128)
    private String performedBy;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime performedAt;

    @Column(length = 64)
    private String ipAddress;

    @Column(columnDefinition = "TEXT")
    private String userAgent;

    @Column(length = 32)
    private String approvalId;
}
