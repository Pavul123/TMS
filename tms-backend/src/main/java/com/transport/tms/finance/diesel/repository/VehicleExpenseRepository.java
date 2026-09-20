package com.transport.tms.finance.diesel.repository;

import com.transport.tms.finance.diesel.entity.DieselEntities.VehicleExpense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleExpenseRepository extends JpaRepository<VehicleExpense, String> {
    List<VehicleExpense> findByVehicleRegistrationOrderByDateDesc(String vehicleRegistration);
    List<VehicleExpense> findAllByOrderByDateDesc();
}
