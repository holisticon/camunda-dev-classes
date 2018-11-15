package de.holisticon.academy.camunda.orchestration.process;

import de.holisticon.academy.camunda.orchestration.process.SimpleDataProcessingProcessBean.Variables;
import de.holisticon.academy.camunda.orchestration.service.ApprovalRequestRepository;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static de.holisticon.academy.camunda.orchestration.process.SimpleDataProcessingProcessBean.Expressions.LOAD_DATA_DELEGATE;

@Component(LOAD_DATA_DELEGATE)
public class LoadDataDelegate implements JavaDelegate {

  private static final Logger logger = LoggerFactory.getLogger(LoadDataDelegate.class);

  private final ApprovalRequestRepository approvalRequestRepository;

  public LoadDataDelegate(ApprovalRequestRepository approvalRequestRepository) {
    this.approvalRequestRepository = approvalRequestRepository;
  }

  public void execute(DelegateExecution execution) {
    String id = (String) execution.getVariable(Variables.APPROVAL_ID);

    approvalRequestRepository.findById(id).ifPresent(approvalRequest -> {
      execution.setVariable(Variables.AMOUNT, approvalRequest.getAmount());
      logger.info("Setting amount {} as variable", approvalRequest.getAmount());
    });
  }
}
