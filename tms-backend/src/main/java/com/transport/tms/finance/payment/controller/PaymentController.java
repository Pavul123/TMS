package com.transport.tms.finance.payment.controller;

import com.transport.tms.common.response.ApiResponse;
import com.transport.tms.finance.payment.dto.PaymentDto;
import com.transport.tms.finance.payment.entity.Payment;
import com.transport.tms.finance.payment.service.PaymentService;
import com.transport.tms.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payment Receipts", description = "Customer delayed payment recording and multi-invoice allocations")
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ACCOUNTS', 'ROLE_MD', 'PAYMENT_VIEW')")
    @Operation(summary = "Get all payments history")
    public ResponseEntity<ApiResponse<List<Payment>>> getAllPayments(
            @RequestParam(required = false) String customerId) {
        if (customerId != null && !customerId.isBlank()) {
            return ResponseEntity.ok(ApiResponse.ok(paymentService.getPaymentsByCustomer(customerId)));
        }
        return ResponseEntity.ok(ApiResponse.ok(paymentService.getAllPayments()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ACCOUNTS', 'ROLE_MD', 'PAYMENT_VIEW')")
    @Operation(summary = "Get payment receipt details by ID")
    public ResponseEntity<ApiResponse<Payment>> getPaymentById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(paymentService.getPaymentById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ACCOUNTS', 'PAYMENT_CREATE')")
    @Operation(summary = "Record customer payment and allocate against invoices")
    public ResponseEntity<ApiResponse<Payment>> recordPayment(
            @Valid @RequestBody PaymentDto.RecordPaymentRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        Payment payment = paymentService.recordPayment(request, currentUser);
        return ResponseEntity.ok(ApiResponse.ok("Payment recorded successfully", payment));
    }

    @PostMapping("/{id}/reverse")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ACCOUNTS', 'PAYMENT_REVERSE')")
    @Operation(summary = "Reverse invalid payment record (Creates audit trail and restores invoice balance)")
    public ResponseEntity<ApiResponse<Payment>> reversePayment(
            @PathVariable String id,
            @Valid @RequestBody PaymentDto.ReversePaymentRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        Payment reversed = paymentService.reversePayment(id, request.getReason(), currentUser);
        return ResponseEntity.ok(ApiResponse.ok("Payment reversed successfully", reversed));
    }
}
