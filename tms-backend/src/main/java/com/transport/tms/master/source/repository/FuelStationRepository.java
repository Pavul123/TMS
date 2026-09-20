package com.transport.tms.master.source.repository;

import com.transport.tms.master.source.entity.FuelStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FuelStationRepository extends JpaRepository<FuelStation, String> {
    List<FuelStation> findByStatus(String status);
}
