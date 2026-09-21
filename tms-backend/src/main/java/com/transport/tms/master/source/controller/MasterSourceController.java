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
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_ACCOUNTS')")
    @Operation(summary = "Create source / crusher")
    public ResponseEntity<ApiResponse<Source>> createSource(@Valid @RequestBody Source source) {
        return ResponseEntity.ok(ApiResponse.ok("Source created", masterSourceService.createSource(source)));
    }

    @PutMapping("/sources/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_ACCOUNTS')")
    @Operation(summary = "Update source / crusher")
    public ResponseEntity<ApiResponse<Source>> updateSource(@PathVariable String id, @Valid @RequestBody Source source) {
        return ResponseEntity.ok(ApiResponse.ok("Source updated", masterSourceService.updateSource(id, source)));
    }

    @DeleteMapping("/sources/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    @Operation(summary = "Delete source / crusher")
    public ResponseEntity<ApiResponse<Void>> deleteSource(@PathVariable String id) {
        masterSourceService.deleteSource(id);
        return ResponseEntity.ok(ApiResponse.ok("Source deleted successfully", null));
    }

    // Materials
    @GetMapping("/materials")
    @Operation(summary = "Get all materials")
    public ResponseEntity<ApiResponse<List<Material>>> getAllMaterials() {
        return ResponseEntity.ok(ApiResponse.ok(masterSourceService.getAllMaterials()));
    }

    @PostMapping("/materials")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_ACCOUNTS')")
    @Operation(summary = "Create material")
    public ResponseEntity<ApiResponse<Material>> createMaterial(@Valid @RequestBody Material material) {
        return ResponseEntity.ok(ApiResponse.ok("Material created", masterSourceService.createMaterial(material)));
    }

    @PutMapping("/materials/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_ACCOUNTS')")
    @Operation(summary = "Update material")
    public ResponseEntity<ApiResponse<Material>> updateMaterial(@PathVariable String id, @Valid @RequestBody Material material) {
        return ResponseEntity.ok(ApiResponse.ok("Material updated", masterSourceService.updateMaterial(id, material)));
    }

    @DeleteMapping("/materials/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    @Operation(summary = "Delete material")
    public ResponseEntity<ApiResponse<Void>> deleteMaterial(@PathVariable String id) {
        masterSourceService.deleteMaterial(id);
        return ResponseEntity.ok(ApiResponse.ok("Material deleted successfully", null));
    }

    // Locations
    @GetMapping("/locations")
    @Operation(summary = "Get all site / yard locations")
    public ResponseEntity<ApiResponse<List<LocationItem>>> getAllLocations() {
        return ResponseEntity.ok(ApiResponse.ok(masterSourceService.getAllLocations()));
    }

    @PostMapping("/locations")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_ACCOUNTS')")
    @Operation(summary = "Create location")
    public ResponseEntity<ApiResponse<LocationItem>> createLocation(@Valid @RequestBody LocationItem location) {
        return ResponseEntity.ok(ApiResponse.ok("Location created", masterSourceService.createLocation(location)));
    }

    @PutMapping("/locations/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_ACCOUNTS')")
    @Operation(summary = "Update location")
    public ResponseEntity<ApiResponse<LocationItem>> updateLocation(@PathVariable String id, @Valid @RequestBody LocationItem location) {
        return ResponseEntity.ok(ApiResponse.ok("Location updated", masterSourceService.updateLocation(id, location)));
    }

    @DeleteMapping("/locations/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    @Operation(summary = "Delete location")
    public ResponseEntity<ApiResponse<Void>> deleteLocation(@PathVariable String id) {
        masterSourceService.deleteLocation(id);
        return ResponseEntity.ok(ApiResponse.ok("Location deleted successfully", null));
    }

    // Fuel Stations
    @GetMapping("/fuel-stations")
    @Operation(summary = "Get all fuel pump stations")
    public ResponseEntity<ApiResponse<List<FuelStation>>> getAllFuelStations() {
        return ResponseEntity.ok(ApiResponse.ok(masterSourceService.getAllFuelStations()));
    }

    @PostMapping("/fuel-stations")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_ACCOUNTS')")
    @Operation(summary = "Create fuel station")
    public ResponseEntity<ApiResponse<FuelStation>> createFuelStation(@Valid @RequestBody FuelStation fuelStation) {
        return ResponseEntity.ok(ApiResponse.ok("Fuel station created", masterSourceService.createFuelStation(fuelStation)));
    }

    @PutMapping("/fuel-stations/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_ACCOUNTS')")
    @Operation(summary = "Update fuel station")
    public ResponseEntity<ApiResponse<FuelStation>> updateFuelStation(@PathVariable String id, @Valid @RequestBody FuelStation fuelStation) {
        return ResponseEntity.ok(ApiResponse.ok("Fuel station updated", masterSourceService.updateFuelStation(id, fuelStation)));
    }

    @DeleteMapping("/fuel-stations/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    @Operation(summary = "Delete fuel station")
    public ResponseEntity<ApiResponse<Void>> deleteFuelStation(@PathVariable String id) {
        masterSourceService.deleteFuelStation(id);
        return ResponseEntity.ok(ApiResponse.ok("Fuel station deleted successfully", null));
    }
}
