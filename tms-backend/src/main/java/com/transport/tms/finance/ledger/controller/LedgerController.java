package com.transport.tms.finance.ledger.controller;

import com.transport.tms.common.response.ApiResponse;
import com.transport.tms.finance.ledger.entity.FinancialTransaction;
import com.transport.tms.finance.ledger.service.LedgerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ledger")
@RequiredArgsConstructor
@Tag(name = "Central Financial Ledger", description = "Immutable double-entry financial transaction statements")
public class LedgerController {

    private final LedgerService ledgerService;

    @GetMapping("/transactions")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ACCOUNTS', 'ROLE_MD')")
    @Operation(summary = "Get central financial transactions history")
    public ResponseEntity<ApiResponse<List<FinancialTransaction>>> getAllTransactions() {
        return ResponseEntity.ok(ApiResponse.ok(ledgerService.getAllTransactions()));
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ACCOUNTS', 'ROLE_MD', 'ROLE_MANAGER')")
    @Operation(summary = "Get customer ledger statements and transaction history")
    public ResponseEntity<ApiResponse<List<FinancialTransaction>>> getCustomerTransactions(
            @PathVariable String customerId) {
        return ResponseEntity.ok(ApiResponse.ok(ledgerService.getCustomerTransactions(customerId)));
    }
}
