package com.transport.tms.finance.invoice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class InvoiceDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GenerateInvoiceRequest {
        @NotNull(message = "Invoice date is required")
        private LocalDate date;

        private LocalDate dueDate;

        @NotBlank(message = "Customer ID is required")
        private String customerId;

        private BigDecimal taxRate; // e.g. 5.00 for 5% GTA or 18.00

        @NotEmpty(message = "At least one trip must be selected for invoice generation")
        private List<String> tripIds;

        private String notes;
    }
}
