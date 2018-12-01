package de.holisticon.academy.camunda.orchestration.process;

import de.holisticon.academy.camunda.orchestration.service.ApprovalRequest;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DetermineApprovalStrategyDelegate implements JavaDelegate {

  private static final BigDecimal AUTO_STRATEGY_THRESHOLD = new BigDecimal("100.0");

  @Override
  public void execute(DelegateExecution delegateExecution) {
    final ApprovalRequest request = (ApprovalRequest) delegateExecution.getVariable(ApprovalProcessBean.Variables.REQUEST);
    if (AUTO_STRATEGY_THRESHOLD.compareTo(request.getAmount()) > 0) {
      delegateExecution.setVariable(ApprovalProcessBean.Variables.APPROVAL_STRATEGY, ApprovalProcessBean.Values.APPROVAL_STRATEGY_AUTOMATIC);
    } else {
      delegateExecution.setVariable(ApprovalProcessBean.Variables.APPROVAL_STRATEGY, ApprovalProcessBean.Values.APPROVAL_STRATEGY_MANUAL);
    }
  }
}
