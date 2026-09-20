package com.transport.tms.finance.wage.repository;

import com.transport.tms.finance.wage.entity.WageEntities.WorkerWage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkerWageRepository extends JpaRepository<WorkerWage, String> {
    List<WorkerWage> findByMonthYear(String monthYear);
    List<WorkerWage> findByWorkerId(String workerId);
}
