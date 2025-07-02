package de.holisticon.academy.camunda.orchestration.external;

import de.holisticon.academy.camunda.orchestration.service.ApprovalRequestRepository;
import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@ExternalTaskSubscription(topicName = "load-approval-request")
public class LoadApprovalRequestWorker implements ExternalTaskHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoadApprovalRequestWorker.class);

    private final ApprovalRequestRepository approvalRequestRepository;

    public LoadApprovalRequestWorker(ApprovalRequestRepository approvalRequestRepository) {
        this.approvalRequestRepository = approvalRequestRepository;
    }

    @Override
    public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {

    }
}
