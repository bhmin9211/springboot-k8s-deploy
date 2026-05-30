package com.example.demo.audit.controller;

import com.example.demo.audit.dto.AuditLogResponse;
import com.example.demo.audit.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    public ResponseEntity<List<AuditLogResponse>> findAll(@RequestParam(required = false) String targetType,
                                                          @RequestParam(required = false) Long targetId) {
        return ResponseEntity.ok(auditLogService.findAll(targetType, targetId));
    }
}
