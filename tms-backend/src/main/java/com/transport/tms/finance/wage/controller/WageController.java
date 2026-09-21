package com.transport.tms.finance.wage.controller;

import com.transport.tms.common.response.ApiResponse;
import com.transport.tms.finance.wage.entity.WageEntities.WageAdvance;
import com.transport.tms.finance.wage.entity.WageEntities.WorkerWage;
import com.transport.tms.finance.wage.service.WageService;
import com.transport.tms.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/wages")
@RequiredArgsConstructor
@Tag(name = "Wages & Driver Advances", description = "Driver cash advances, monthly salary calculations and wage settlements")
public class WageController {

    private final WageService wageService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ACCOUNTS', 'ROLE_MD')")
    @Operation(summary = "Get all monthly wage settlements")
    public ResponseEntity<ApiResponse<List<WorkerWage>>> getAllWages() {
        return ResponseEntity.ok(ApiResponse.ok(wageService.getAllWages()));
    }

    @PostMapping({"/disburse", "/pay", "/settle"})
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ACCOUNTS', 'WAGE_MANAGE')")
    @Operation(summary = "Calculate and disburse monthly wage (Base + Overtime + Allowance - Advances)")
    public ResponseEntity<ApiResponse<WorkerWage>> disburseWage(
            @RequestBody WorkerWage wage,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.ok("Wage disbursed successfully", wageService.calculateAndDisburseWage(wage, currentUser)));
    }

    @GetMapping("/advances")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ACCOUNTS', 'ROLE_MANAGER', 'ROLE_MD')")
    @Operation(summary = "Get all advance cash entries")
    public ResponseEntity<ApiResponse<List<WageAdvance>>> getAllAdvances() {
        return ResponseEntity.ok(ApiResponse.ok(wageService.getAllAdvances()));
    }

    @PostMapping({"/advances", "/advance"})
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ACCOUNTS', 'WAGE_MANAGE')")
    @Operation(summary = "Issue advance cash to driver or worker")
    public ResponseEntity<ApiResponse<WageAdvance>> issueAdvance(
            @RequestBody WageAdvance advance,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.ok("Advance recorded successfully", wageService.recordAdvance(advance, currentUser)));
    }
}
