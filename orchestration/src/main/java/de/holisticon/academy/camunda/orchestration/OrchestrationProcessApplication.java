package de.holisticon.academy.camunda.orchestration;

import de.holisticon.academy.camunda.orchestration.service.ApprovalRequestRepository;
import de.holisticon.academy.camunda.orchestration.service.AutomaticApprovalService;
import org.camunda.bpm.application.ProcessApplication;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@ProcessApplication
@SpringBootApplication
public class OrchestrationProcessApplication {

  public static void main(String[] args) {
    SpringApplication.run(OrchestrationProcessApplication.class, args);
  }

}
