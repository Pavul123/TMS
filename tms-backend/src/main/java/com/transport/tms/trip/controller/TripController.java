package com.transport.tms.trip.controller;

import com.transport.tms.common.response.ApiResponse;
import com.transport.tms.security.UserPrincipal;
import com.transport.tms.trip.dto.TripDto;
import com.transport.tms.trip.entity.Trip;
import com.transport.tms.trip.service.TripService;
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
@RequestMapping("/api/v1/trips")
@RequiredArgsConstructor
@Tag(name = "Trip Operations", description = "Daily tipper trip creation, tracking, and status transitions")
public class TripController {

    private final TripService tripService;

    @GetMapping
    @Operation(summary = "Get all trips (Sanitized according to user role)")
    public ResponseEntity<ApiResponse<List<?>>> getAllTrips(
            @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.ok(tripService.getAllTrips(currentUser)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get trip by ID")
    public ResponseEntity<ApiResponse<Object>> getTripById(
            @PathVariable String id,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.ok(tripService.getTripById(id, currentUser)));
    }

    @GetMapping("/unbilled")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ACCOUNTS', 'ROLE_MD', 'ROLE_MANAGER')")
    @Operation(summary = "Get unbilled completed trips for invoice generation")
    public ResponseEntity<ApiResponse<List<Trip>>> getUnbilledTrips(
            @RequestParam(required = false) String customerId) {
        return ResponseEntity.ok(ApiResponse.ok(tripService.getUnbilledTrips(customerId)));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_WORKER', 'TRIP_CREATE')")
    @Operation(summary = "Create a new trip (Worker / Operations flow)")
    public ResponseEntity<ApiResponse<Object>> createTrip(
            @Valid @RequestBody TripDto.CreateTripRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        Object response = tripService.createTrip(request, currentUser);
        return ResponseEntity.ok(ApiResponse.ok("Trip created successfully", response));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_WORKER')")
    @Operation(summary = "Update trip operational status")
    public ResponseEntity<ApiResponse<Trip>> updateTripStatus(
            @PathVariable String id,
            @RequestParam String status,
            @RequestParam(required = false) String comment,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        Trip trip = tripService.updateTripStatus(id, status, comment, currentUser);
        return ResponseEntity.ok(ApiResponse.ok("Trip status updated", trip));
    }
}
