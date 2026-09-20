package com.transport.tms.governance.audit.service;

import com.transport.tms.governance.audit.entity.AuditLog;
import com.transport.tms.governance.audit.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    @Transactional
    public void recordAudit(
            String entityType,
            String entityId,
            String action,
            String fieldName,
            String oldValue,
            String newValue,
            String reason,
            String performedBy,
            String approvalId
    ) {
        AuditLog log = AuditLog.builder()
                .entityType(entityType)
                .entityId(entityId)
                .action(action)
                .fieldName(fieldName)
                .oldValue(oldValue)
                .newValue(newValue)
                .reason(reason)
                .performedBy(performedBy)
                .approvalId(approvalId)
                .build();
        auditLogRepository.save(log);
    }

    @Transactional(readOnly = true)
    public List<AuditLog> getRecentAuditLogs() {
        return auditLogRepository.findTop100ByOrderByPerformedAtDesc();
    }

    @Transactional(readOnly = true)
    public List<AuditLog> getAuditLogsForEntity(String entityType, String entityId) {
        return auditLogRepository.findByEntityTypeAndEntityIdOrderByPerformedAtDesc(entityType, entityId);
    }
}
