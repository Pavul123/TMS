package com.transport.tms.finance.invoice.controller;

import com.transport.tms.common.response.ApiResponse;
import com.transport.tms.finance.invoice.dto.InvoiceDto;
import com.transport.tms.finance.invoice.entity.Invoice;
import com.transport.tms.finance.invoice.service.InvoiceService;
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
@RequestMapping("/api/v1/invoices")
@RequiredArgsConstructor
@Tag(name = "Invoicing", description = "Multi-trip GST invoice generation and customer statements")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ACCOUNTS', 'ROLE_MD', 'ROLE_MANAGER')")
    @Operation(summary = "Get all invoices")
    public ResponseEntity<ApiResponse<List<Invoice>>> getAllInvoices(
            @RequestParam(required = false) String customerId) {
        if (customerId != null && !customerId.isBlank()) {
            return ResponseEntity.ok(ApiResponse.ok(invoiceService.getInvoicesByCustomer(customerId)));
        }
        return ResponseEntity.ok(ApiResponse.ok(invoiceService.getAllInvoices()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ACCOUNTS', 'ROLE_MD', 'ROLE_MANAGER')")
    @Operation(summary = "Get invoice by ID with line items")
    public ResponseEntity<ApiResponse<Invoice>> getInvoiceById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(invoiceService.getInvoiceById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ACCOUNTS', 'INVOICE_CREATE')")
    @Operation(summary = "Generate GST Invoice from selected completed trips")
    public ResponseEntity<ApiResponse<Invoice>> generateInvoice(
            @Valid @RequestBody InvoiceDto.GenerateInvoiceRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        Invoice invoice = invoiceService.generateInvoice(request, currentUser);
        return ResponseEntity.ok(ApiResponse.ok("Invoice generated successfully", invoice));
    }
}
