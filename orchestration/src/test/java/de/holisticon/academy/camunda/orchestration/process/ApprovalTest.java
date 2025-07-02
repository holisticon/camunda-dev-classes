package de.holisticon.academy.camunda.orchestration.process;

import org.camunda.bpm.engine.ProcessEngineConfiguration;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.junit5.ProcessEngineExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.camunda.bpm.engine.test.assertions.bpmn.AbstractAssertions.init;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;

@Deployment(resources = "approval.bpmn")
class ApprovalTest {

  @RegisterExtension
  static ProcessEngineExtension extension = ProcessEngineExtension.builder()
    .useProcessEngine(
      ProcessEngineConfiguration
        .createStandaloneInMemProcessEngineConfiguration()
        .buildProcessEngine()
    )
    .build();

  private ApprovalProcessBean processBean;

  @BeforeEach
  void setUp() {
    this.processBean = new ApprovalProcessBean(extension.getRuntimeService());
    init(extension.getProcessEngine());
  }

  @Test
  void shouldDeploy() {
    // no asserts, deployment would throw exception and fail the test on errors
  }

    @Test
    public void shouldStartWaitInApprovalRequested() {
        ProcessInstance instance = this.processBean.start();

        assertThat(instance).isNotNull();
        assertThat(instance).isWaitingAt(ApprovalProcessBean.Elements.APPROVAL_REQUESTED);
    }

}
