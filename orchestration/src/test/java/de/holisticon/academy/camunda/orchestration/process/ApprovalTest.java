package de.holisticon.academy.camunda.orchestration.process;

import de.holisticon.academy.camunda.orchestration.process.ApprovalProcessBean.Elements;
import de.holisticon.academy.camunda.orchestration.process.ApprovalProcessBean.Expressions;
import de.holisticon.academy.camunda.orchestration.service.ApprovalRequest;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.engine.test.mock.Mocks;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.extension.mockito.CamundaMockito;
import org.camunda.bpm.spring.boot.starter.test.helper.ProcessEngineRuleRunner;
import org.camunda.bpm.spring.boot.starter.test.helper.StandaloneInMemoryTestConfiguration;
import org.camunda.spin.plugin.impl.SpinProcessEnginePlugin;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;

import static org.camunda.bpm.engine.test.assertions.bpmn.AbstractAssertions.init;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareAssertions.assertThat;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.*;

@RunWith(ProcessEngineRuleRunner.class)
@Deployment(resources = {"approval.bpmn", "approvalStrategy.dmn"})
public class ApprovalTest {


  @Rule
  public final ProcessEngineRule engine = createEngine();
  private ApprovalProcessBean processBean;

  @Before
  public void before() {
    this.processBean = new ApprovalProcessBean(this.engine.getRuntimeService(), this.engine.getTaskService());
    init(engine.getProcessEngine());

    CamundaMockito.registerJavaDelegateMock(Expressions.DETERMINE_APPROVAL_STRATEGY);
    CamundaMockito.registerJavaDelegateMock(Expressions.LOAD_APPROVAL_REQUEST);
    CamundaMockito.registerJavaDelegateMock(Expressions.AUTO_APPROVE_REQUEST);

    Mocks.register(Expressions.AUDIT, new AuditListener());
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
    CamundaMockito.getJavaDelegateMock(Expressions.LOAD_APPROVAL_REQUEST)
      .onExecutionSetVariables(Variables.putValue(ApprovalProcessBean.Variables.REQUEST, new ApprovalRequest("id", "subj", "kermit", new BigDecimal("7.81"))));


    ProcessInstance instance = this.processBean.start("1");

    assertThat(instance).isNotNull();
    assertThat(instance).isWaitingAt(Elements.APPROVAL_REQUESTED);

    execute(job());

    assertThat(instance).isEnded();
    assertThat(instance).hasPassedInOrder(
      Elements.APPROVAL_REQUESTED, Elements.LOAD_APPROVAL_REQUEST, Elements.DETERMINE_APPROVAL_STRATEGY, Elements.REQUEST_APPROVED);
  }

  @Test
  public void shouldStartAndLoadAndManual() {
    CamundaMockito.getJavaDelegateMock(Expressions.LOAD_APPROVAL_REQUEST)
      .onExecutionSetVariables(Variables.putValue(ApprovalProcessBean.Variables.REQUEST, new ApprovalRequest("id", "subj", "kermit", new BigDecimal("117.81"))));

    ProcessInstance instance = this.processBean.start("1");

    assertThat(instance).isNotNull();
    assertThat(instance).isWaitingAt(Elements.APPROVAL_REQUESTED);

    execute(job());

    assertThat(instance).isWaitingAt(Elements.USER_APPROVE_REQUEST);

  }

  @Test
  public void shouldStartAndLoadAndManualAndApprove() {
    CamundaMockito.getJavaDelegateMock(Expressions.LOAD_APPROVAL_REQUEST)
      .onExecutionSetVariables(Variables.putValue(ApprovalProcessBean.Variables.REQUEST, new ApprovalRequest("id", "subj", "kermit", new BigDecimal("117.81"))));

    ProcessInstance instance = this.processBean.start("1");

    assertThat(instance).isNotNull();
    assertThat(instance).isWaitingAt(Elements.APPROVAL_REQUESTED);

    execute(job());

    assertThat(instance).isWaitingAt(Elements.USER_APPROVE_REQUEST);
    this.processBean.complete(task().getId(), Variables.putValue(ApprovalProcessBean.Variables.APPROVAL_DECISION, ApprovalProcessBean.Values.APPROVAL_DECISION_APPROVED));
    execute(job());

    assertThat(instance).isEnded();
    assertThat(instance).hasPassedInOrder(
      Elements.APPROVAL_REQUESTED, Elements.LOAD_APPROVAL_REQUEST, Elements.DETERMINE_APPROVAL_STRATEGY, Elements.USER_APPROVE_REQUEST, Elements.REQUEST_APPROVED
    );
  }

  @Test
  public void shouldStartAndLoadAndManualAndReject() {
    CamundaMockito.getJavaDelegateMock(Expressions.LOAD_APPROVAL_REQUEST)
      .onExecutionSetVariables(Variables.putValue(ApprovalProcessBean.Variables.REQUEST, new ApprovalRequest("id", "subj", "kermit", new BigDecimal("117.81"))));

    ProcessInstance instance = this.processBean.start("1");

    assertThat(instance).isNotNull();
    assertThat(instance).isWaitingAt(Elements.APPROVAL_REQUESTED);

    execute(job());

    assertThat(instance).isWaitingAt(Elements.USER_APPROVE_REQUEST);
    this.processBean.complete(task().getId(), Variables.putValue(ApprovalProcessBean.Variables.APPROVAL_DECISION, ApprovalProcessBean.Values.APPROVAL_DECISION_REJECTED));
    execute(job());

    assertThat(instance).isEnded();
    assertThat(instance).hasPassedInOrder(
      Elements.APPROVAL_REQUESTED, Elements.LOAD_APPROVAL_REQUEST, Elements.DETERMINE_APPROVAL_STRATEGY, Elements.USER_APPROVE_REQUEST, Elements.REQUEST_REJECTED
    );
  }

  @Test
  public void shouldStartAndLoadAndManualAndReturnedAndCancel() {
    CamundaMockito.getJavaDelegateMock(Expressions.LOAD_APPROVAL_REQUEST)
      .onExecutionSetVariables(Variables.putValue(ApprovalProcessBean.Variables.REQUEST, new ApprovalRequest("id", "subj", "kermit", new BigDecimal("117.81"))));

    ProcessInstance instance = this.processBean.start("1");

    assertThat(instance).isNotNull();
    assertThat(instance).isWaitingAt(Elements.APPROVAL_REQUESTED);

    execute(job());

    assertThat(instance).isWaitingAt(Elements.USER_APPROVE_REQUEST);
    this.processBean.complete(task().getId(), Variables.putValue(ApprovalProcessBean.Variables.APPROVAL_DECISION, ApprovalProcessBean.Values.APPROVAL_DECISION_RETURNED));
    execute(job());

    assertThat(instance).isWaitingAt(Elements.USER_AMEND_REQUEST);
    this.processBean.complete(task().getId(), Variables.putValue(ApprovalProcessBean.Variables.AMEND_ACTION, ApprovalProcessBean.Values.AMEND_ACTION_CANCELLED));
    execute(job());

    assertThat(instance).isEnded();
    assertThat(instance).hasPassedInOrder(
      Elements.APPROVAL_REQUESTED, Elements.LOAD_APPROVAL_REQUEST, Elements.DETERMINE_APPROVAL_STRATEGY, Elements.USER_APPROVE_REQUEST, Elements.REQUEST_CANCELLED
    );
  }

  @Test
  public void shouldStartAndLoadAndManualAndReturnedAndResubmit() {
    CamundaMockito.getJavaDelegateMock(Expressions.LOAD_APPROVAL_REQUEST)
      .onExecutionSetVariables(Variables.putValue(ApprovalProcessBean.Variables.REQUEST, new ApprovalRequest("id", "subj", "kermit", new BigDecimal("117.81"))));

    ProcessInstance instance = this.processBean.start("1");

    assertThat(instance).isNotNull();
    assertThat(instance).isWaitingAt(Elements.APPROVAL_REQUESTED);

    execute(job());

    assertThat(instance).isWaitingAt(Elements.USER_APPROVE_REQUEST);
    this.processBean.complete(task().getId(), Variables.putValue(ApprovalProcessBean.Variables.APPROVAL_DECISION, ApprovalProcessBean.Values.APPROVAL_DECISION_RETURNED));
    execute(job());

    assertThat(instance).isWaitingAt(Elements.USER_AMEND_REQUEST);
    this.processBean.complete(task().getId(), Variables.putValue(ApprovalProcessBean.Variables.AMEND_ACTION, ApprovalProcessBean.Values.AMEND_ACTION_RESUBMITTED));
    execute(job());

    assertThat(instance).isWaitingAt(Elements.USER_APPROVE_REQUEST);
  }

  static ProcessEngineRule createEngine() {
    StandaloneInMemoryTestConfiguration config = new StandaloneInMemoryTestConfiguration();
    config.getProcessEnginePlugins().add(new SpinProcessEnginePlugin());
    return config.rule();
  }
}
