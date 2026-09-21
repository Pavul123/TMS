package com.transport.tms.dashboard.controller;

import com.transport.tms.common.response.ApiResponse;
import com.transport.tms.dashboard.dto.DashboardDtos;
import com.transport.tms.dashboard.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/v1/dashboard", "/api/v1/dashboards"})
@RequiredArgsConstructor
@Tag(name = "Executive Dashboards", description = "Real-time derived analytics and KPIs for MD, Accounts, and Operations")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping({"/md", "/md-cockpit"})
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MD')")
    @Operation(summary = "Get MD Executive Cockpit metrics (Revenue, Profit, Utilization, Truck P&L)")
    public ResponseEntity<ApiResponse<DashboardDtos.MdDashboardDto>> getMdDashboard() {
        return ResponseEntity.ok(ApiResponse.ok(dashboardService.getMdDashboard()));
    }

    @GetMapping({"/accounts", "/accounts-position"})
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ACCOUNTS', 'ROLE_MD')")
    @Operation(summary = "Get Accounts Financial Position metrics")
    public ResponseEntity<ApiResponse<DashboardDtos.AccountsDashboardDto>> getAccountsDashboard() {
        return ResponseEntity.ok(ApiResponse.ok(dashboardService.getAccountsDashboard()));
    }

    @GetMapping({"/manager", "/operations-dispatch"})
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_MD')")
    @Operation(summary = "Get Operations Manager Dispatch KPIs")
    public ResponseEntity<ApiResponse<DashboardDtos.ManagerDashboardDto>> getManagerDashboard() {
        return ResponseEntity.ok(ApiResponse.ok(dashboardService.getManagerDashboard()));
    }
}
