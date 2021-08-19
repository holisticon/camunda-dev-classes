package de.holisticon.academy.camunda.orchestration.process;

import de.holisticon.academy.camunda.orchestration.service.ApprovalRequest;
import io.holunda.camunda.bpm.data.CamundaBpmData;
import io.holunda.camunda.bpm.data.factory.VariableFactory;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.variable.VariableMap;

import java.util.List;
import java.util.Optional;

import static io.holunda.camunda.bpm.data.CamundaBpmData.*;
import static io.holunda.camunda.bpm.data.CamundaBpmData.stringVariable;

/**
 * Encapsulates all API methods around the process and Camunda
 */
public class ApprovalProcessBean {

  private final RuntimeService runtimeService;
  private final TaskService taskService;

  public ApprovalProcessBean(RuntimeService runtimeService, TaskService taskService) {
    this.runtimeService = runtimeService;
    this.taskService = taskService;
  }

  public ApprovalProcessInstance start(String id) {
    return ApprovalProcessInstance.wrap(
      this.runtimeService.startProcessInstanceByKey(
        "approval",
        CamundaBpmData.builder()
          .set(Variables.APPROVAL_ID, id)
          .build())
    );
  }

  public void complete(String taskId, VariableMap variables) {
    this.taskService.complete(taskId, variables);
  }

  Optional<Task> task(String taskDefinitionKey, String taskId) {
    return Optional.ofNullable(taskService
      .createTaskQuery()
      .taskDefinitionKey(taskDefinitionKey)
      .taskId(taskId)
      .singleResult());
  }

  public Optional<Task> approveTask(String taskId) {
    return task(Elements.USER_APPROVE_REQUEST, taskId);
  }

  public Optional<Task> amendTask(String taskId) {
    return task(Elements.USER_AMEND_REQUEST, taskId);
  }

  public List<Task> getTasks() {
    return taskService
      .createTaskQuery()
      .initializeFormKeys()
      .list();
  }

  public boolean isValidApprovalDecision(String approvalDecision) {
    return approvalDecision.equals(Values.APPROVAL_DECISION_REJECTED)
      || approvalDecision.equals(Values.APPROVAL_DECISION_APPROVED)
      || approvalDecision.equals(Values.APPROVAL_DECISION_RETURNED);
  }

  public boolean isValidAmendAction(String amendAction) {
    return amendAction.equals(ApprovalProcessBean.Values.AMEND_ACTION_RESUBMITTED)
      || amendAction.equals(ApprovalProcessBean.Values.AMEND_ACTION_CANCELLED);
  }

  enum Elements {
    ;

    final static String APPROVAL_REQUESTED = "approval_requested";
    final static String LOAD_APPROVAL_REQUEST = "service_load_approval_request";
    final static String DETERMINE_APPROVAL_STRATEGY = "service_determine_approval_strategy";
    final static String REQUEST_APPROVED = "request_approved";
    final static String REQUEST_REJECTED = "request_rejected";
    final static String REQUEST_CANCELLED = "request_cancelled";
    final static String USER_APPROVE_REQUEST = "task_approve_request";
    final static String USER_AMEND_REQUEST = "task_amend_request";
  }

  public enum Variables {
    ;

    // variables goes here
    final static VariableFactory<String> APPROVAL_ID = stringVariable("approvalId");
    final static VariableFactory<String> AMOUNT = stringVariable("amount");
    final static VariableFactory<ApprovalRequest> REQUEST = customVariable("request", ApprovalRequest.class);
    final static VariableFactory<String> APPROVAL_STRATEGY = stringVariable("approvalStrategy");

    public final static VariableFactory<String> APPROVAL_DECISION = stringVariable("approvalDecision");
    public final static VariableFactory<String> AMEND_ACTION = stringVariable("amendAction");
  }

  enum Expressions {
    ;

    final static String ERROR = "automaticApprovalError";
    final static String AUDIT = "audit";
    final static String LOAD_APPROVAL_REQUEST = "loadApprovalRequestDelegate";
    final static String DETERMINE_APPROVAL_STRATEGY = "determineApprovalStrategyDelegate";
    final static String AUTO_APPROVE_REQUEST = "automaticApproveRequestDelegate";
  }

  public enum Values {
    ;
    final static String APPROVAL_STRATEGY_AUTOMATIC = "Automatic";
    final static String APPROVAL_STRATEGY_MANUAL = "Manual";

    public final static String APPROVAL_DECISION_APPROVED = "APPROVED";
    public final static String APPROVAL_DECISION_REJECTED = "REJECTED";
    public final static String APPROVAL_DECISION_RETURNED = "RETURNED";

    public final static String AMEND_ACTION_CANCELLED = "CANCELLED";
    public final static String AMEND_ACTION_RESUBMITTED = "RESUBMITTED";

  }
}
