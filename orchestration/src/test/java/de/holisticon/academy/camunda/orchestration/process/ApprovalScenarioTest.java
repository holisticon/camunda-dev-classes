package de.holisticon.academy.camunda.orchestration.process;

import com.tngtech.jgiven.annotation.*;
import com.tngtech.jgiven.junit5.DualScenarioTest;
import de.holisticon.academy.camunda.orchestration.process.ApprovalProcessBean.Elements;
import de.holisticon.academy.camunda.orchestration.process.ApprovalProcessBean.Expressions;
import de.holisticon.academy.camunda.orchestration.service.ApprovalRequest;
import io.holunda.camunda.bpm.data.CamundaBpmData;
import io.holunda.camunda.bpm.extension.jgiven.ProcessStage;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngineException;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.impl.util.ClockUtil;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests;
import org.camunda.bpm.engine.test.junit5.ProcessEngineExtension;
import org.camunda.bpm.engine.test.mock.Mocks;
import org.camunda.bpm.extension.mockito.CamundaMockito;
import org.camunda.bpm.spring.boot.starter.test.helper.StandaloneInMemoryTestConfiguration;
import org.camunda.spin.plugin.impl.SpinProcessEnginePlugin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.math.BigDecimal;
import java.time.Duration;

@Deployment(resources = {"approval.bpmn", "approvalStrategy.dmn"})
class ApprovalScenarioTest extends DualScenarioTest<ApprovalScenarioTest.GivenWhenStage, ApprovalScenarioTest.ThenStage> {

  @RegisterExtension
  static ProcessEngineExtension extension = createExtension();

  @ProvidedScenarioState
  static ProcessEngine camunda = extension.getProcessEngine();

  @Test
  @Hidden
  void should_deploy() {
    then()
      .process_is_deployed("approval");
  }

  @Test
  @As("When a new Instance was started, it should wait in APPROVAL_REQUESTED")
  void shouldStartWaitInApprovalRequested() {
    when()
      .process_is_started_with_$("1")
    ;
    then()
      .process_waits_in(Elements.APPROVAL_REQUESTED)
    ;
  }

  @Test
  @As("Approval requests with an amount below the threshold are approved automatically")
  void shouldStartAndLoadAndApprove() {
    when()
      .process_is_started_with_$("id")
      .and()
      .job_is_executed()
      .and()
      .approval_request_is_loaded_$(
        new ApprovalRequest("id", "subj", "kermit", new BigDecimal("7.81"))
      )
    ;
    then()
      .process_is_finished()
      .and()
      .process_has_passed(
        Elements.APPROVAL_REQUESTED, Elements.LOAD_APPROVAL_REQUEST, Elements.DETERMINE_APPROVAL_STRATEGY, Elements.REQUEST_APPROVED
      )
    ;
  }

  @Test
  @As("ApprovalRequests, which are applicable for automatic approval, will fallback to manual approval, when automatic approval fails")
  void shouldStartAndLoadAndApproveAndFail() {
    given()
      .auto_approval_is_not_possible()
    ;
    when()
      .process_is_started_with_$("id")
      .and()
      .job_is_executed()
      .and()
      .approval_request_is_loaded_$(
        new ApprovalRequest("id", "subj", "kermit", new BigDecimal("7.81"))
      )
    ;
    then()
      .process_waits_in(Elements.USER_APPROVE_REQUEST)
      .and()
      .process_has_passed(
        Elements.LOAD_APPROVAL_REQUEST, Elements.AUTO_APPROVE_REQUEST
      )
    ;
  }

  @Test
  @As("Approval requests with an amount above the threshold have to be approved manually")
  public void shouldStartAndLoadAndManual() {
    when()
      .process_is_started_with_$("id")
      .and()
      .job_is_executed()
      .and()
      .approval_request_is_loaded_$(
        new ApprovalRequest("id", "subj", "kermit", new BigDecimal("117.81"))
      )
    ;
    then()
      .process_waits_in(Elements.USER_APPROVE_REQUEST)
    ;
  }

  @Test
  @As("Approval requests can be approved manually")
  void shouldStartAndLoadAndManualAndApprove() {
    when()
      .process_is_started_with_$("id")
      .and()
      .job_is_executed()
      .and()
      .approval_request_is_loaded_$(
        new ApprovalRequest("id", "subj", "kermit", new BigDecimal("117.81"))
      )
    ;
    then()
      .process_waits_in(Elements.USER_APPROVE_REQUEST)
    ;
    when()
      .task_is_completed_with_variables(
        CamundaBpmData.builder().set(ApprovalProcessBean.Variables.APPROVAL_DECISION, ApprovalProcessBean.Values.APPROVAL_DECISION_APPROVED).build(),
        true
      )
    ;
    then()
      .process_is_finished()
      .and()
      .process_has_passed(
        Elements.APPROVAL_REQUESTED, Elements.LOAD_APPROVAL_REQUEST, Elements.DETERMINE_APPROVAL_STRATEGY, Elements.USER_APPROVE_REQUEST, Elements.REQUEST_APPROVED
      )
    ;
  }

  @Test
  @As("Approval requests, which are applicable for manual approval, can be rejected")
  void shouldStartAndLoadAndManualAndReject() {
    when()
      .process_is_started_with_$("id")
      .and()
      .job_is_executed()
      .and()
      .approval_request_is_loaded_$(
        new ApprovalRequest("id", "subj", "kermit", new BigDecimal("117.81"))
      )
    ;
    then()
      .process_waits_in(Elements.USER_APPROVE_REQUEST)
    ;
    when()
      .task_is_completed_with_variables(
        CamundaBpmData.builder().set(ApprovalProcessBean.Variables.APPROVAL_DECISION, ApprovalProcessBean.Values.APPROVAL_DECISION_REJECTED).build(),
        true
      )
    ;
    then()
      .process_is_finished()
      .and()
      .process_has_passed(
        Elements.APPROVAL_REQUESTED, Elements.LOAD_APPROVAL_REQUEST, Elements.DETERMINE_APPROVAL_STRATEGY, Elements.USER_APPROVE_REQUEST, Elements.REQUEST_REJECTED
      )
    ;
  }

  @Test
  @As("Approval requests, which are returned during manual approval, can be canceled by the requester")
  void shouldStartAndLoadAndManualAndReturnedAndCancel() {
    when()
      .process_is_started_with_$("id")
      .and()
      .job_is_executed()
      .and()
      .approval_request_is_loaded_$(
        new ApprovalRequest("id", "subj", "kermit", new BigDecimal("117.81"))
      )
    ;
    then()
      .process_waits_in(Elements.USER_APPROVE_REQUEST)
    ;
    when()
      .task_is_completed_with_variables(
        CamundaBpmData.builder().set(ApprovalProcessBean.Variables.APPROVAL_DECISION, ApprovalProcessBean.Values.APPROVAL_DECISION_RETURNED).build(),
        true
      )
    ;
    then()
      .process_waits_in(Elements.USER_AMEND_REQUEST)
    ;
    when()
      .task_is_completed_with_variables(
        CamundaBpmData.builder().set(ApprovalProcessBean.Variables.AMEND_ACTION, ApprovalProcessBean.Values.AMEND_ACTION_CANCELLED).build(),
        true
      )
    ;
    then()
      .process_is_finished()
      .and()
      .process_has_passed(
        Elements.APPROVAL_REQUESTED, Elements.LOAD_APPROVAL_REQUEST, Elements.DETERMINE_APPROVAL_STRATEGY, Elements.USER_APPROVE_REQUEST, Elements.REQUEST_CANCELLED
      )
    ;
  }

  @Test
  @As("Approval requests, which are returned during manual approval, will be canceled after 2 minutes")
  void shouldStartAndLoadAndManualAndReturnedAndCancelByTimeout() {
    when()
      .process_is_started_with_$("id")
      .and()
      .job_is_executed()
      .and()
      .approval_request_is_loaded_$(
        new ApprovalRequest("id", "subj", "kermit", new BigDecimal("117.81"))
      )
    ;
    then()
      .process_waits_in(Elements.USER_APPROVE_REQUEST)
    ;
    when()
      .task_is_completed_with_variables(
        CamundaBpmData.builder().set(ApprovalProcessBean.Variables.APPROVAL_DECISION, ApprovalProcessBean.Values.APPROVAL_DECISION_RETURNED).build(),
        true
      )
      .and()
      .$_minutes_pass(5L)
      .and()
      .job_is_executed()
    ;
    then()
      .process_is_finished()
      .and()
      .process_has_passed(
        Elements.APPROVAL_REQUESTED, Elements.LOAD_APPROVAL_REQUEST, Elements.DETERMINE_APPROVAL_STRATEGY, Elements.USER_APPROVE_REQUEST, Elements.REQUEST_CANCELLED
      )
    ;
  }

  @Test
  @As("Approval requests, which are returned during manual approval, can be resubmitted by the requester")
  public void shouldStartAndLoadAndManualAndReturnedAndResubmit() {
    when()
      .process_is_started_with_$("id")
      .and()
      .job_is_executed()
      .and()
      .approval_request_is_loaded_$(
        new ApprovalRequest("id", "subj", "kermit", new BigDecimal("117.81"))
      )
    ;
    then()
      .process_waits_in(Elements.USER_APPROVE_REQUEST)
    ;
    when()
      .task_is_completed_with_variables(
        CamundaBpmData.builder().set(ApprovalProcessBean.Variables.APPROVAL_DECISION, ApprovalProcessBean.Values.APPROVAL_DECISION_RETURNED).build(),
        true
      )
    ;
    then()
      .process_waits_in(Elements.USER_AMEND_REQUEST)
    ;
    when()
      .task_is_completed_with_variables(
        CamundaBpmData.builder().set(ApprovalProcessBean.Variables.AMEND_ACTION, ApprovalProcessBean.Values.AMEND_ACTION_RESUBMITTED).build(),
        true
      )
      .and()
      .approval_request_is_loaded_$(
        new ApprovalRequest("id", "subj", "kermit", new BigDecimal("117.81"))
      )
    ;
    then()
      .process_waits_in(Elements.USER_APPROVE_REQUEST)
    ;
  }

  @Test
  @As("Approval requests cannot be approved manually without an approval decision")
  void cannotCompleteManualApprovalTaskWithoutApprovalDecision() {
    when()
      .process_is_started_with_$("id")
      .and()
      .job_is_executed()
      .and()
      .approval_request_is_loaded_$(
        new ApprovalRequest("id", "subj", "kermit", new BigDecimal("117.81"))
      )
    ;
    then()
      .process_waits_in(Elements.USER_APPROVE_REQUEST)
    ;
    when()
      .task_is_completed_without_variables()
    ;
    then()
      .process_waits_in(Elements.USER_APPROVE_REQUEST)
    ;
  }


  static ProcessEngineExtension createExtension() {
    StandaloneInMemoryTestConfiguration config = new StandaloneInMemoryTestConfiguration();
    config.getProcessEnginePlugins().add(new SpinProcessEnginePlugin());

    return ProcessEngineExtension.builder()
      .useProcessEngine(config.buildProcessEngine())
      .build();
  }

  public static class GivenWhenStage extends ProcessStage<GivenWhenStage, ApprovalProcessInstance> {

    @ProvidedScenarioState
    private ApprovalProcessBean approvalProcessBean;

    @BeforeStage
    public void before() {
      BpmnAwareTests.init(camunda);

      CamundaMockito.registerJavaDelegateMock(Expressions.DETERMINE_APPROVAL_STRATEGY);
      CamundaMockito.registerJavaDelegateMock(Expressions.AUTO_APPROVE_REQUEST);

      Mocks.register(Expressions.AUDIT, new AuditListener());

      CamundaMockito.registerInstance(VariableGuardConfiguration.MANUAL_APPROVAL_GUARD, new VariableGuardConfiguration().manualApprovalGuard());

      approvalProcessBean = new ApprovalProcessBean(camunda.getRuntimeService(), camunda.getTaskService());
    }

    public GivenWhenStage approval_request_is_loaded_$(ApprovalRequest approvalRequest) {
      external_task_exists("load-approval-request");
      external_task_is_completed(
        "load-approval-request",
        CamundaBpmData.builder().set(ApprovalProcessBean.Variables.REQUEST, approvalRequest).build(),
        true
      );
      return self();
    }

    public GivenWhenStage process_is_started_with_$(@Format(args = "id='%s'") String approvalId) {
      processInstanceSupplier = approvalProcessBean.start(approvalId);

      return self();
    }

    public GivenWhenStage auto_approval_is_not_possible() {
      CamundaMockito.getJavaDelegateMock(Expressions.AUTO_APPROVE_REQUEST)
        .onExecutionThrowBpmnError(new BpmnError(Expressions.ERROR));

      return self();
    }

    public GivenWhenStage $_minutes_pass(Long minutes) {
      ClockUtil.offset(Duration.ofMinutes(minutes).toMillis());

      return self();
    }

    public GivenWhenStage task_is_completed_without_variables() {
      try {
        task_is_completed_with_variables(CamundaBpmData.builder().build());
      } catch (ProcessEngineException e) {
      }

      return self();
    }
  }

  public static class ThenStage extends ProcessStage<ThenStage, ApprovalProcessInstance> {

  }

}
