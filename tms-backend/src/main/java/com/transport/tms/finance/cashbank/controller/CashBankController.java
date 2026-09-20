package com.transport.tms.finance.cashbank.controller;

import com.transport.tms.common.response.ApiResponse;
import com.transport.tms.finance.cashbank.entity.CashBankEntities.CashBankAccount;
import com.transport.tms.finance.cashbank.entity.CashBankEntities.ContraTransfer;
import com.transport.tms.finance.cashbank.service.CashBankService;
import com.transport.tms.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
@Tag(name = "Cash & Bank Accounts", description = "Cash-in-hand, Corporate Bank accounts, and Contra transfers")
public class CashBankController {

    private final CashBankService cashBankService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ACCOUNTS', 'ROLE_MD')")
    @Operation(summary = "Get all cash and bank accounts with live balances")
    public ResponseEntity<ApiResponse<List<CashBankAccount>>> getAllAccounts() {
        return ResponseEntity.ok(ApiResponse.ok(cashBankService.getAllAccounts()));
    }

    @GetMapping("/transfers")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ACCOUNTS', 'ROLE_MD')")
    @Operation(summary = "Get all Contra transfer histories")
    public ResponseEntity<ApiResponse<List<ContraTransfer>>> getAllTransfers() {
        return ResponseEntity.ok(ApiResponse.ok(cashBankService.getAllTransfers()));
    }

    @PostMapping("/transfer")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ACCOUNTS', 'CASHBANK_MANAGE')")
    @Operation(summary = "Execute internal Contra transfer (e.g. Cash in Hand -> Corporate Bank)")
    public ResponseEntity<ApiResponse<ContraTransfer>> executeContraTransfer(
            @RequestBody ContraTransferRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        ContraTransfer transfer = cashBankService.executeContraTransfer(
                request.getFromAccountId(),
                request.getToAccountId(),
                request.getAmount(),
                request.getReferenceNumber(),
                request.getReason(),
                currentUser.getFullName()
        );
        return ResponseEntity.ok(ApiResponse.ok("Contra transfer executed successfully", transfer));
    }

    @Data
    public static class ContraTransferRequest {
        private String fromAccountId;
        private String toAccountId;
        private BigDecimal amount;
        private String referenceNumber;
        private String reason;
    }
}
