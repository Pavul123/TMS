package com.transport.tms.finance.wage.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class WageEntities {

    @Entity
    @Table(name = "worker_wages")
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WorkerWage {

        @Id
        @Column(length = 32)
        private String id; // e.g. WAG-0001

        @Column(nullable = false, length = 32)
        private String workerId;

        @Column(nullable = false, length = 128)
        private String workerName;

        @Column(nullable = false, length = 10)
        private String monthYear; // e.g. "2026-09"

        @Column(nullable = false, precision = 12, scale = 2)
        @Builder.Default
        private BigDecimal baseSalary = BigDecimal.ZERO;

        @Column(nullable = false, precision = 12, scale = 2)
        @Builder.Default
        private BigDecimal overtimeAmount = BigDecimal.ZERO;

        @Column(nullable = false, precision = 12, scale = 2)
        @Builder.Default
        private BigDecimal tripAllowance = BigDecimal.ZERO;

        @Column(nullable = false, precision = 12, scale = 2)
        @Builder.Default
        private BigDecimal advancesDeducted = BigDecimal.ZERO;

        @Column(nullable = false, precision = 12, scale = 2)
        @Builder.Default
        private BigDecimal otherDeductions = BigDecimal.ZERO;

        @Column(nullable = false, precision = 12, scale = 2)
        @Builder.Default
        private BigDecimal netPayable = BigDecimal.ZERO;

        @Column(nullable = false, length = 32)
        @Builder.Default
        private String status = "PENDING"; // PENDING, DISBURSED

        private LocalDate paymentDate;
        private String paymentMode;
        private String referenceNo;
        private String disbursedBy;

        @CreationTimestamp
        @Column(nullable = false, updatable = false)
        private LocalDateTime createdAt;
    }

    @Entity
    @Table(name = "wage_advances")
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WageAdvance {

        @Id
        @Column(length = 32)
        private String id; // e.g. ADV-0001

        @Column(nullable = false)
        private LocalDate date;

        @Column(nullable = false, length = 16)
        private String recipientType; // DRIVER, WORKER

        @Column(nullable = false, length = 32)
        private String recipientId;

        @Column(nullable = false, length = 128)
        private String recipientName;

        @Column(nullable = false, precision = 15, scale = 2)
        private BigDecimal amount;

        @Column(columnDefinition = "TEXT")
        private String reason;

        @Column(nullable = false, length = 32)
        @Builder.Default
        private String paymentMode = "CASH";

        @Column(nullable = false, length = 32)
        @Builder.Default
        private String status = "PAID";

        private String approvedBy;

        @Column(nullable = false, length = 128)
        private String givenBy;

        @CreationTimestamp
        @Column(nullable = false, updatable = false)
        private LocalDateTime createdAt;
    }
}
