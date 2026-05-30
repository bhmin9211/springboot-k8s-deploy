package com.example.demo.payment.controller;

import com.example.demo.config.security.SecurityRoles;
import com.example.demo.payment.dto.CreatePaymentRequest;
import com.example.demo.payment.dto.FailPaymentRequest;
import com.example.demo.payment.dto.PaymentRequestResponse;
import com.example.demo.payment.dto.PaymentStatusHistoryResponse;
import com.example.demo.payment.dto.RejectPaymentRequest;
import com.example.demo.payment.service.PaymentRequestService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/payment-requests")
@RequiredArgsConstructor
public class PaymentRequestController {

    private final PaymentRequestService paymentRequestService;

    @PreAuthorize("hasAnyRole('" + SecurityRoles.OPERATOR + "','" + SecurityRoles.ADMIN + "')")
    @PostMapping
    public ResponseEntity<PaymentRequestResponse> create(@RequestBody CreatePaymentRequest request,
                                                         Authentication authentication,
                                                         HttpServletRequest httpRequest) {
        PaymentRequestResponse response = paymentRequestService.create(request, actor(authentication), clientIp(httpRequest));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<PaymentRequestResponse>> findAll() {
        return ResponseEntity.ok(paymentRequestService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentRequestResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentRequestService.findById(id));
    }

    @PreAuthorize("hasAnyRole('" + SecurityRoles.OPERATOR + "','" + SecurityRoles.ADMIN + "')")
    @PostMapping("/{id}/approve")
    public ResponseEntity<PaymentRequestResponse> approve(@PathVariable Long id,
                                                          Authentication authentication,
                                                          HttpServletRequest httpRequest) {
        return ResponseEntity.ok(paymentRequestService.approve(id, actor(authentication), clientIp(httpRequest)));
    }

    @PreAuthorize("hasAnyRole('" + SecurityRoles.OPERATOR + "','" + SecurityRoles.ADMIN + "')")
    @PostMapping("/{id}/reject")
    public ResponseEntity<PaymentRequestResponse> reject(@PathVariable Long id,
                                                         @RequestBody RejectPaymentRequest request,
                                                         Authentication authentication,
                                                         HttpServletRequest httpRequest) {
        return ResponseEntity.ok(paymentRequestService.reject(id, request.reason(), actor(authentication), clientIp(httpRequest)));
    }

    @PreAuthorize("hasAnyRole('" + SecurityRoles.OPERATOR + "','" + SecurityRoles.ADMIN + "')")
    @PostMapping("/{id}/process")
    public ResponseEntity<PaymentRequestResponse> process(@PathVariable Long id,
                                                          Authentication authentication,
                                                          HttpServletRequest httpRequest) {
        return ResponseEntity.ok(paymentRequestService.process(id, actor(authentication), clientIp(httpRequest)));
    }

    @PreAuthorize("hasAnyRole('" + SecurityRoles.OPERATOR + "','" + SecurityRoles.ADMIN + "')")
    @PostMapping("/{id}/success")
    public ResponseEntity<PaymentRequestResponse> succeed(@PathVariable Long id,
                                                          Authentication authentication,
                                                          HttpServletRequest httpRequest) {
        return ResponseEntity.ok(paymentRequestService.succeed(id, actor(authentication), clientIp(httpRequest)));
    }

    @PreAuthorize("hasAnyRole('" + SecurityRoles.OPERATOR + "','" + SecurityRoles.ADMIN + "')")
    @PostMapping("/{id}/fail")
    public ResponseEntity<PaymentRequestResponse> fail(@PathVariable Long id,
                                                       @RequestBody FailPaymentRequest request,
                                                       Authentication authentication,
                                                       HttpServletRequest httpRequest) {
        return ResponseEntity.ok(paymentRequestService.fail(id, request.reason(), actor(authentication), clientIp(httpRequest)));
    }

    @PreAuthorize("hasAnyRole('" + SecurityRoles.OPERATOR + "','" + SecurityRoles.ADMIN + "')")
    @PostMapping("/{id}/retry")
    public ResponseEntity<PaymentRequestResponse> retry(@PathVariable Long id,
                                                        Authentication authentication,
                                                        HttpServletRequest httpRequest) {
        return ResponseEntity.ok(paymentRequestService.retry(id, actor(authentication), clientIp(httpRequest)));
    }

    @GetMapping("/{id}/histories")
    public ResponseEntity<List<PaymentStatusHistoryResponse>> findHistories(@PathVariable Long id) {
        return ResponseEntity.ok(paymentRequestService.findHistories(id));
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
