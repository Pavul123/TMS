package com.transport.tms.governance.correction.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class CorrectionDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FieldChangeRequest {
        @NotBlank(message = "Field name is required")
        private String field;

        @NotBlank(message = "New value is required")
        private String newValue;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubmitCorrectionRequest {
        @NotBlank(message = "Entity type is required")
        private String entityType; // TRIP, INVOICE, PAYMENT, CUSTOMER, DIESEL

        @NotBlank(message = "Entity ID is required")
        private String entityId;

        @NotBlank(message = "Reason for change is mandatory")
        private String reason;

        @NotEmpty(message = "At least one field change must be specified")
        private List<FieldChangeRequest> changes;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewActionRequest {
        private String comment;
    }
}
