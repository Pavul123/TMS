package com.transport.tms.master.source.controller;

import com.transport.tms.common.response.ApiResponse;
import com.transport.tms.master.source.entity.FuelStation;
import com.transport.tms.master.source.entity.LocationItem;
import com.transport.tms.master.source.entity.Material;
import com.transport.tms.master.source.entity.Source;
import com.transport.tms.master.source.service.MasterSourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Masters Catalog", description = "Sources, Materials, Locations and Fuel Stations")
public class MasterSourceController {

    private final MasterSourceService masterSourceService;

    // Sources (Crushers/Quarries)
    @GetMapping("/sources")
    @Operation(summary = "Get all sources / crushers")
    public ResponseEntity<ApiResponse<List<Source>>> getAllSources() {
        return ResponseEntity.ok(ApiResponse.ok(masterSourceService.getAllSources()));
    }

    @PostMapping("/sources")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    @Operation(summary = "Create source / crusher")
    public ResponseEntity<ApiResponse<Source>> createSource(@Valid @RequestBody Source source) {
        return ResponseEntity.ok(ApiResponse.ok("Source created", masterSourceService.createSource(source)));
    }

    // Materials
    @GetMapping("/materials")
    @Operation(summary = "Get all materials")
    public ResponseEntity<ApiResponse<List<Material>>> getAllMaterials() {
        return ResponseEntity.ok(ApiResponse.ok(masterSourceService.getAllMaterials()));
    }

    @PostMapping("/materials")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    @Operation(summary = "Create material")
    public ResponseEntity<ApiResponse<Material>> createMaterial(@Valid @RequestBody Material material) {
        return ResponseEntity.ok(ApiResponse.ok("Material created", masterSourceService.createMaterial(material)));
    }

    // Locations
    @GetMapping("/locations")
    @Operation(summary = "Get all site / yard locations")
    public ResponseEntity<ApiResponse<List<LocationItem>>> getAllLocations() {
        return ResponseEntity.ok(ApiResponse.ok(masterSourceService.getAllLocations()));
    }

    @PostMapping("/locations")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    @Operation(summary = "Create location")
    public ResponseEntity<ApiResponse<LocationItem>> createLocation(@Valid @RequestBody LocationItem location) {
        return ResponseEntity.ok(ApiResponse.ok("Location created", masterSourceService.createLocation(location)));
    }

    // Fuel Stations
    @GetMapping("/fuel-stations")
    @Operation(summary = "Get all fuel pump stations")
    public ResponseEntity<ApiResponse<List<FuelStation>>> getAllFuelStations() {
        return ResponseEntity.ok(ApiResponse.ok(masterSourceService.getAllFuelStations()));
    }

    @PostMapping("/fuel-stations")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    @Operation(summary = "Create fuel station")
    public ResponseEntity<ApiResponse<FuelStation>> createFuelStation(@Valid @RequestBody FuelStation fuelStation) {
        return ResponseEntity.ok(ApiResponse.ok("Fuel station created", masterSourceService.createFuelStation(fuelStation)));
    }
}
