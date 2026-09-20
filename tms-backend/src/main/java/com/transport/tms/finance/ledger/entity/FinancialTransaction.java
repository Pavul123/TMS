package com.transport.tms.finance.ledger.entity;

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

@Entity
@Table(name = "financial_transactions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialTransaction {

    @Id
    @Column(length = 32)
    private String id; // e.g. TXN-02481

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false, length = 32)
    private String entityType; // CUSTOMER, DRIVER, VEHICLE, VENDOR, BANK, CASH

    @Column(nullable = false, length = 32)
    private String entityId;

    @Column(nullable = false, length = 128)
    private String entityName;

    @Column(nullable = false, length = 64)
    private String transactionType; // RECEIVABLE, CUSTOMER_PAYMENT, DRIVER_ADVANCE, DIESEL, MAINTENANCE, TRANSFER, SALARY

    @Column(nullable = false, length = 64)
    private String category; // Operating, Revenue, Fleet, Labor, Contra

    @Column(nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal debit = BigDecimal.ZERO;

    @Column(nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal credit = BigDecimal.ZERO;

    @Column(nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(length = 32)
    private String paymentMode; // CASH, BANK, NEFT, RTGS, CHEQUE

    @Column(length = 64)
    private String referenceId; // e.g. INV-0001, PAY-0001

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 128)
    private String createdBy;

    @Column(nullable = false, length = 32)
    @Builder.Default
    private String status = "POSTED"; // POSTED, REVERSED, CANCELLED

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
