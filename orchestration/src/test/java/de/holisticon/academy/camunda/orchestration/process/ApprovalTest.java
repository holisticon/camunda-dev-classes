package de.holisticon.academy.camunda.orchestration.process;

import de.holisticon.academy.camunda.orchestration.process.ApprovalProcessBean.Elements;
import de.holisticon.academy.camunda.orchestration.process.ApprovalProcessBean.Expressions;
import de.holisticon.academy.camunda.orchestration.service.ApprovalRequestRepository;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.engine.test.mock.Mocks;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.extension.mockito.CamundaMockito;
import org.camunda.bpm.spring.boot.starter.test.helper.ProcessEngineRuleRunner;
import org.camunda.bpm.spring.boot.starter.test.helper.StandaloneInMemoryTestConfiguration;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import sun.net.ExtendedOptionsImpl;

import static org.camunda.bpm.engine.test.assertions.bpmn.AbstractAssertions.init;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareAssertions.assertThat;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.execute;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.job;
import static org.mockito.Mockito.mock;

@RunWith(ProcessEngineRuleRunner.class)
@Deployment(resources = "approval.bpmn")
public class ApprovalTest {


  @Rule
  public final ProcessEngineRule engine = new StandaloneInMemoryTestConfiguration().rule();
  private ApprovalProcessBean processBean;

  @Before
  public void before() {
    this.processBean = new ApprovalProcessBean(this.engine.getRuntimeService());
    init(engine.getProcessEngine());

    CamundaMockito.registerJavaDelegateMock(Expressions.DETERMINE_APPROVAL_STRATEGY);

    Mocks.register(Expressions.LOAD_APPROVAL_REQUEST, new LoadApprovalRequestDelegate(mock(ApprovalRequestRepository.class)));
  }

  @Test
  public void shouldDeploy() {
    // no asserts, deployment would throw exception and fail the test on errors
  }

  @Test
  public void shouldStartWaitInApprovalRequested() {
    ProcessInstance instance = this.processBean.start("1");

    assertThat(instance).isNotNull();
    assertThat(instance).isWaitingAt(Elements.APPROVAL_REQUESTED);
  }


  @Test
  public void shouldStartAndLoadAndApprove() {
    CamundaMockito.getJavaDelegateMock(Expressions.DETERMINE_APPROVAL_STRATEGY)
      .onExecutionSetVariables(Variables.putValue(ApprovalProcessBean.Variables.APPROVAL_STRATEGY, ApprovalProcessBean.Values.APPROVAL_STRATEGY_AUTOMATIC));

    ProcessInstance instance = this.processBean.start("1");

    assertThat(instance).isNotNull();
    assertThat(instance).isWaitingAt(Elements.APPROVAL_REQUESTED);

    execute(job());

    assertThat(instance).isEnded();
    assertThat(instance).hasPassedInOrder(
      Elements.APPROVAL_REQUESTED, Elements.LOAD_APPROVAL_REQUEST, Elements.DETERMINE_APPROVAL_STRATEGY, Elements.REQUEST_APPROVED);
  }

  @Test
  public void shouldStartAndLoadAndReject() {
    CamundaMockito.getJavaDelegateMock(Expressions.DETERMINE_APPROVAL_STRATEGY)
      .onExecutionSetVariables(Variables.putValue(ApprovalProcessBean.Variables.APPROVAL_STRATEGY, "foo"));

    ProcessInstance instance = this.processBean.start("1");

    assertThat(instance).isNotNull();
    assertThat(instance).isWaitingAt(Elements.APPROVAL_REQUESTED);

    execute(job());

    assertThat(instance).isEnded();
    assertThat(instance).hasPassedInOrder(
      Elements.APPROVAL_REQUESTED, Elements.LOAD_APPROVAL_REQUEST, Elements.DETERMINE_APPROVAL_STRATEGY, Elements.REQUEST_REJECTED);
  }

}
