package com.example.demo.config.security;

import com.example.demo.audit.repository.AuditLogRepository;
import com.example.demo.support.MariaDbIntegrationSupport;
import com.example.demo.payment.repository.PaymentRequestRepository;
import com.example.demo.payment.repository.PaymentStatusHistoryRepository;
import com.example.demo.reconciliation.repository.ReconciliationResultRepository;
import com.example.demo.settlement.repository.SettlementRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApiAuthorizationIntegrationTest extends MariaDbIntegrationSupport {

    @Autowired
    private MockMvc mockMvc;

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

    @AfterEach
    void cleanup() {
        reconciliationResultRepository.deleteAll();
        settlementRepository.deleteAll();
        statusHistoryRepository.deleteAll();
        auditLogRepository.deleteAll();
        paymentRequestRepository.deleteAll();
    }

    @Test
    void allowsHealthAndLoginWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/health/healthcheck"))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));
        mockMvc.perform(get("/auth/health"))
                .andExpect(status().isOk());
    }

    @Test
    void rejectsAnonymousAccessToBusinessApis() throws Exception {
        mockMvc.perform(get("/payment-requests"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("인증이 필요합니다."));
        mockMvc.perform(post("/payment-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(paymentJson("anonymous-key")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("CSRF 토큰이 유효하지 않습니다."));
        mockMvc.perform(get("/settlements"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/reconciliations"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/audit-logs"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/k8s/pods"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void allowsViewerToReadAndDeniesMutations() throws Exception {
        String body = paymentJson("viewer-key");
        mockMvc.perform(post("/payment-requests")
                        .with(csrf()).with(user("operator").roles("OPERATOR"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/payment-requests").with(csrf()).with(user("viewer").roles("VIEWER")))
                .andExpect(status().isOk());
        mockMvc.perform(get("/audit-logs").with(csrf()).with(user("viewer").roles("VIEWER")))
                .andExpect(status().isOk());
        mockMvc.perform(get("/k8s/pods").with(csrf()).with(user("viewer").roles("VIEWER")))
                .andExpect(status().isOk());

        mockMvc.perform(post("/payment-requests")
                        .with(csrf()).with(user("viewer").roles("VIEWER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("이 작업을 수행할 권한이 없습니다."));

        Long id = paymentRequestRepository.findAll().get(0).getId();
        mockMvc.perform(post("/payment-requests/" + id + "/approve")
                        .with(csrf()).with(user("viewer").roles("VIEWER")))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/settlements/generate")
                        .with(csrf()).with(user("viewer").roles("VIEWER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"paymentRequestId\":" + id + "}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void allowsOperatorToChangePaymentState() throws Exception {
        MvcResult created = mockMvc.perform(post("/payment-requests")
                        .with(csrf()).with(user("operator").roles("OPERATOR"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(paymentJson("operator-key")))
                .andExpect(status().isCreated())
                .andReturn();
        Number id = com.jayway.jsonpath.JsonPath.read(created.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(post("/payment-requests/" + id + "/approve")
                        .with(csrf()).with(user("operator").roles("OPERATOR")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));

        mockMvc.perform(post("/payment-requests/" + id + "/reject")
                        .with(csrf()).with(user("operator").roles("OPERATOR"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reason\":\"이미 승인된 요청\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void deniesUserWithoutBusinessRole() throws Exception {
        mockMvc.perform(get("/payment-requests").with(csrf()).with(user("guest").roles("USER")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("이 작업을 수행할 권한이 없습니다."));
    }

    @Test
    void keepsKubernetesCommandsBehindRoleAndFeatureFlag() throws Exception {
        mockMvc.perform(put("/k8s/namespaces/default/deployments/example/scale")
                        .param("replicas", "2")
                        .with(csrf()).with(user("viewer").roles("VIEWER")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("이 작업을 수행할 권한이 없습니다."));

        mockMvc.perform(put("/k8s/namespaces/default/deployments/example/scale")
                        .param("replicas", "2")
                        .with(csrf()).with(user("operator").roles("OPERATOR")))
                .andExpect(status().isForbidden())
                .andExpect(content().string(containsString("비활성화")));

        mockMvc.perform(delete("/k8s/namespaces/default/pods/example")
                        .with(csrf()).with(user("admin").roles("ADMIN")))
                .andExpect(status().isForbidden())
                .andExpect(content().string(containsString("비활성화")));

        mockMvc.perform(post("/k8s/namespaces")
                        .param("name", "should-not-be-created")
                        .with(csrf()).with(user("admin").roles("ADMIN")))
                .andExpect(status().isForbidden())
                .andExpect(content().string(containsString("비활성화")));
    }

    @Test
    void dropsExternalLoginRedirect() throws Exception {
        MvcResult external = mockMvc.perform(get("/auth/login/keycloak").param("redirect", "//evil.example"))
                .andExpect(status().isFound())
                .andReturn();
        assertThat(external.getRequest().getSession(false).getAttribute(OAuth2LoginSuccessHandler.POST_LOGIN_REDIRECT_ATTRIBUTE))
                .isEqualTo("/");

        MvcResult internal = mockMvc.perform(get("/auth/login/keycloak").param("redirect", "/payment-requests"))
                .andExpect(status().isFound())
                .andReturn();
        assertThat(internal.getRequest().getSession(false).getAttribute(OAuth2LoginSuccessHandler.POST_LOGIN_REDIRECT_ATTRIBUTE))
                .isEqualTo("/payment-requests");
    }

    @Test
    void rejectsAuthenticatedMutationWithoutCsrfToken() throws Exception {
        mockMvc.perform(post("/payment-requests")
                        .with(user("operator").roles("OPERATOR"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(paymentJson("missing-csrf")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("CSRF 토큰이 유효하지 않습니다."));
    }

    @Test
    void acceptsMutationWhenCsrfTokenComesFromEndpoint() throws Exception {
        MvcResult csrfResult = mockMvc.perform(get("/auth/csrf"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.headerName").value("X-CSRF-TOKEN"))
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn();
        String token = com.jayway.jsonpath.JsonPath.read(csrfResult.getResponse().getContentAsString(), "$.token");
        String headerName = com.jayway.jsonpath.JsonPath.read(csrfResult.getResponse().getContentAsString(), "$.headerName");
        MockHttpSession session = (MockHttpSession) csrfResult.getRequest().getSession(false);

        mockMvc.perform(post("/payment-requests")
                        .session(session)
                        .header(headerName, token)
                        .with(user("operator").roles("OPERATOR"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(paymentJson("csrf-endpoint-key")))
                .andExpect(status().isCreated());
    }

    private String paymentJson(String idempotencyKey) {
        return """
                {"idempotencyKey":"%s","amount":1000,"requestedBy":"external-order-api"}
                """.formatted(idempotencyKey);
    }
}
