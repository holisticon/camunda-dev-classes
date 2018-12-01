package de.holisticon.academy.camunda.orchestration.process;

import de.holisticon.academy.camunda.orchestration.service.ApprovalRequestRepository;
import de.holisticon.academy.camunda.orchestration.service.AutomaticApprovalService;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component
public class AutomaticApproveRequestDelegate implements JavaDelegate {

  private final ApprovalRequestRepository approvalRequestRepository;
  private final AutomaticApprovalService automaticApprovalService;

  public AutomaticApproveRequestDelegate(ApprovalRequestRepository approvalRequestRepository, AutomaticApprovalService automaticApprovalService) {
    this.approvalRequestRepository = approvalRequestRepository;
    this.automaticApprovalService = automaticApprovalService;
  }

  @Override
  public void execute(DelegateExecution execution) {
    final String id = (String) execution.getVariable(ApprovalProcessBean.Variables.APPROVAL_ID);

    approvalRequestRepository.findById(id).ifPresent(approvalRequest -> {
      try {
        boolean approvalResult = automaticApprovalService.approve(approvalRequest);
        if (approvalResult) {
          execution.setVariable(ApprovalProcessBean.Variables.APPROVAL_DECISION, ApprovalProcessBean.Values.APPROVAL_DECISION_APPROVED);
        } else {
          execution.setVariable(ApprovalProcessBean.Variables.APPROVAL_DECISION, ApprovalProcessBean.Values.APPROVAL_DECISION_REJECTED);
        }
      } catch (RuntimeException e) {
        throw new BpmnError(ApprovalProcessBean.Expressions.ERROR);
      }
    });
  }
}
