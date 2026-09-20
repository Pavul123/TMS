package com.transport.tms.master.source.service;

import com.transport.tms.common.exception.Exceptions;
import com.transport.tms.master.source.entity.FuelStation;
import com.transport.tms.master.source.entity.LocationItem;
import com.transport.tms.master.source.entity.Material;
import com.transport.tms.master.source.entity.Source;
import com.transport.tms.master.source.repository.FuelStationRepository;
import com.transport.tms.master.source.repository.LocationRepository;
import com.transport.tms.master.source.repository.MaterialRepository;
import com.transport.tms.master.source.repository.SourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MasterSourceService {

    private final SourceRepository sourceRepository;
    private final MaterialRepository materialRepository;
    private final LocationRepository locationRepository;
    private final FuelStationRepository fuelStationRepository;

    // Sources (Crushers)
    @Transactional(readOnly = true)
    public List<Source> getAllSources() {
        return sourceRepository.findAll();
    }

    @Transactional
    public Source createSource(Source source) {
        if (source.getId() == null || source.getId().isBlank()) {
            source.setId("SRC-" + String.format("%03d", (int)(Math.random() * 900) + 100));
        }
        return sourceRepository.save(source);
    }

    // Materials
    @Transactional(readOnly = true)
    public List<Material> getAllMaterials() {
        return materialRepository.findAll();
    }

    @Transactional
    public Material createMaterial(Material material) {
        if (material.getId() == null || material.getId().isBlank()) {
            material.setId("MAT-" + String.format("%03d", (int)(Math.random() * 900) + 100));
        }
        return materialRepository.save(material);
    }

    // Locations
    @Transactional(readOnly = true)
    public List<LocationItem> getAllLocations() {
        return locationRepository.findAll();
    }

    @Transactional
    public LocationItem createLocation(LocationItem location) {
        if (location.getId() == null || location.getId().isBlank()) {
            location.setId("LOC-" + String.format("%03d", (int)(Math.random() * 900) + 100));
        }
        return locationRepository.save(location);
    }

    // Fuel Stations
    @Transactional(readOnly = true)
    public List<FuelStation> getAllFuelStations() {
        return fuelStationRepository.findAll();
    }

    @Transactional
    public FuelStation createFuelStation(FuelStation fuelStation) {
        if (fuelStation.getId() == null || fuelStation.getId().isBlank()) {
            fuelStation.setId("PMP-" + String.format("%03d", (int)(Math.random() * 900) + 100));
        }
        return fuelStationRepository.save(fuelStation);
    }
}
