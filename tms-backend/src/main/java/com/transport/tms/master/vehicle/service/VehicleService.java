package com.transport.tms.master.vehicle.service;

import com.transport.tms.common.exception.Exceptions;
import com.transport.tms.master.vehicle.entity.Vehicle;
import com.transport.tms.master.vehicle.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    @Transactional(readOnly = true)
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Vehicle getVehicleByRegistration(String registration) {
        return vehicleRepository.findById(registration)
                .orElseThrow(() -> new Exceptions.ResourceNotFoundException("Vehicle", "registration", registration));
    }

    @Transactional(readOnly = true)
    public List<Vehicle> getAvailableVehicles() {
        return vehicleRepository.findByStatus("AVAILABLE");
    }

    @Transactional
    public Vehicle createVehicle(Vehicle vehicle) {
        if (vehicleRepository.existsById(vehicle.getRegistration().trim())) {
            throw new Exceptions.BadRequestException("Vehicle with registration " + vehicle.getRegistration() + " already exists!");
        }
        vehicle.setRegistration(vehicle.getRegistration().trim());
        return vehicleRepository.save(vehicle);
    }

    @Transactional
    public Vehicle updateVehicle(String registration, Vehicle request) {
        Vehicle vehicle = getVehicleByRegistration(registration);
        vehicle.setType(request.getType());
        vehicle.setOwnership(request.getOwnership());
        vehicle.setCapacity(request.getCapacity());
        vehicle.setFuelCapacity(request.getFuelCapacity());
        vehicle.setCurrentKm(request.getCurrentKm());
        vehicle.setLastMaintenanceDate(request.getLastMaintenanceDate());
        vehicle.setMaintenanceFee(request.getMaintenanceFee());
        vehicle.setInsuranceExpiry(request.getInsuranceExpiry());
        vehicle.setFcExpiry(request.getFcExpiry());
        vehicle.setPermitExpiry(request.getPermitExpiry());
        vehicle.setAssignedDriverId(request.getAssignedDriverId());
        vehicle.setAssignedDriverName(request.getAssignedDriverName());
        vehicle.setStatus(request.getStatus());
        return vehicleRepository.save(vehicle);
    }

    @Transactional
    public Vehicle updateVehicleStatus(String registration, String status) {
        Vehicle vehicle = getVehicleByRegistration(registration);
        vehicle.setStatus(status);
        return vehicleRepository.save(vehicle);
    }
}
