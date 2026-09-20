package com.transport.tms.finance.diesel.entity;

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

public class DieselEntities {

    @Entity
    @Table(name = "diesel_logs")
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DieselLog {

        @Id
        @Column(length = 32)
        private String id; // DSL-0001

        @Column(nullable = false)
        private LocalDate date;

        @Column(nullable = false, length = 32)
        private String vehicleRegistration;

        @Column(nullable = false, length = 32)
        private String driverId;

        @Column(nullable = false, length = 128)
        private String driverName;

        @Column(nullable = false, length = 32)
        private String fuelStationId;

        @Column(nullable = false, length = 128)
        private String fuelStationName;

        @Column(length = 64)
        private String billNumber;

        @Column(nullable = false, precision = 10, scale = 2)
        private BigDecimal litres;

        @Column(nullable = false, precision = 10, scale = 2)
        private BigDecimal ratePerLitre;

        @Column(nullable = false, precision = 15, scale = 2)
        private BigDecimal totalAmount;

        @Column(precision = 12, scale = 2)
        private BigDecimal startKm;

        @Column(precision = 12, scale = 2)
        private BigDecimal endKm;

        @Column(precision = 12, scale = 2)
        private BigDecimal kmRun;

        @Column(precision = 8, scale = 2)
        private BigDecimal mileage;

        @Column(nullable = false, length = 32)
        @Builder.Default
        private String paymentMode = "CREDIT";

        @Column(nullable = false, length = 32)
        @Builder.Default
        private String status = "RECORDED";

        @Column(columnDefinition = "TEXT")
        private String notes;

        @Column(nullable = false, length = 128)
        private String enteredBy;

        @CreationTimestamp
        @Column(nullable = false, updatable = false)
        private LocalDateTime createdAt;
    }

    @Entity
    @Table(name = "vehicle_expenses")
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VehicleExpense {

        @Id
        @Column(length = 32)
        private String id; // EXP-0001

        @Column(nullable = false)
        private LocalDate date;

        @Column(nullable = false, length = 32)
        private String vehicleRegistration;

        @Column(nullable = false, length = 64)
        private String expenseType; // Tyre, Oil Change, Grease, Toll, Spare Parts, Service

        @Column(nullable = false, precision = 15, scale = 2)
        private BigDecimal amount;

        @Column(length = 128)
        private String vendorName;

        @Column(length = 64)
        private String invoiceBillNo;

        @Column(nullable = false, length = 32)
        @Builder.Default
        private String paymentMode = "CASH";

        @Column(nullable = false, length = 32)
        @Builder.Default
        private String status = "PAID";

        @Column(columnDefinition = "TEXT")
        private String description;

        @Column(nullable = false, length = 128)
        private String enteredBy;

        @CreationTimestamp
        @Column(nullable = false, updatable = false)
        private LocalDateTime createdAt;
    }
}
