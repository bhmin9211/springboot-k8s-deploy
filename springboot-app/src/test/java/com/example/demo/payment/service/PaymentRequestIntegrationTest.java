package com.example.demo.payment.service;

import com.example.demo.audit.entity.AuditLog;
import com.example.demo.support.MariaDbIntegrationSupport;
import com.example.demo.audit.repository.AuditLogRepository;
import com.example.demo.payment.dto.CreatePaymentRequest;
import com.example.demo.payment.dto.PaymentRequestCreateResult;
import com.example.demo.payment.dto.PaymentRequestResponse;
import com.example.demo.payment.entity.PaymentRequest;
import com.example.demo.payment.entity.PaymentStatus;
import com.example.demo.payment.entity.PaymentStatusHistory;
import com.example.demo.payment.repository.PaymentRequestRepository;
import com.example.demo.payment.repository.PaymentStatusHistoryRepository;
import com.example.demo.reconciliation.dto.ReconcileSettlementRequest;
import com.example.demo.reconciliation.entity.ReconciliationResultType;
import com.example.demo.reconciliation.repository.ReconciliationResultRepository;
import com.example.demo.reconciliation.service.ReconciliationService;
import com.example.demo.settlement.dto.GenerateSettlementRequest;
import com.example.demo.settlement.dto.SettlementResponse;
import com.example.demo.settlement.entity.SettlementStatus;
import com.example.demo.settlement.repository.SettlementRepository;
import com.example.demo.settlement.service.SettlementService;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PaymentRequestIntegrationTest extends MariaDbIntegrationSupport {

    private static final String ACTOR = "operator-1";
    private static final String IP = "10.0.0.8";

    @Autowired
    private PaymentRequestService paymentRequestService;

    @Autowired
    private SettlementService settlementService;

    @Autowired
    private ReconciliationService reconciliationService;

    @Autowired
    private PaymentRequestRepository paymentRequestRepository;

    @Autowired
    private PaymentStatusHistoryRepository statusHistoryRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private SettlementRepository settlementRepository;

    @Autowired
    private ReconciliationResultRepository reconciliationResultRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @AfterEach
    void cleanup() {
        reconciliationResultRepository.deleteAll();
        settlementRepository.deleteAll();
        statusHistoryRepository.deleteAll();
        auditLogRepository.deleteAll();
        paymentRequestRepository.deleteAll();
    }

    @Test
    void createsPaymentRequestWithHistoryAndAudit() {
        PaymentRequestResponse created = create(key(), "120000.00", "external-order-api");

        assertThat(created.status()).isEqualTo(PaymentStatus.REQUESTED);
        assertThat(created.requestNo()).startsWith("PAY-");
        assertThat(paymentRequestRepository.count()).isEqualTo(1);

        List<PaymentStatusHistory> histories = statusHistoryRepository.findByPaymentRequestIdOrderByCreatedAtAsc(created.id());
        assertThat(histories).singleElement().satisfies(history -> {
            assertThat(history.getFromStatus()).isNull();
            assertThat(history.getToStatus()).isEqualTo(PaymentStatus.REQUESTED);
            assertThat(history.getChangedBy()).isEqualTo(ACTOR);
        });

        assertThat(auditLogRepository.findAll()).singleElement().satisfies(log -> {
            assertThat(log.getActorId()).isEqualTo(ACTOR);
            assertThat(log.getActionType()).isEqualTo("CREATE");
            assertThat(log.getTargetType()).isEqualTo("PAYMENT_REQUEST");
            assertThat(log.getTargetId()).isEqualTo(created.id());
            assertThat(log.getIpAddress()).isEqualTo(IP);
            assertThat(log.getBeforeValue()).isNull();
            assertThat(log.getAfterValue()).contains("REQUESTED");
        });
    }

    @Test
    void returnsSameRequestForSameIdempotencyKeyAndPayload() {
        String idempotencyKey = key();
        PaymentRequestResponse created = create(idempotencyKey, "1200.00", "alice");
        paymentRequestService.approve(created.id(), ACTOR, IP);

        PaymentRequestCreateResult replay = paymentRequestService.create(
                new CreatePaymentRequest(idempotencyKey, new BigDecimal("1200.0"), "alice"),
                ACTOR,
                IP
        );

        assertThat(replay.created()).isFalse();
        assertThat(replay.paymentRequest().id()).isEqualTo(created.id());
        assertThat(replay.paymentRequest().requestNo()).isEqualTo(created.requestNo());
        assertThat(replay.paymentRequest().status()).isEqualTo(PaymentStatus.APPROVED);
        assertThat(paymentRequestRepository.count()).isEqualTo(1);
        assertThat(statusHistoryRepository.count()).isEqualTo(2);
        assertThat(auditLogRepository.count()).isEqualTo(2);
    }

    @Test
    void conflictsWhenSameIdempotencyKeyHasDifferentPayload() {
        String idempotencyKey = key();
        create(idempotencyKey, "1200.00", "alice");

        assertThatThrownBy(() -> paymentRequestService.create(
                new CreatePaymentRequest(idempotencyKey, new BigDecimal("1200.01"), "alice"),
                ACTOR,
                IP
        ))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("다른 요청 내용");

        assertThatThrownBy(() -> paymentRequestService.create(
                new CreatePaymentRequest(idempotencyKey, new BigDecimal("1200.00"), "bob"),
                ACTOR,
                IP
        ))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("다른 요청 내용");

        assertThat(paymentRequestRepository.count()).isEqualTo(1);
        assertThat(auditLogRepository.count()).isEqualTo(1);
    }

    @Test
    void concurrentSamePayloadCreatesOneRow() throws Exception {
        CreatePaymentRequest request = new CreatePaymentRequest(key(), new BigDecimal("5000.00"), "alice");
        int threads = 8;
        List<PaymentRequestCreateResult> results = runTogether(threads, () -> paymentRequestService.create(request, ACTOR, IP));

        Set<Long> ids = new HashSet<>();
        long createdCount = 0;
        for (PaymentRequestCreateResult result : results) {
            ids.add(result.paymentRequest().id());
            if (result.created()) {
                createdCount++;
            }
        }

        assertThat(createdCount).isEqualTo(1);
        assertThat(ids).hasSize(1);
        assertThat(paymentRequestRepository.count()).isEqualTo(1);
        assertThat(statusHistoryRepository.count()).isEqualTo(1);
        assertThat(auditLogRepository.count()).isEqualTo(1);
    }

    @Test
    void concurrentDifferentPayloadKeepsSingleRow() throws Exception {
        String idempotencyKey = key();
        CreatePaymentRequest first = new CreatePaymentRequest(idempotencyKey, new BigDecimal("100.00"), "alice");
        CreatePaymentRequest second = new CreatePaymentRequest(idempotencyKey, new BigDecimal("200.00"), "alice");
        int threads = 8;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        try {
            List<Future<PaymentRequestCreateResult>> futures = submitTogether(executor, threads, index ->
                    paymentRequestService.create(index % 2 == 0 ? first : second, ACTOR, IP));

            int success = 0;
            int createdCount = 0;
            int conflicts = 0;
            Set<Long> ids = new HashSet<>();
            for (Future<PaymentRequestCreateResult> future : futures) {
                try {
                    PaymentRequestCreateResult result = future.get(10, TimeUnit.SECONDS);
                    success++;
                    if (result.created()) {
                        createdCount++;
                    }
                    ids.add(result.paymentRequest().id());
                } catch (ExecutionException exception) {
                    assertThat(exception.getCause()).isInstanceOf(IllegalStateException.class);
                    conflicts++;
                }
            }

            assertThat(createdCount).isEqualTo(1);
            assertThat(success).isEqualTo(threads / 2);
            assertThat(conflicts).isEqualTo(threads / 2);
            assertThat(ids).hasSize(1);
            assertThat(paymentRequestRepository.count()).isEqualTo(1);
            assertThat(statusHistoryRepository.count()).isEqualTo(1);
        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    void concurrentApprovalCommitsOnlyOneVersion() throws Exception {
        PaymentRequestResponse created = create(key(), "1000.00", "alice");
        TransactionTemplate template = new TransactionTemplate(transactionManager);
        java.util.concurrent.CountDownLatch loaded = new java.util.concurrent.CountDownLatch(2);
        java.util.concurrent.CountDownLatch release = new java.util.concurrent.CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
            List<Future<Void>> futures = new ArrayList<>();
            for (int index = 0; index < 2; index++) {
                futures.add(executor.submit(() -> template.execute(status -> {
                    PaymentRequest paymentRequest = paymentRequestRepository.findById(created.id()).orElseThrow();
                    paymentRequest.approve(ACTOR);
                    loaded.countDown();
                    try {
                        if (!release.await(5, TimeUnit.SECONDS)) {
                            throw new IllegalStateException("승인 커밋 대기가 초과되었습니다.");
                        }
                    } catch (InterruptedException exception) {
                        Thread.currentThread().interrupt();
                        throw new IllegalStateException(exception);
                    }
                    return null;
                })));
            }
            assertThat(loaded.await(5, TimeUnit.SECONDS)).isTrue();
            release.countDown();

            int committed = 0;
            int conflicts = 0;
            for (Future<Void> future : futures) {
                try {
                    future.get(10, TimeUnit.SECONDS);
                    committed++;
                } catch (ExecutionException exception) {
                    assertThat(exception.getCause()).isInstanceOf(ObjectOptimisticLockingFailureException.class);
                    conflicts++;
                }
            }

            assertThat(committed).isEqualTo(1);
            assertThat(conflicts).isEqualTo(1);
            assertThat(paymentRequestService.findById(created.id()).status()).isEqualTo(PaymentStatus.APPROVED);
            assertThat(statusHistoryRepository.findByPaymentRequestIdOrderByCreatedAtAsc(created.id())).hasSize(1);
        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    void approvesAndRejectsAlongAllowedTransitions() {
        PaymentRequestResponse approved = create(key(), "1000.00", "alice");
        paymentRequestService.approve(approved.id(), ACTOR, IP);

        PaymentRequestResponse approvedState = paymentRequestService.findById(approved.id());
        assertThat(approvedState.status()).isEqualTo(PaymentStatus.APPROVED);
        assertThat(approvedState.approvedBy()).isEqualTo(ACTOR);
        assertThat(auditActions(approved.id())).containsExactlyInAnyOrder("APPROVE", "CREATE");

        PaymentRequestResponse rejected = create(key(), "1000.00", "alice");
        paymentRequestService.reject(rejected.id(), "증빙 금액이 다릅니다.", ACTOR, IP);

        assertThat(paymentRequestService.findById(rejected.id()).status()).isEqualTo(PaymentStatus.REJECTED);
        assertThat(auditLogRepository.findByTargetTypeAndTargetIdOrderByCreatedAtDesc("PAYMENT_REQUEST", rejected.id()))
                .anySatisfy(log -> {
                    assertThat(log.getActionType()).isEqualTo("REJECT");
                    assertThat(log.getAfterValue()).isEqualTo("{\"status\":\"REJECTED\",\"reason\":\"증빙 금액이 다릅니다.\"}");
                });
    }

    @Test
    void blocksIllegalTransitionAndKeepsCurrentState() {
        PaymentRequestResponse created = create(key(), "1000.00", "alice");
        paymentRequestService.approve(created.id(), ACTOR, IP);

        assertThatThrownBy(() -> paymentRequestService.approve(created.id(), ACTOR, IP))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("허용되지 않은 상태 변경");
        assertThatThrownBy(() -> paymentRequestService.succeed(created.id(), ACTOR, IP))
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> paymentRequestService.reject(created.id(), " ", ACTOR, IP))
                .isInstanceOf(IllegalArgumentException.class);

        assertThat(paymentRequestService.findById(created.id()).status()).isEqualTo(PaymentStatus.APPROVED);
        assertThat(statusHistoryRepository.findByPaymentRequestIdOrderByCreatedAtAsc(created.id())).hasSize(2);
    }

    @Test
    void blocksRetryAfterThreeFailures() {
        PaymentRequestResponse created = create(key(), "1000.00", "alice");
        paymentRequestService.approve(created.id(), ACTOR, IP);
        paymentRequestService.process(created.id(), ACTOR, IP);
        paymentRequestService.fail(created.id(), "timeout", ACTOR, IP);

        paymentRequestService.retry(created.id(), ACTOR, IP);
        paymentRequestService.fail(created.id(), "timeout", ACTOR, IP);
        paymentRequestService.retry(created.id(), ACTOR, IP);
        paymentRequestService.fail(created.id(), "timeout", ACTOR, IP);
        paymentRequestService.retry(created.id(), ACTOR, IP);
        paymentRequestService.fail(created.id(), "timeout", ACTOR, IP);

        assertThatThrownBy(() -> paymentRequestService.retry(created.id(), ACTOR, IP))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("최대 재처리");

        PaymentRequestResponse current = paymentRequestService.findById(created.id());
        assertThat(current.status()).isEqualTo(PaymentStatus.FAILED);
        assertThat(current.retryCount()).isEqualTo(3);
    }

    @Test
    void settlesSuccessfulPaymentAndWritesAudit() {
        PaymentRequestResponse succeeded = moveToSuccess(key());

        SettlementResponse settlement = settlementService.generate(
                new GenerateSettlementRequest(succeeded.id(), null),
                ACTOR,
                IP
        );

        assertThat(settlement.grossAmount()).isEqualByComparingTo("1000.00");
        assertThat(settlement.feeAmount()).isEqualByComparingTo("25.00");
        assertThat(settlement.settlementAmount()).isEqualByComparingTo("975.00");
        assertThat(settlement.settlementStatus()).isEqualTo(SettlementStatus.CREATED);
        assertThat(paymentRequestService.findById(succeeded.id()).status()).isEqualTo(PaymentStatus.SETTLED);
        assertThat(auditLogRepository.findByTargetTypeAndTargetIdOrderByCreatedAtDesc("SETTLEMENT", settlement.id()))
                .singleElement()
                .extracting(AuditLog::getActionType)
                .isEqualTo("CREATE");
        assertThat(auditActions(succeeded.id())).contains("SETTLE");

        assertThatThrownBy(() -> settlementService.generate(
                new GenerateSettlementRequest(create(key(), "1000.00", "alice").id(), null),
                ACTOR,
                IP
        )).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void reconcilesMatchedAndMismatchedAmounts() {
        PaymentRequestResponse succeeded = moveToSuccess(key());
        SettlementResponse settlement = settlementService.generate(
                new GenerateSettlementRequest(succeeded.id(), null),
                ACTOR,
                IP
        );

        var matched = reconciliationService.reconcile(
                new ReconcileSettlementRequest(settlement.id(), "EXT-1", new BigDecimal("975.00"), "CREATED"),
                ACTOR,
                IP
        );
        var mismatched = reconciliationService.reconcile(
                new ReconcileSettlementRequest(settlement.id(), "EXT-2", new BigDecimal("970.00"), "CREATED"),
                ACTOR,
                IP
        );

        assertThat(matched.resultType()).isEqualTo(ReconciliationResultType.MATCHED);
        assertThat(mismatched.resultType()).isEqualTo(ReconciliationResultType.AMOUNT_MISMATCH);
        assertThat(reconciliationService.findMismatches()).extracting(result -> result.externalTransactionId())
                .containsExactly("EXT-2");
    }

    @Test
    void httpCreateReplayAndConflictUseDistinctStatusCodes() throws Exception {
        String idempotencyKey = key();
        String body = """
                {"idempotencyKey":"%s","amount":1500.50,"requestedBy":"external-order-api"}
                """.formatted(idempotencyKey);

        MvcResult created = mockMvc.perform(post("/payment-requests")
                        .with(csrf()).with(user("operator").roles("OPERATOR"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("REQUESTED"))
                .andReturn();
        String requestNo = JsonPath.read(created.getResponse().getContentAsString(), "$.requestNo");

        mockMvc.perform(post("/payment-requests")
                        .with(csrf()).with(user("operator").roles("OPERATOR"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestNo").value(requestNo));

        mockMvc.perform(post("/payment-requests")
                        .with(csrf()).with(user("operator").roles("OPERATOR"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"idempotencyKey":"%s","amount":1500.51,"requestedBy":"external-order-api"}
                                """.formatted(idempotencyKey)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("다른 요청 내용")));

        assertThat(paymentRequestRepository.count()).isEqualTo(1);
    }

    private PaymentRequestResponse create(String idempotencyKey, String amount, String requestedBy) {
        return paymentRequestService.create(
                new CreatePaymentRequest(idempotencyKey, new BigDecimal(amount), requestedBy),
                ACTOR,
                IP
        ).paymentRequest();
    }

    private PaymentRequestResponse moveToSuccess(String idempotencyKey) {
        PaymentRequestResponse created = create(idempotencyKey, "1000.00", "alice");
        paymentRequestService.approve(created.id(), ACTOR, IP);
        paymentRequestService.process(created.id(), ACTOR, IP);
        return paymentRequestService.succeed(created.id(), ACTOR, IP);
    }

    private List<String> auditActions(Long paymentRequestId) {
        return auditLogRepository.findByTargetTypeAndTargetIdOrderByCreatedAtDesc("PAYMENT_REQUEST", paymentRequestId)
                .stream()
                .map(AuditLog::getActionType)
                .toList();
    }

    private String key() {
        return "PAY-ORDER-" + UUID.randomUUID();
    }

    private <T> List<T> runTogether(int threads, java.util.concurrent.Callable<T> task) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        try {
            List<Future<T>> futures = submitTogether(executor, threads, index -> task.call());
            List<T> results = new ArrayList<>();
            for (Future<T> future : futures) {
                results.add(future.get(10, TimeUnit.SECONDS));
            }
            return results;
        } finally {
            executor.shutdownNow();
        }
    }

    private <T> List<Future<T>> submitTogether(ExecutorService executor, int threads, IndexedTask<T> task) throws InterruptedException {
        java.util.concurrent.CountDownLatch ready = new java.util.concurrent.CountDownLatch(threads);
        java.util.concurrent.CountDownLatch start = new java.util.concurrent.CountDownLatch(1);
        List<Future<T>> futures = new ArrayList<>();
        for (int index = 0; index < threads; index++) {
            int current = index;
            futures.add(executor.submit(() -> {
                ready.countDown();
                if (!start.await(5, TimeUnit.SECONDS)) {
                    throw new IllegalStateException("동시 요청 시작 대기 시간이 초과되었습니다.");
                }
                return task.run(current);
            }));
        }
        assertThat(ready.await(5, TimeUnit.SECONDS)).isTrue();
        start.countDown();
        return futures;
    }

    @FunctionalInterface
    private interface IndexedTask<T> {
        T run(int index) throws Exception;
    }
}
