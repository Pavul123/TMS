package com.transport.tms.finance.wage.repository;

import com.transport.tms.finance.wage.entity.WageEntities.WageAdvance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WageAdvanceRepository extends JpaRepository<WageAdvance, String> {
    List<WageAdvance> findByRecipientTypeAndRecipientId(String recipientType, String recipientId);
    List<WageAdvance> findAllByOrderByDateDesc();
}
