package com.example.demo.payment.service;

import com.example.demo.audit.service.AuditLogService;
import com.example.demo.payment.dto.PaymentRequestResponse;
import com.example.demo.payment.entity.PaymentRequest;
import com.example.demo.payment.entity.PaymentStatusHistory;
import com.example.demo.payment.repository.PaymentRequestRepository;
import com.example.demo.payment.repository.PaymentStatusHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class PaymentRequestInsertTx {

    private static final String TARGET_TYPE = "PAYMENT_REQUEST";

    private final PaymentRequestRepository paymentRequestRepository;
    private final PaymentStatusHistoryRepository statusHistoryRepository;
    private final AuditLogService auditLogService;

    /**
     * 별도 빈의 REQUIRES_NEW 트랜잭션이다.
     * 같은 클래스 내부 호출은 프록시를 타지 않아 트랜잭션이 분리되지 않는다.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public PaymentRequestResponse insert(String idempotencyKey, String requestNo, BigDecimal amount, String requestedBy,
                                         String actorId, String ipAddress) {
        PaymentRequest saved = paymentRequestRepository.saveAndFlush(PaymentRequest.create(
                idempotencyKey,
                requestNo,
                amount,
                requestedBy
        ));
        statusHistoryRepository.save(PaymentStatusHistory.of(
                saved.getId(),
                null,
                saved.getStatus(),
                "요청 등록",
                actorId
        ));
        auditLogService.record(
                actorId,
                "CREATE",
                TARGET_TYPE,
                saved.getId(),
                null,
                "{\"status\":\"" + saved.getStatus() + "\"}",
                ipAddress
        );
        return PaymentRequestResponse.from(saved);
    }
}
