package com.example.demo.payment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "payment_requests",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_payment_requests_idempotency_key", columnNames = "idempotency_key"),
                @UniqueConstraint(name = "uk_payment_requests_request_no", columnNames = "request_no")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "idempotency_key", nullable = false, length = 100)
    private String idempotencyKey;

    @Column(name = "request_no", nullable = false, length = 40)
    private String requestNo;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "requested_by", nullable = false, length = 100)
    private String requestedBy;

    @Column(name = "approved_by", length = 100)
    private String approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "rejected_by", length = 100)
    private String rejectedBy;

    @Column(name = "rejected_at")
    private LocalDateTime rejectedAt;

    @Column(name = "reject_reason", length = 500)
    private String rejectReason;

    @Column(name = "failure_reason", length = 500)
    private String failureReason;

    @Column(name = "retry_count", nullable = false)
    private int retryCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PaymentStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    private PaymentRequest(String idempotencyKey, String requestNo, BigDecimal amount, String requestedBy) {
        this.idempotencyKey = idempotencyKey;
        this.requestNo = requestNo;
        this.amount = amount;
        this.requestedBy = requestedBy;
        this.status = PaymentStatus.REQUESTED;
        this.retryCount = 0;
    }

    public static PaymentRequest create(String idempotencyKey, String requestNo, BigDecimal amount, String requestedBy) {
        return new PaymentRequest(idempotencyKey, requestNo, amount, requestedBy);
    }

    public PaymentStatus approve(String approvedBy) {
        PaymentStatus from = status;
        changeStatus(PaymentStatus.APPROVED);
        this.approvedBy = approvedBy;
        this.approvedAt = LocalDateTime.now();
        return from;
    }

    public PaymentStatus reject(String rejectedBy, String reason) {
        PaymentStatus from = status;
        changeStatus(PaymentStatus.REJECTED);
        this.rejectedBy = rejectedBy;
        this.rejectedAt = LocalDateTime.now();
        this.rejectReason = reason;
        return from;
    }

    public PaymentStatus startProcessing() {
        PaymentStatus from = status;
        changeStatus(PaymentStatus.PROCESSING);
        return from;
    }

    public PaymentStatus succeed() {
        PaymentStatus from = status;
        changeStatus(PaymentStatus.SUCCESS);
        this.failureReason = null;
        return from;
    }

    public PaymentStatus fail(String reason) {
        PaymentStatus from = status;
        changeStatus(PaymentStatus.FAILED);
        this.failureReason = reason;
        return from;
    }

    public PaymentStatus retry() {
        PaymentStatus from = status;
        changeStatus(PaymentStatus.RETRYING);
        this.retryCount++;
        return from;
    }

    public PaymentStatus settle() {
        PaymentStatus from = status;
        changeStatus(PaymentStatus.SETTLED);
        return from;
    }

    private void changeStatus(PaymentStatus to) {
        if (!PaymentStatusTransitions.canTransit(status, to)) {
            throw new IllegalStateException("허용되지 않은 상태 변경입니다. " + status + " -> " + to);
        }
        this.status = to;
    }

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
