package com.transport.tms.governance.audit.controller;

import com.transport.tms.common.response.ApiResponse;
import com.transport.tms.governance.audit.entity.AuditLog;
import com.transport.tms.governance.audit.service.AuditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/audit-logs")
@RequiredArgsConstructor
@Tag(name = "Audit Trail", description = "System-wide immutable audit trail and change history")
public class AuditLogController {

    private final AuditService auditService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MD', 'AUDIT_VIEW')")
    @Operation(summary = "Get recent 100 audit log entries")
    public ResponseEntity<ApiResponse<List<AuditLog>>> getRecentAuditLogs() {
        return ResponseEntity.ok(ApiResponse.ok(auditService.getRecentAuditLogs()));
    }

    @GetMapping("/{entityType}/{entityId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MD', 'ROLE_MANAGER', 'AUDIT_VIEW')")
    @Operation(summary = "Get audit trail for specific business entity (Trip, Invoice, Payment, etc.)")
    public ResponseEntity<ApiResponse<List<AuditLog>>> getEntityAuditLogs(
            @PathVariable String entityType,
            @PathVariable String entityId) {
        return ResponseEntity.ok(ApiResponse.ok(auditService.getAuditLogsForEntity(entityType, entityId)));
    }
}
