package de.holisticon.academy.camunda.orchestration.process;

import com.tngtech.jgiven.annotation.As;
import com.tngtech.jgiven.annotation.BeforeStage;
import com.tngtech.jgiven.annotation.Format;
import com.tngtech.jgiven.annotation.Hidden;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.junit.ScenarioTest;
import de.holisticon.academy.camunda.orchestration.process.ApprovalProcessBean.Elements;
import de.holisticon.academy.camunda.orchestration.process.ApprovalProcessBean.Expressions;
import de.holisticon.academy.camunda.orchestration.service.ApprovalRequest;
import io.holunda.camunda.bpm.data.CamundaBpmData;
import io.holunda.camunda.bpm.extension.jgiven.ProcessStage;
import org.camunda.bpm.engine.ProcessEngineException;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.impl.util.ClockUtil;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.engine.test.mock.Mocks;
import org.camunda.bpm.extension.mockito.CamundaMockito;
import org.camunda.bpm.spring.boot.starter.test.helper.StandaloneInMemoryTestConfiguration;
import org.camunda.spin.plugin.impl.SpinProcessEnginePlugin;
import org.junit.Rule;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;

import static org.camunda.bpm.engine.test.assertions.bpmn.AbstractAssertions.init;

@Deployment(resources = {"approval.bpmn", "approvalStrategy.dmn"})
public class ApprovalScenarioTest extends ScenarioTest<ApprovalScenarioTest.GivenWhenStage, ApprovalScenarioTest.GivenWhenStage, ApprovalScenarioTest.ThenStage> {

  @Rule
  @ProvidedScenarioState
  public final ProcessEngineRule engine = createEngine();

  @Test
  @Hidden
  public void should_deploy() {
    then()
      .process_is_deployed("approval");
  }

  /**
   * TODO Please Implement this scenario!
   * cf. {@link ApprovalTest#shouldStartWaitInApprovalRequested()}
   */
  @Test
  @As("When a new Instance is started, it should wait in APPROVAL_REQUESTED")
  public void shouldStartWaitInApprovalRequested() {
    Assertions.fail("Please implement this scenario!");
  }

  @Test
  @As("Approval requests with an amount below the threshold are approved automatically")
  public void shouldStartAndLoadAndApprove() {
    given()
      .an_approval_request_$(
        new ApprovalRequest("id", "subj", "kermit", new BigDecimal("7.81"))
      )
    ;
    when()
      .process_is_started_with_$("id")
      .and()
      .job_is_executed()
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
  public void shouldStartAndLoadAndApproveAndFail() {
    given()
      .an_approval_request_$(
        new ApprovalRequest("id", "subj", "kermit", new BigDecimal("7.81"))
      )
      .and()
      .auto_approval_is_not_possible()
    ;
    when()
      .process_is_started_with_$("id")
      .and()
      .job_is_executed()
    ;
    then()
      .process_waits_in(Elements.USER_APPROVE_REQUEST)
      .and()
      .process_has_passed(
        Elements.LOAD_APPROVAL_REQUEST, Elements.AUTO_APPROVE_REQUEST
      )
    ;
  }

  /**
   * TODO Please Implement this scenario!
   * cf. {@link ApprovalTest#shouldStartAndLoadAndManual()}
   */
  @Test
  @As("Approval requests with an amount above the threshold have to be approved manually")
  public void shouldStartAndLoadAndManual() {
    Assertions.fail("Please implement this scenario!");
  }

  @Test
  @As("Approval requests can be approved manually")
  public void shouldStartAndLoadAndManualAndApprove() {
    given()
      .an_approval_request_$(
        new ApprovalRequest("id", "subj", "kermit", new BigDecimal("117.81"))
      )
    ;
    when()
      .process_is_started_with_$("id")
      .and()
      .job_is_executed()
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
  public void shouldStartAndLoadAndManualAndReject() {
    given()
      .an_approval_request_$(
        new ApprovalRequest("id", "subj", "kermit", new BigDecimal("117.81"))
      )
    ;
    when()
      .process_is_started_with_$("id")
      .and()
      .job_is_executed()
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
  public void shouldStartAndLoadAndManualAndReturnedAndCancel() {
    given()
      .an_approval_request_$(
        new ApprovalRequest("id", "subj", "kermit", new BigDecimal("117.81"))
      )
    ;
    when()
      .process_is_started_with_$("id")
      .and()
      .job_is_executed()
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
  public void shouldStartAndLoadAndManualAndReturnedAndCancelByTimeout() {
    given()
      .an_approval_request_$(
        new ApprovalRequest("id", "subj", "kermit", new BigDecimal("117.81"))
      )
    ;
    when()
      .process_is_started_with_$("id")
      .and()
      .job_is_executed()
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

  /**
   * TODO Please Implement this scenario!
   * cf. {@link ApprovalTest#shouldStartAndLoadAndManualAndReturnedAndResubmit()}
   */
  @Test
  @As("Approval requests, which are returned during manual approval, can be resubmitted by the requester")
  public void shouldStartAndLoadAndManualAndReturnedAndResubmit() {
    Assertions.fail("Please implement this scenario!");
  }

  @Test
  @As("Approval requests cannot be approved manually without an approval decision")
  public void cannotCompleteManualApprovalTaskWithoutApprovalDecision() {
    given()
      .an_approval_request_$(
        new ApprovalRequest("id", "subj", "kermit", new BigDecimal("117.81"))
      )
    ;
    when()
      .process_is_started_with_$("id")
      .and()
      .job_is_executed()
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

  private static ProcessEngineRule createEngine() {
    StandaloneInMemoryTestConfiguration config = new StandaloneInMemoryTestConfiguration();
    config.getProcessEnginePlugins().add(new SpinProcessEnginePlugin());
    return config.rule();
  }

  public static class GivenWhenStage extends ProcessStage<GivenWhenStage, ApprovalProcessInstance> {

    @ProvidedScenarioState
    private ApprovalProcessBean approvalProcessBean;

    @BeforeStage
    public void before() {
      init(camunda.getProcessEngine());

      CamundaMockito.registerJavaDelegateMock(Expressions.DETERMINE_APPROVAL_STRATEGY);
      CamundaMockito.registerJavaDelegateMock(Expressions.LOAD_APPROVAL_REQUEST);
      CamundaMockito.registerJavaDelegateMock(Expressions.AUTO_APPROVE_REQUEST);

      Mocks.register(Expressions.AUDIT, new AuditListener());

      CamundaMockito.registerInstance(VariableGuardConfiguration.MANUAL_APPROVAL_GUARD, new VariableGuardConfiguration().manualApprovalGuard());

      approvalProcessBean = new ApprovalProcessBean(camunda.getRuntimeService(), camunda.getTaskService());
    }

    public GivenWhenStage an_approval_request_$(ApprovalRequest approvalRequest) {
      CamundaMockito.getJavaDelegateMock(Expressions.LOAD_APPROVAL_REQUEST)
        .onExecutionSetVariables(
          CamundaBpmData.builder()
            .set(ApprovalProcessBean.Variables.REQUEST, approvalRequest)
            .build()
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
      ClockUtil.offset(minutes * 60 * 1000);

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
