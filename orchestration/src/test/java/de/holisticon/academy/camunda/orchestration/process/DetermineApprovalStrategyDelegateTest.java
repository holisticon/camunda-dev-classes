package de.holisticon.academy.camunda.orchestration.process;

import de.holisticon.academy.camunda.orchestration.service.ApprovalRequest;
import org.camunda.bpm.extension.mockito.delegate.DelegateExecutionFake;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class DetermineApprovalStrategyDelegateTest {

  private final DetermineApprovalStrategyDelegate delegate = new DetermineApprovalStrategyDelegate();

  @Test
  void shouldSelectAutomaticStrategy() {

    DelegateExecutionFake execution = new DelegateExecutionFake().withVariable(ApprovalProcessBean.Variables.REQUEST,
      new ApprovalRequest("id", "subject", "kermit", new BigDecimal("12.17"))
    );

    delegate.execute(execution);

    assertThat(execution.getVariable(ApprovalProcessBean.Variables.APPROVAL_STRATEGY)).isNotNull();
    assertThat(execution.getVariable(ApprovalProcessBean.Variables.APPROVAL_STRATEGY)).isEqualTo(ApprovalProcessBean.Values.APPROVAL_STRATEGY_AUTOMATIC);
  }


  @Test
  void shouldSelectManualStrategy() {

    DelegateExecutionFake execution = new DelegateExecutionFake().withVariable(ApprovalProcessBean.Variables.REQUEST,
      new ApprovalRequest("id", "subject", "kermit", new BigDecimal("100.00"))
    );

    delegate.execute(execution);

    assertThat(execution.getVariable(ApprovalProcessBean.Variables.APPROVAL_STRATEGY)).isNotNull();
    assertThat(execution.getVariable(ApprovalProcessBean.Variables.APPROVAL_STRATEGY)).isEqualTo(ApprovalProcessBean.Values.APPROVAL_STRATEGY_MANUAL);
  }

}
