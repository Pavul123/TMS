package com.transport.tms.master.driver.repository;

import com.transport.tms.master.driver.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DriverRepository extends JpaRepository<Driver, String> {
    List<Driver> findByStatus(String status);
}
