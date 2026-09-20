package com.transport.tms.finance.invoice.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invoices")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {

    @Id
    @Column(length = 32)
    private String id; // e.g. INV-0001

    @Column(nullable = false)
    private LocalDate date;

    private LocalDate dueDate;

    @Column(nullable = false, length = 32)
    private String customerId;

    @Column(nullable = false, length = 128)
    private String customerName;

    @Column(length = 32)
    private String customerGstin;

    @Column(nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(nullable = false, precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal taxRate = BigDecimal.ZERO; // e.g. 5.00 for GTA or 18.00

    @Column(nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal paidAmount = BigDecimal.ZERO;

    @Column(nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal balanceAmount = BigDecimal.ZERO;

    @Column(nullable = false, length = 32)
    @Builder.Default
    private String status = "GENERATED"; // DRAFT, GENERATED, PARTIALLY_PAID, PAID, CANCELLED

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(nullable = false, length = 128)
    private String generatedBy;

    @Version
    @Column(nullable = false)
    @Builder.Default
    private Long version = 0L;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    @Builder.Default
    private List<InvoiceItem> items = new ArrayList<>();

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
