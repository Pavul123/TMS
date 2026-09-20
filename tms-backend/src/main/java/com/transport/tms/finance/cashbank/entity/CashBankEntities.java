package com.transport.tms.finance.cashbank.entity;

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

public class CashBankEntities {

    @Entity
    @Table(name = "cash_bank_accounts")
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CashBankAccount {

        @Id
        @Column(length = 32)
        private String id; // e.g. ACC-001, ACC-002

        @Column(nullable = false, length = 128)
        private String accountName;

        @Column(nullable = false, length = 32)
        private String accountType; // CASH, BANK

        @Column(length = 64)
        private String accountNumber;

        @Column(length = 128)
        private String bankName;

        @Column(length = 32)
        private String ifsc;

        @Column(length = 128)
        private String branch;

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

    @Entity
    @Table(name = "contra_transfers")
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContraTransfer {

        @Id
        @Column(length = 32)
        private String id; // e.g. TRF-0001

        @Column(nullable = false)
        private LocalDate transferDate;

        @Column(nullable = false, length = 32)
        private String fromAccountId;

        @Column(nullable = false, length = 32)
        private String toAccountId;

        @Column(nullable = false, precision = 15, scale = 2)
        private BigDecimal amount;

        @Column(length = 64)
        private String referenceNumber;

        @Column(columnDefinition = "TEXT")
        private String reason;

        @Column(nullable = false, length = 128)
        private String performedBy;

        @CreationTimestamp
        @Column(nullable = false, updatable = false)
        private LocalDateTime createdAt;
    }
}
