package com.transport.tms.finance.invoice.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "invoice_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    @JsonBackReference
    private Invoice invoice;

    @Column(nullable = false, length = 32)
    private String tripId;

    @Column(nullable = false)
    private LocalDate tripDate;

    @Column(nullable = false, length = 32)
    private String vehicle;

    @Column(nullable = false, length = 128)
    private String material;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal quantity;

    @Column(nullable = false, length = 32)
    private String unit;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal rate;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;
}
