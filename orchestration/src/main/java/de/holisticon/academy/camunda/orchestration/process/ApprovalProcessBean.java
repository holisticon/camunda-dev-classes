package de.holisticon.academy.camunda.orchestration.process;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.variable.VariableMap;

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

  public ProcessInstance start(String id) {
    return this.runtimeService.startProcessInstanceByKey("approval",
      org.camunda.bpm.engine.variable.Variables.putValue(Variables.APPROVAL_ID, id));
  }

  public void complete(String taskId, VariableMap variables) {
    this.taskService.complete(taskId, variables);
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
    final static String APPROVAL_ID = "approvalId";
    final static String AMOUNT = "amount";
    final static String REQUEST = "request";
    final static String APPROVAL_STRATEGY = "approvalStrategy";

    public final static String APPROVAL_DECISION = "approvalDecision";
    public final static String AMEND_ACTION = "amendAction";
  }

  enum Expressions {
    ;

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
