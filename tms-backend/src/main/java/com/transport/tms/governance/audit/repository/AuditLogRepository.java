package com.transport.tms.governance.audit.repository;

import com.transport.tms.governance.audit.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findTop100ByOrderByPerformedAtDesc();
    List<AuditLog> findByEntityTypeAndEntityIdOrderByPerformedAtDesc(String entityType, String entityId);
}
