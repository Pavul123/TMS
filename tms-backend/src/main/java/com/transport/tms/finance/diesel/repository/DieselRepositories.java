package com.transport.tms.finance.diesel.repository;

import com.transport.tms.finance.diesel.entity.DieselEntities.DieselLog;
import com.transport.tms.finance.diesel.entity.DieselEntities.VehicleExpense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public class DieselRepositories {

    @Repository
    public interface DieselLogRepository extends JpaRepository<DieselLog, String> {
        List<DieselLog> findByVehicleRegistrationOrderByDateDesc(String vehicleRegistration);
        List<DieselLog> findAllByOrderByDateDesc();
    }

    @Repository
    public interface VehicleExpenseRepository extends JpaRepository<VehicleExpense, String> {
        List<VehicleExpense> findByVehicleRegistrationOrderByDateDesc(String vehicleRegistration);
        List<VehicleExpense> findAllByOrderByDateDesc();
    }
}
