package com.example.demo.audit.service;

import com.example.demo.audit.dto.AuditLogResponse;
import com.example.demo.audit.entity.AuditLog;
import com.example.demo.audit.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Transactional(readOnly = true)
    public List<AuditLogResponse> findAll(String targetType, Long targetId) {
        if (targetType != null && !targetType.isBlank() && targetId != null) {
            return auditLogRepository.findByTargetTypeAndTargetIdOrderByCreatedAtDesc(targetType, targetId).stream()
                    .map(AuditLogResponse::from)
                    .toList();
        }

        return auditLogRepository.findAll().stream()
                .map(AuditLogResponse::from)
                .toList();
    }

    @Transactional
    public void record(String actorId, String actionType, String targetType, Long targetId,
                       String beforeValue, String afterValue, String ipAddress) {
        auditLogRepository.save(AuditLog.of(
                actorId,
                actionType,
                targetType,
                targetId,
                beforeValue,
                afterValue,
                ipAddress
        ));
    }
}
