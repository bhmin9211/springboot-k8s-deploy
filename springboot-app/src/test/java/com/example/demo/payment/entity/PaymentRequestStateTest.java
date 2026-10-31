package com.example.demo.payment.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaymentRequestStateTest {

    @Test
    void allowsDocumentedTransitionsOnly() {
        assertThat(PaymentStatusTransitions.canTransit(PaymentStatus.REQUESTED, PaymentStatus.APPROVED)).isTrue();
        assertThat(PaymentStatusTransitions.canTransit(PaymentStatus.REQUESTED, PaymentStatus.REJECTED)).isTrue();
        assertThat(PaymentStatusTransitions.canTransit(PaymentStatus.REQUESTED, PaymentStatus.SUCCESS)).isFalse();
        assertThat(PaymentStatusTransitions.canTransit(PaymentStatus.APPROVED, PaymentStatus.PROCESSING)).isTrue();
        assertThat(PaymentStatusTransitions.canTransit(PaymentStatus.PROCESSING, PaymentStatus.SUCCESS)).isTrue();
        assertThat(PaymentStatusTransitions.canTransit(PaymentStatus.PROCESSING, PaymentStatus.FAILED)).isTrue();
        assertThat(PaymentStatusTransitions.canTransit(PaymentStatus.FAILED, PaymentStatus.RETRYING)).isTrue();
        assertThat(PaymentStatusTransitions.canTransit(PaymentStatus.RETRYING, PaymentStatus.SUCCESS)).isTrue();
        assertThat(PaymentStatusTransitions.canTransit(PaymentStatus.RETRYING, PaymentStatus.FAILED)).isTrue();
        assertThat(PaymentStatusTransitions.canTransit(PaymentStatus.SUCCESS, PaymentStatus.SETTLED)).isTrue();
        assertThat(PaymentStatusTransitions.canTransit(PaymentStatus.REJECTED, PaymentStatus.APPROVED)).isFalse();
        assertThat(PaymentStatusTransitions.canTransit(PaymentStatus.SETTLED, PaymentStatus.PROCESSING)).isFalse();
    }

    @Test
    void approvesRequestedPayment() {
        PaymentRequest paymentRequest = newRequest();

        PaymentStatus from = paymentRequest.approve("operator");

        assertThat(from).isEqualTo(PaymentStatus.REQUESTED);
        assertThat(paymentRequest.getStatus()).isEqualTo(PaymentStatus.APPROVED);
        assertThat(paymentRequest.getApprovedBy()).isEqualTo("operator");
        assertThat(paymentRequest.getApprovedAt()).isNotNull();
    }

    @Test
    void rejectsRequestedPayment() {
        PaymentRequest paymentRequest = newRequest();

        PaymentStatus from = paymentRequest.reject("operator", "금액 불일치");

        assertThat(from).isEqualTo(PaymentStatus.REQUESTED);
        assertThat(paymentRequest.getStatus()).isEqualTo(PaymentStatus.REJECTED);
        assertThat(paymentRequest.getRejectReason()).isEqualTo("금액 불일치");
    }

    @Test
    void blocksSuccessBeforeProcessingAndKeepsStatus() {
        PaymentRequest paymentRequest = newRequest();

        assertThatThrownBy(paymentRequest::succeed)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("허용되지 않은 상태 변경");
        assertThat(paymentRequest.getStatus()).isEqualTo(PaymentStatus.REQUESTED);
    }

    @Test
    void retriesFailedPaymentAndClearsFailureOnSuccess() {
        PaymentRequest paymentRequest = newRequest();
        paymentRequest.approve("operator");
        paymentRequest.startProcessing();
        paymentRequest.fail("timeout");

        paymentRequest.retry();

        assertThat(paymentRequest.getStatus()).isEqualTo(PaymentStatus.RETRYING);
        assertThat(paymentRequest.getRetryCount()).isEqualTo(1);

        paymentRequest.succeed();

        assertThat(paymentRequest.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
        assertThat(paymentRequest.getFailureReason()).isNull();
    }

    @Test
    void keepsRejectedAndSettledTerminal() {
        PaymentRequest rejected = newRequest();
        rejected.reject("operator", "증빙 없음");
        assertThatThrownBy(() -> rejected.approve("operator"))
                .isInstanceOf(IllegalStateException.class);
        assertThat(rejected.getStatus()).isEqualTo(PaymentStatus.REJECTED);

        PaymentRequest settled = newRequest();
        settled.approve("operator");
        settled.startProcessing();
        settled.succeed();
        settled.settle();
        assertThatThrownBy(settled::startProcessing)
                .isInstanceOf(IllegalStateException.class);
        assertThat(settled.getStatus()).isEqualTo(PaymentStatus.SETTLED);
    }

    private PaymentRequest newRequest() {
        return PaymentRequest.create("key", "PAY-1", new BigDecimal("1000.00"), "alice");
    }
}
