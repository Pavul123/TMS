package com.transport.tms.finance.payment.entity;

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
@Table(name = "payments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @Column(length = 32)
    private String id; // e.g. PAY-0001

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false, length = 32)
    private String customerId;

    @Column(nullable = false, length = 128)
    private String customerName;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 32)
    private String paymentMode; // CASH, BANK, NEFT, RTGS, CHEQUE, UPI

    @Column(length = 64)
    private String referenceNumber; // Bank UTR or Cheque number

    @Column(nullable = false, length = 32)
    private String accountId; // Reference to Cash/Bank account

    @Column(nullable = false, length = 32)
    @Builder.Default
    private String status = "POSTED"; // POSTED, REVERSED, CANCELLED

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(nullable = false, length = 128)
    private String receivedBy;

    @Version
    @Column(nullable = false)
    private Long version;

    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    @Builder.Default
    private List<PaymentAllocation> allocations = new ArrayList<>();

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
