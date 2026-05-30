package com.example.demo.audit.dto;

import com.example.demo.audit.entity.AuditLog;

import java.time.LocalDateTime;

public record AuditLogResponse(
        Long id,
        String actorId,
        String actionType,
        String targetType,
        Long targetId,
        String beforeValue,
        String afterValue,
        String ipAddress,
        LocalDateTime createdAt
) {

    public static AuditLogResponse from(AuditLog auditLog) {
        return new AuditLogResponse(
                auditLog.getId(),
                auditLog.getActorId(),
                auditLog.getActionType(),
                auditLog.getTargetType(),
                auditLog.getTargetId(),
                auditLog.getBeforeValue(),
                auditLog.getAfterValue(),
                auditLog.getIpAddress(),
                auditLog.getCreatedAt()
        );
    }
}
