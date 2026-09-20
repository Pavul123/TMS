package com.transport.tms.governance.approval.controller;

import com.transport.tms.common.response.ApiResponse;
import com.transport.tms.governance.approval.service.ApprovalService;
import com.transport.tms.governance.correction.dto.CorrectionDto;
import com.transport.tms.governance.correction.entity.CorrectionRequest;
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
@RequestMapping("/api/v1/approvals")
@RequiredArgsConstructor
@Tag(name = "Approval Engine", description = "2-Person Rule correction requests and approval queue")
public class ApprovalController {

    private final ApprovalService approvalService;

    @PostMapping("/requests")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_WORKER', 'ROLE_ACCOUNTS', 'TRIP_EDIT_REQUEST')")
    @Operation(summary = "Submit a correction request for a locked record")
    public ResponseEntity<ApiResponse<CorrectionRequest>> submitRequest(
            @Valid @RequestBody CorrectionDto.SubmitCorrectionRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        CorrectionRequest created = approvalService.submitCorrectionRequest(request, currentUser);
        return ResponseEntity.ok(ApiResponse.ok("Correction request submitted for approval", created));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MD', 'ROLE_MANAGER', 'TRIP_APPROVE')")
    @Operation(summary = "Get all pending approvals in the review queue")
    public ResponseEntity<ApiResponse<List<CorrectionRequest>>> getPendingApprovals() {
        return ResponseEntity.ok(ApiResponse.ok(approvalService.getPendingApprovals()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MD', 'ROLE_MANAGER')")
    @Operation(summary = "Get correction request by ID")
    public ResponseEntity<ApiResponse<CorrectionRequest>> getApprovalById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(approvalService.getCorrectionById(id)));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MD', 'ROLE_MANAGER', 'TRIP_APPROVE')")
    @Operation(summary = "Approve correction and apply changes to database")
    public ResponseEntity<ApiResponse<CorrectionRequest>> approve(
            @PathVariable String id,
            @RequestBody(required = false) CorrectionDto.ReviewActionRequest body,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        String comment = body != null ? body.getComment() : "Approved by " + currentUser.getFullName();
        CorrectionRequest approved = approvalService.approveCorrection(id, comment, currentUser);
        return ResponseEntity.ok(ApiResponse.ok("Correction approved and changes applied successfully", approved));
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MD', 'ROLE_MANAGER', 'TRIP_APPROVE')")
    @Operation(summary = "Reject correction request without altering database")
    public ResponseEntity<ApiResponse<CorrectionRequest>> reject(
            @PathVariable String id,
            @RequestBody(required = false) CorrectionDto.ReviewActionRequest body,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        String comment = body != null ? body.getComment() : "Rejected by " + currentUser.getFullName();
        CorrectionRequest rejected = approvalService.rejectCorrection(id, comment, currentUser);
        return ResponseEntity.ok(ApiResponse.ok("Correction request rejected", rejected));
    }
}
