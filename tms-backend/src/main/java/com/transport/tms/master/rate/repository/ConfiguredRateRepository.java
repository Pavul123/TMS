package com.transport.tms.master.rate.repository;

import com.transport.tms.master.rate.entity.ConfiguredRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConfiguredRateRepository extends JpaRepository<ConfiguredRate, String> {

    List<ConfiguredRate> findByStatus(String status);

    List<ConfiguredRate> findByCustomerId(String customerId);

    @Query("SELECT r FROM ConfiguredRate r WHERE r.customerId = :customerId " +
           "AND LOWER(r.material) = LOWER(:material) " +
           "AND r.status = 'ACTIVE' ORDER BY r.effectiveFrom DESC")
    List<ConfiguredRate> findMatchingRates(
            @Param("customerId") String customerId,
            @Param("material") String material
    );
}
