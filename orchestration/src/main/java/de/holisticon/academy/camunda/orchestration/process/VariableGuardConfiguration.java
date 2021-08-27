package de.holisticon.academy.camunda.orchestration.process;

import io.holunda.camunda.bpm.data.guard.integration.DefaultGuardTaskListener;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static io.holunda.camunda.bpm.data.guard.CamundaBpmDataGuards.exists;

@Configuration
public class VariableGuardConfiguration {

  public static final String MANUAL_APPROVAL_GUARD = "manualApprovalGuard";

  @Bean(VariableGuardConfiguration.MANUAL_APPROVAL_GUARD)
  public TaskListener manualApprovalGuard() {
    return new DefaultGuardTaskListener(
      List.of(
        exists(ApprovalProcessBean.Variables.APPROVAL_DECISION) // conditions
      ), true // throw violation as exception
    );
  }

}
