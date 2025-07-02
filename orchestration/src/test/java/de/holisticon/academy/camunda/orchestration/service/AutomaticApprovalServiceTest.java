package de.holisticon.academy.camunda.orchestration.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AutomaticApprovalServiceTest {

    private final AutomaticApprovalService service = new AutomaticApprovalService();

    @Test
    void testApprove() {
        assertThat(service.approve(new ApprovalRequest(UUID.randomUUID().toString(), "request", "kermit", new BigDecimal("10")))).isTrue();
    }

    @Test
    void testError() {
        assertThatThrownBy(() -> service.approve(new ApprovalRequest(UUID.randomUUID().toString(), "request", "kermit", new BigDecimal("83.12"))))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Something bad happened during approval");
    }
}
