package com.example.demo.payment.dto;

import com.example.demo.payment.entity.PaymentStatus;
import com.example.demo.payment.entity.PaymentStatusHistory;

import java.time.LocalDateTime;

public record PaymentStatusHistoryResponse(
        Long id,
        Long paymentRequestId,
        PaymentStatus fromStatus,
        PaymentStatus toStatus,
        String reason,
        String changedBy,
        LocalDateTime createdAt
) {

    public static PaymentStatusHistoryResponse from(PaymentStatusHistory history) {
        return new PaymentStatusHistoryResponse(
                history.getId(),
                history.getPaymentRequestId(),
                history.getFromStatus(),
                history.getToStatus(),
                history.getReason(),
                history.getChangedBy(),
                history.getCreatedAt()
        );
    }
}
