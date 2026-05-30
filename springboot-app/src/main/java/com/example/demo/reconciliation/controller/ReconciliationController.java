package com.example.demo.reconciliation.controller;

import com.example.demo.config.security.SecurityRoles;
import com.example.demo.reconciliation.dto.ReconcileSettlementRequest;
import com.example.demo.reconciliation.dto.ReconciliationResultResponse;
import com.example.demo.reconciliation.service.ReconciliationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reconciliations")
@RequiredArgsConstructor
public class ReconciliationController {

    private final ReconciliationService reconciliationService;

    @PreAuthorize("hasAnyRole('" + SecurityRoles.OPERATOR + "','" + SecurityRoles.ADMIN + "')")
    @PostMapping
    public ResponseEntity<ReconciliationResultResponse> reconcile(@RequestBody ReconcileSettlementRequest request,
                                                                  Authentication authentication,
                                                                  HttpServletRequest httpRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reconciliationService.reconcile(request, actor(authentication), clientIp(httpRequest)));
    }

    @GetMapping
    public ResponseEntity<List<ReconciliationResultResponse>> findAll() {
        return ResponseEntity.ok(reconciliationService.findAll());
    }

    @GetMapping("/mismatches")
    public ResponseEntity<List<ReconciliationResultResponse>> findMismatches() {
        return ResponseEntity.ok(reconciliationService.findMismatches());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleConflict(IllegalStateException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", e.getMessage()));
    }

    private String actor(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return "system";
        }
        return authentication.getName();
    }

    private String clientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
