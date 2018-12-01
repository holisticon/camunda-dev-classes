package de.holisticon.academy.camunda.orchestration.process;

import de.holisticon.academy.camunda.orchestration.process.ApprovalProcessBean.Variables;
import de.holisticon.academy.camunda.orchestration.service.ApprovalRequestRepository;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static de.holisticon.academy.camunda.orchestration.process.ApprovalProcessBean.Expressions.LOAD_APPROVAL_REQUEST;

@Component(LOAD_APPROVAL_REQUEST)
public class LoadApprovalRequestDelegate implements JavaDelegate {

  private static final Logger logger = LoggerFactory.getLogger(LoadApprovalRequestDelegate.class);

  private final ApprovalRequestRepository approvalRequestRepository;

  public LoadApprovalRequestDelegate(ApprovalRequestRepository approvalRequestRepository) {
    this.approvalRequestRepository = approvalRequestRepository;
  }

  public void execute(DelegateExecution execution) {
    String id = (String) execution.getVariable(Variables.APPROVAL_ID);

    approvalRequestRepository.findById(id).ifPresent(approvalRequest -> {
      execution.setVariable(Variables.REQUEST, approvalRequest);
      logger.info("Setting request {} as variable", approvalRequest);
    });
  }
}
