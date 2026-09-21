package com.transport.tms.finance.diesel.controller;

import com.transport.tms.common.response.ApiResponse;
import com.transport.tms.finance.diesel.entity.DieselEntities.DieselLog;
import com.transport.tms.finance.diesel.entity.DieselEntities.VehicleExpense;
import com.transport.tms.finance.diesel.service.DieselExpenseService;
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
@RequestMapping({"/api/v1/fleet-expenses", "/api/v1/diesel"})
@RequiredArgsConstructor
@Tag(name = "Diesel & Vehicle Expenses", description = "Diesel logs, mileage tracking, and maintenance expense records")
public class DieselExpenseController {

    private final DieselExpenseService service;

    @GetMapping({"/diesel", ""})
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ACCOUNTS', 'ROLE_MANAGER', 'ROLE_MD', 'ROLE_WORKER')")
    @Operation(summary = "Get all diesel fill logs")
    public ResponseEntity<ApiResponse<List<DieselLog>>> getAllDiesel() {
        return ResponseEntity.ok(ApiResponse.ok(service.getAllDieselLogs()));
    }

    @PostMapping({"/diesel", ""})
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ACCOUNTS', 'ROLE_MANAGER', 'ROLE_WORKER', 'DIESEL_MANAGE')")
    @Operation(summary = "Record diesel receipt and compute mileage")
    public ResponseEntity<ApiResponse<DieselLog>> recordDiesel(
            @RequestBody DieselLog log,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.ok("Diesel entry recorded", service.recordDiesel(log, currentUser)));
    }

    @DeleteMapping("/diesel/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ACCOUNTS', 'ROLE_MANAGER')")
    @Operation(summary = "Delete diesel log")
    public ResponseEntity<ApiResponse<Void>> deleteDiesel(@PathVariable String id) {
        service.deleteDieselLog(id);
        return ResponseEntity.ok(ApiResponse.ok("Diesel log deleted successfully", null));
    }

    @GetMapping("/maintenance")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ACCOUNTS', 'ROLE_MANAGER', 'ROLE_MD')")
    @Operation(summary = "Get all vehicle maintenance & operating expenses")
    public ResponseEntity<ApiResponse<List<VehicleExpense>>> getAllExpenses() {
        return ResponseEntity.ok(ApiResponse.ok(service.getAllExpenses()));
    }

    @PostMapping("/maintenance")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ACCOUNTS', 'ROLE_MANAGER')")
    @Operation(summary = "Record vehicle maintenance / tyre / grease expense")
    public ResponseEntity<ApiResponse<VehicleExpense>> recordExpense(
            @RequestBody VehicleExpense expense,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.ok("Vehicle expense recorded", service.recordExpense(expense, currentUser)));
    }

    @DeleteMapping("/maintenance/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ACCOUNTS', 'ROLE_MANAGER')")
    @Operation(summary = "Delete vehicle maintenance expense")
    public ResponseEntity<ApiResponse<Void>> deleteMaintenanceExpense(@PathVariable String id) {
        service.deleteExpense(id);
        return ResponseEntity.ok(ApiResponse.ok("Expense deleted successfully", null));
    }
}
