package com.transport.tms.finance.diesel.repository;

import com.transport.tms.finance.diesel.entity.DieselEntities.DieselLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DieselLogRepository extends JpaRepository<DieselLog, String> {
    List<DieselLog> findByVehicleRegistrationOrderByDateDesc(String vehicleRegistration);
    List<DieselLog> findAllByOrderByDateDesc();
}
