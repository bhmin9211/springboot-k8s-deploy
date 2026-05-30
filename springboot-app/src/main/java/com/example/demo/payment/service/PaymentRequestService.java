package com.example.demo.payment.service;

import com.example.demo.audit.service.AuditLogService;
import com.example.demo.payment.dto.CreatePaymentRequest;
import com.example.demo.payment.dto.PaymentRequestResponse;
import com.example.demo.payment.dto.PaymentStatusHistoryResponse;
import com.example.demo.payment.entity.PaymentRequest;
import com.example.demo.payment.entity.PaymentStatus;
import com.example.demo.payment.entity.PaymentStatusHistory;
import com.example.demo.payment.repository.PaymentRequestRepository;
import com.example.demo.payment.repository.PaymentStatusHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentRequestService {

    private static final String TARGET_TYPE = "PAYMENT_REQUEST";
    private static final int MAX_RETRY_COUNT = 3;

    private final PaymentRequestRepository paymentRequestRepository;
    private final PaymentStatusHistoryRepository statusHistoryRepository;
    private final AuditLogService auditLogService;

    @Transactional
    public PaymentRequestResponse create(CreatePaymentRequest request, String actorId, String ipAddress) {
        validateCreateRequest(request);

        return paymentRequestRepository.findByIdempotencyKey(request.idempotencyKey())
                .map(PaymentRequestResponse::from)
                .orElseGet(() -> createNewPaymentRequest(request, actorId, ipAddress));
    }

    @Transactional(readOnly = true)
    public List<PaymentRequestResponse> findAll() {
        return paymentRequestRepository.findAll().stream()
                .map(PaymentRequestResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public PaymentRequestResponse findById(Long id) {
        return PaymentRequestResponse.from(getPaymentRequest(id));
    }

    @Transactional
    public PaymentRequestResponse approve(Long id, String actorId, String ipAddress) {
        PaymentRequest paymentRequest = getPaymentRequest(id);
        PaymentStatus fromStatus = paymentRequest.approve(actorId);

        statusHistoryRepository.save(PaymentStatusHistory.of(
                paymentRequest.getId(),
                fromStatus,
                paymentRequest.getStatus(),
                "운영자 승인",
                actorId
        ));
        auditLogService.record(
                actorId,
                "APPROVE",
                TARGET_TYPE,
                paymentRequest.getId(),
                statusJson(fromStatus),
                statusJson(paymentRequest.getStatus()),
                ipAddress
        );

        return PaymentRequestResponse.from(paymentRequest);
    }

    @Transactional
    public PaymentRequestResponse reject(Long id, String reason, String actorId, String ipAddress) {
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("반려 사유는 필수입니다.");
        }

        PaymentRequest paymentRequest = getPaymentRequest(id);
        PaymentStatus fromStatus = paymentRequest.reject(actorId, reason);

        statusHistoryRepository.save(PaymentStatusHistory.of(
                paymentRequest.getId(),
                fromStatus,
                paymentRequest.getStatus(),
                reason,
                actorId
        ));
        auditLogService.record(
                actorId,
                "REJECT",
                TARGET_TYPE,
                paymentRequest.getId(),
                statusJson(fromStatus),
                "{\"status\":\"" + paymentRequest.getStatus() + "\",\"reason\":\"" + escape(reason) + "\"}",
                ipAddress
        );

        return PaymentRequestResponse.from(paymentRequest);
    }

    @Transactional
    public PaymentRequestResponse process(Long id, String actorId, String ipAddress) {
        PaymentRequest paymentRequest = getPaymentRequest(id);
        PaymentStatus fromStatus = paymentRequest.startProcessing();
        recordStatusChange(paymentRequest, fromStatus, "외부 처리 시작", actorId, ipAddress, "PROCESS");
        return PaymentRequestResponse.from(paymentRequest);
    }

    @Transactional
    public PaymentRequestResponse succeed(Long id, String actorId, String ipAddress) {
        PaymentRequest paymentRequest = getPaymentRequest(id);
        PaymentStatus fromStatus = paymentRequest.succeed();
        recordStatusChange(paymentRequest, fromStatus, "외부 처리 성공", actorId, ipAddress, "SUCCESS");
        return PaymentRequestResponse.from(paymentRequest);
    }

    @Transactional
    public PaymentRequestResponse fail(Long id, String reason, String actorId, String ipAddress) {
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("실패 사유는 필수입니다.");
        }

        PaymentRequest paymentRequest = getPaymentRequest(id);
        PaymentStatus fromStatus = paymentRequest.fail(reason);
        recordStatusChange(paymentRequest, fromStatus, reason, actorId, ipAddress, "FAIL");
        return PaymentRequestResponse.from(paymentRequest);
    }

    @Transactional
    public PaymentRequestResponse retry(Long id, String actorId, String ipAddress) {
        PaymentRequest paymentRequest = getPaymentRequest(id);
        if (paymentRequest.getRetryCount() >= MAX_RETRY_COUNT) {
            throw new IllegalStateException("최대 재처리 횟수를 초과했습니다. max=" + MAX_RETRY_COUNT);
        }

        PaymentStatus fromStatus = paymentRequest.retry();
        recordStatusChange(paymentRequest, fromStatus, "운영자 재처리 요청", actorId, ipAddress, "RETRY");
        return PaymentRequestResponse.from(paymentRequest);
    }

    @Transactional(readOnly = true)
    public List<PaymentStatusHistoryResponse> findHistories(Long id) {
        if (!paymentRequestRepository.existsById(id)) {
            throw new IllegalArgumentException("결제 요청을 찾을 수 없습니다. id=" + id);
        }

        return statusHistoryRepository.findByPaymentRequestIdOrderByCreatedAtAsc(id).stream()
                .map(PaymentStatusHistoryResponse::from)
                .toList();
    }

    private PaymentRequestResponse createNewPaymentRequest(CreatePaymentRequest request, String actorId, String ipAddress) {
        try {
            PaymentRequest paymentRequest = PaymentRequest.create(
                    request.idempotencyKey(),
                    generateRequestNo(),
                    request.amount(),
                    request.requestedBy()
            );
            PaymentRequest saved = paymentRequestRepository.saveAndFlush(paymentRequest);
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
                    statusJson(saved.getStatus()),
                    ipAddress
            );
            return PaymentRequestResponse.from(saved);
        } catch (DataIntegrityViolationException e) {
            return paymentRequestRepository.findByIdempotencyKey(request.idempotencyKey())
                    .map(PaymentRequestResponse::from)
                    .orElseThrow(() -> e);
        }
    }

    private PaymentRequest getPaymentRequest(Long id) {
        return paymentRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("결제 요청을 찾을 수 없습니다. id=" + id));
    }

    @Transactional(readOnly = true)
    public PaymentRequest getPaymentRequestEntity(Long id) {
        return getPaymentRequest(id);
    }

    public void settle(PaymentRequest paymentRequest, String actorId, String ipAddress) {
        PaymentStatus fromStatus = paymentRequest.settle();
        recordStatusChange(paymentRequest, fromStatus, "정산 데이터 생성", actorId, ipAddress, "SETTLE");
    }

    private void recordStatusChange(PaymentRequest paymentRequest, PaymentStatus fromStatus, String reason,
                                    String actorId, String ipAddress, String actionType) {
        statusHistoryRepository.save(PaymentStatusHistory.of(
                paymentRequest.getId(),
                fromStatus,
                paymentRequest.getStatus(),
                reason,
                actorId
        ));
        auditLogService.record(
                actorId,
                actionType,
                TARGET_TYPE,
                paymentRequest.getId(),
                statusJson(fromStatus),
                statusJson(paymentRequest.getStatus()),
                ipAddress
        );
    }

    private void validateCreateRequest(CreatePaymentRequest request) {
        if (request.idempotencyKey() == null || request.idempotencyKey().isBlank()) {
            throw new IllegalArgumentException("idempotencyKey는 필수입니다.");
        }
        if (request.amount() == null || request.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("amount는 0보다 커야 합니다.");
        }
        if (request.requestedBy() == null || request.requestedBy().isBlank()) {
            throw new IllegalArgumentException("requestedBy는 필수입니다.");
        }
    }

    private String generateRequestNo() {
        String date = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String random = UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
        return "PAY-" + date + "-" + random;
    }

    private String statusJson(PaymentStatus status) {
        return "{\"status\":\"" + status + "\"}";
    }

    private String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
