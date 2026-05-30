package com.example.demo.payment.dto;

import com.example.demo.payment.entity.PaymentRequest;
import com.example.demo.payment.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentRequestResponse(
        Long id,
        String idempotencyKey,
        String requestNo,
        BigDecimal amount,
        PaymentStatus status,
        String requestedBy,
        String approvedBy,
        LocalDateTime approvedAt,
        String rejectedBy,
        LocalDateTime rejectedAt,
        String rejectReason,
        String failureReason,
        int retryCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static PaymentRequestResponse from(PaymentRequest paymentRequest) {
        return new PaymentRequestResponse(
                paymentRequest.getId(),
                paymentRequest.getIdempotencyKey(),
                paymentRequest.getRequestNo(),
                paymentRequest.getAmount(),
                paymentRequest.getStatus(),
                paymentRequest.getRequestedBy(),
                paymentRequest.getApprovedBy(),
                paymentRequest.getApprovedAt(),
                paymentRequest.getRejectedBy(),
                paymentRequest.getRejectedAt(),
                paymentRequest.getRejectReason(),
                paymentRequest.getFailureReason(),
                paymentRequest.getRetryCount(),
                paymentRequest.getCreatedAt(),
                paymentRequest.getUpdatedAt()
        );
    }
}
