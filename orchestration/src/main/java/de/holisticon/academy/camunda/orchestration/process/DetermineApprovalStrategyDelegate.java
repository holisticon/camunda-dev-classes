package de.holisticon.academy.camunda.orchestration.process;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DetermineApprovalStrategyDelegate implements JavaDelegate {

  private static final BigDecimal AUTO_STRATEGY_THRESHOLD = new BigDecimal("100.0");

  @Override
  public void execute(DelegateExecution delegateExecution) {
    final var request = ApprovalProcessBean.Variables.REQUEST.from(delegateExecution).get();
    if (AUTO_STRATEGY_THRESHOLD.compareTo(request.getAmount()) > 0) {
      ApprovalProcessBean.Variables.APPROVAL_STRATEGY.on(delegateExecution).set(ApprovalProcessBean.Values.APPROVAL_STRATEGY_AUTOMATIC);
    } else {
      ApprovalProcessBean.Variables.APPROVAL_STRATEGY.on(delegateExecution).set(ApprovalProcessBean.Values.APPROVAL_STRATEGY_MANUAL);
    }
  }
}
