package com.example.demo.payment.service;

import com.example.demo.audit.service.AuditLogService;
import com.example.demo.payment.dto.CreatePaymentRequest;
import com.example.demo.payment.dto.PaymentRequestCreateResult;
import com.example.demo.payment.dto.PaymentRequestResponse;
import com.example.demo.payment.entity.PaymentRequest;
import com.example.demo.payment.entity.PaymentStatus;
import com.example.demo.payment.repository.PaymentRequestRepository;
import com.example.demo.payment.repository.PaymentStatusHistoryRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentRequestServiceTest {

    @Mock
    private PaymentRequestRepository paymentRequestRepository;

    @Mock
    private PaymentStatusHistoryRepository statusHistoryRepository;

    @Mock
    private AuditLogService auditLogService;

    @Mock
    private PaymentRequestInsertTx paymentRequestInsertTx;

    @InjectMocks
    private PaymentRequestService paymentRequestService;

    @Test
    void returnsExistingRequestWhenPayloadMatches() {
        PaymentRequest existing = PaymentRequest.create("key", "PAY-1", new BigDecimal("10.00"), "alice");
        when(paymentRequestRepository.findByIdempotencyKey("key")).thenReturn(Optional.of(existing));

        PaymentRequestCreateResult result = paymentRequestService.create(
                new CreatePaymentRequest("key", new BigDecimal("10.0"), "alice"),
                "actor",
                "127.0.0.1"
        );

        assertThat(result.created()).isFalse();
        assertThat(result.paymentRequest().requestNo()).isEqualTo("PAY-1");
        assertThat(result.paymentRequest().status()).isEqualTo(PaymentStatus.REQUESTED);
        verify(paymentRequestInsertTx, never()).insert(any(), any(), any(), any(), any(), any());
        verifyNoInteractions(auditLogService);
    }

    @Test
    void rejectsDifferentPayloadBeforeInsert() {
        PaymentRequest existing = PaymentRequest.create("key", "PAY-1", new BigDecimal("10.00"), "alice");
        when(paymentRequestRepository.findByIdempotencyKey("key")).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> paymentRequestService.create(
                new CreatePaymentRequest("key", new BigDecimal("11.00"), "alice"),
                "actor",
                "127.0.0.1"
        ))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("다른 요청 내용");

        verify(paymentRequestInsertTx, never()).insert(any(), any(), any(), any(), any(), any());
    }

    @Test
    void insertsWhenIdempotencyKeyIsNew() {
        CreatePaymentRequest request = new CreatePaymentRequest("key", new BigDecimal("10.00"), "alice");
        when(paymentRequestRepository.findByIdempotencyKey("key")).thenReturn(Optional.empty());
        when(paymentRequestInsertTx.insert(eq("key"), anyString(), eq(request.amount()), eq("alice"), eq("actor"), eq("127.0.0.1")))
                .thenReturn(savedResponse());

        PaymentRequestCreateResult result = paymentRequestService.create(request, "actor", "127.0.0.1");

        assertThat(result.created()).isTrue();
        assertThat(result.paymentRequest().id()).isEqualTo(10L);
    }

    @Test
    void replaysCommittedRowAfterIdempotencyConstraintViolation() {
        CreatePaymentRequest request = new CreatePaymentRequest("key", new BigDecimal("10.00"), "alice");
        PaymentRequest winner = PaymentRequest.create("key", "PAY-WIN", new BigDecimal("10.00"), "alice");
        when(paymentRequestRepository.findByIdempotencyKey("key"))
                .thenReturn(Optional.empty(), Optional.of(winner));
        when(paymentRequestInsertTx.insert(anyString(), anyString(), any(), anyString(), anyString(), anyString()))
                .thenThrow(violation("UK_PAYMENT_REQUESTS_IDEMPOTENCY_KEY"));

        PaymentRequestCreateResult result = paymentRequestService.create(request, "actor", "127.0.0.1");

        assertThat(result.created()).isFalse();
        assertThat(result.paymentRequest().requestNo()).isEqualTo("PAY-WIN");
        verifyNoInteractions(auditLogService);
    }

    @Test
    void conflictsWhenConcurrentWinnerHasDifferentPayload() {
        when(paymentRequestRepository.findByIdempotencyKey("key"))
                .thenReturn(Optional.empty(), Optional.of(PaymentRequest.create("key", "PAY-WIN", new BigDecimal("10.00"), "bob")));
        when(paymentRequestInsertTx.insert(anyString(), anyString(), any(), anyString(), anyString(), anyString()))
                .thenThrow(violation("UK_PAYMENT_REQUESTS_IDEMPOTENCY_KEY"));

        assertThatThrownBy(() -> paymentRequestService.create(
                new CreatePaymentRequest("key", new BigDecimal("10.00"), "alice"),
                "actor",
                "127.0.0.1"
        ))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("다른 요청 내용");
    }

    @Test
    void retriesRequestNumberCollisionThenInserts() {
        CreatePaymentRequest request = new CreatePaymentRequest("key", new BigDecimal("10.00"), "alice");
        when(paymentRequestRepository.findByIdempotencyKey("key")).thenReturn(Optional.empty());
        when(paymentRequestInsertTx.insert(anyString(), anyString(), any(), anyString(), anyString(), anyString()))
                .thenThrow(violation("UK_PAYMENT_REQUESTS_REQUEST_NO"))
                .thenReturn(savedResponse());

        PaymentRequestCreateResult result = paymentRequestService.create(request, "actor", "127.0.0.1");

        assertThat(result.created()).isTrue();
        verify(paymentRequestInsertTx, times(2)).insert(anyString(), anyString(), any(), anyString(), anyString(), anyString());
    }

    @Test
    void stopsAfterRequestNumberRetriesAreExhausted() {
        when(paymentRequestRepository.findByIdempotencyKey("key")).thenReturn(Optional.empty());
        when(paymentRequestInsertTx.insert(anyString(), anyString(), any(), anyString(), anyString(), anyString()))
                .thenThrow(violation("UK_PAYMENT_REQUESTS_REQUEST_NO"));

        assertThatThrownBy(() -> paymentRequestService.create(
                new CreatePaymentRequest("key", new BigDecimal("10.00"), "alice"),
                "actor",
                "127.0.0.1"
        )).isInstanceOf(DataIntegrityViolationException.class);

        verify(paymentRequestInsertTx, times(3)).insert(anyString(), anyString(), any(), anyString(), anyString(), anyString());
    }

    @Test
    void rejectsBlankIdempotencyKey() {
        assertThatThrownBy(() -> paymentRequestService.create(
                new CreatePaymentRequest(" ", new BigDecimal("10.00"), "alice"),
                "actor",
                "127.0.0.1"
        )).isInstanceOf(IllegalArgumentException.class);

        verifyNoInteractions(paymentRequestInsertTx);
    }

    private PaymentRequestResponse savedResponse() {
        return new PaymentRequestResponse(
                10L,
                "key",
                "PAY-1",
                new BigDecimal("10.00"),
                PaymentStatus.REQUESTED,
                "alice",
                null,
                null,
                null,
                null,
                null,
                null,
                0,
                null,
                null
        );
    }

    private DataIntegrityViolationException violation(String constraintName) {
        return new DataIntegrityViolationException(
                "constraint",
                new ConstraintViolationException("duplicate", new SQLException("duplicate"), constraintName)
        );
    }
}
