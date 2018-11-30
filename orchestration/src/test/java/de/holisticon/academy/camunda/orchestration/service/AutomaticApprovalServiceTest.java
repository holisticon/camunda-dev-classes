package de.holisticon.academy.camunda.orchestration.service;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class AutomaticApprovalServiceTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();
  private AutomaticApprovalService service = new AutomaticApprovalService();

  @Test
  public void test_approve() {
    assertThat(service.approve(new ApprovalRequest(UUID.randomUUID().toString(), "request", "kermit", new BigDecimal("10")))).isTrue();
  }

  @Test
  public void test_error() {
    thrown.expectMessage("Something bad happened during approval");
    thrown.expect(RuntimeException.class);

    assertThat(service.approve(new ApprovalRequest(UUID.randomUUID().toString(), "request", "kermit", new BigDecimal("83.12")))).isTrue();
  }

}
