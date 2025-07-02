package de.holisticon.academy.camunda.orchestration.external;

import de.holisticon.academy.camunda.orchestration.process.Variables;
import de.holisticon.academy.camunda.orchestration.service.ApprovalRequestRepository;
import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
@ExternalTaskSubscription()
public class LoadApprovalRequestWorker implements ExternalTaskHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(LoadApprovalRequestWorker.class);

  private final ApprovalRequestRepository approvalRequestRepository;

  public LoadApprovalRequestWorker(ApprovalRequestRepository approvalRequestRepository) {
    this.approvalRequestRepository = approvalRequestRepository;
  }

  @Override
  public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {

    String id = externalTask.getVariable(Variables.APPROVAL_ID);

    approvalRequestRepository.findById(id).ifPresentOrElse(
      approvalRequest -> {
        externalTaskService.complete(externalTask,
          Map.of(Variables.REQUEST, approvalRequest)
        );
        LOGGER.info("Setting request {} as variable", approvalRequest);
      },
      () -> {
        int retries = Optional.ofNullable(externalTask.getRetries()).orElse(3);

        externalTaskService.handleFailure(
          externalTask.getId(),
          "Error while loading approval request",
          "Could not load request with id \" + id",
          retries - 1,
          10_000
        );
      }
    );
  }
}
