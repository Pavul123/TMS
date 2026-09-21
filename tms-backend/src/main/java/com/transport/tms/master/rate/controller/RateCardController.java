package com.transport.tms.master.rate.controller;

import com.transport.tms.common.response.ApiResponse;
import com.transport.tms.master.rate.entity.ConfiguredRate;
import com.transport.tms.master.rate.service.RateCardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rates")
@RequiredArgsConstructor
@Tag(name = "Rate Cards", description = "Customer & Crusher freight rate configuration")
public class RateCardController {

    private final RateCardService rateCardService;

    @GetMapping
    @Operation(summary = "Get all configured rates")
    public ResponseEntity<ApiResponse<List<ConfiguredRate>>> getAllRates(
            @RequestParam(required = false) String customerId) {
        if (customerId != null && !customerId.isBlank()) {
            return ResponseEntity.ok(ApiResponse.ok(rateCardService.getRatesByCustomer(customerId)));
        }
        return ResponseEntity.ok(ApiResponse.ok(rateCardService.getAllRates()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get rate card by ID")
    public ResponseEntity<ApiResponse<ConfiguredRate>> getRateById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(rateCardService.getRateById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_ACCOUNTS', 'RATE_MANAGE')")
    @Operation(summary = "Create rate card")
    public ResponseEntity<ApiResponse<ConfiguredRate>> createRate(@Valid @RequestBody ConfiguredRate rate) {
        return ResponseEntity.ok(ApiResponse.ok("Rate card created", rateCardService.createRate(rate)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_ACCOUNTS', 'RATE_MANAGE')")
    @Operation(summary = "Update rate card")
    public ResponseEntity<ApiResponse<ConfiguredRate>> updateRate(
            @PathVariable String id,
            @Valid @RequestBody ConfiguredRate rate) {
        return ResponseEntity.ok(ApiResponse.ok("Rate card updated", rateCardService.updateRate(id, rate)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_ACCOUNTS', 'RATE_MANAGE')")
    @Operation(summary = "Delete rate card")
    public ResponseEntity<ApiResponse<Void>> deleteRate(@PathVariable String id) {
        rateCardService.deleteRate(id);
        return ResponseEntity.ok(ApiResponse.ok("Rate card deleted successfully", null));
    }
}
