package com.transport.tms.governance.correction.repository;

import com.transport.tms.governance.correction.entity.CorrectionRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CorrectionRequestRepository extends JpaRepository<CorrectionRequest, String> {
    List<CorrectionRequest> findByStatusOrderByRequestedAtDesc(String status);
    List<CorrectionRequest> findByEntityTypeAndEntityId(String entityType, String entityId);
}
