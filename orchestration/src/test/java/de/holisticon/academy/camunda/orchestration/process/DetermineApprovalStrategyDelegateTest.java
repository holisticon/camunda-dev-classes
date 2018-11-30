package de.holisticon.academy.camunda.orchestration.process;

import org.camunda.bpm.extension.mockito.delegate.DelegateExecutionFake;
import org.junit.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class DetermineApprovalStrategyDelegateTest {

  private final DetermineApprovalStrategyDelegate delegate = new DetermineApprovalStrategyDelegate();

  @Test
  public void shouldSelectAutomaticStrategy() {

    DelegateExecutionFake execution = new DelegateExecutionFake().withVariable(ApprovalProcessBean.Variables.AMOUNT, new BigDecimal("12.17"));

    delegate.execute(execution);

    assertThat(execution.getVariable(ApprovalProcessBean.Variables.APPROVAL_STRATEGY)).isNotNull();
    assertThat(execution.getVariable(ApprovalProcessBean.Variables.APPROVAL_STRATEGY)).isEqualTo(ApprovalProcessBean.Values.APPROVAL_STRATEGY_AUTOMATIC);
  }


  @Test
  public void shouldSelectManualStrategy() {

    DelegateExecutionFake execution = new DelegateExecutionFake().withVariable(ApprovalProcessBean.Variables.AMOUNT, new BigDecimal("100.00"));

    delegate.execute(execution);

    assertThat(execution.getVariable(ApprovalProcessBean.Variables.APPROVAL_STRATEGY)).isNotNull();
    assertThat(execution.getVariable(ApprovalProcessBean.Variables.APPROVAL_STRATEGY)).isEqualTo(ApprovalProcessBean.Values.APPROVAL_STRATEGY_MANUAL);
  }

}
