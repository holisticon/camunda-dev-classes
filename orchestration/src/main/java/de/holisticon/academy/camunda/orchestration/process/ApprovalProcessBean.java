package de.holisticon.academy.camunda.orchestration.process;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;

/**
 * Encapsulates all API methods around the process and Camunda
 */
public class ApprovalProcessBean {

  private final RuntimeService runtimeService;

  public ApprovalProcessBean(RuntimeService runtimeService) {
    this.runtimeService = runtimeService;
  }

  public ProcessInstance start(String id) {
    return this.runtimeService.startProcessInstanceByKey("approval",
      org.camunda.bpm.engine.variable.Variables.putValue(Variables.APPROVAL_ID, id));
  }

  enum Elements {
    ;

    final static String APPROVAL_REQUESTED = "approval_requested";
    final static String LOAD_APPROVAL_REQUEST = "service_load_approval_request";
    final static String DETERMINE_APPROVAL_STRATEGY = "service_determine_approval_strategy";
    final static String REQUEST_APPROVED = "request_approved";
    final static String REQUEST_REJECTED = "request_rejected";
    final static String USER_APPROVE_REQUEST = "user_approve_task";
    final static String USER_AMEND_REQUEST = "user_amend_task";
  }

  public enum Variables {
    ;

    // variables goes here
    final static String APPROVAL_ID = "approvalId";
    final static String AMOUNT = "amount";
    final static String APPROVAL_STRATEGY = "approvalStrategy";

    public final static String APPROVAL_DECISION = "approvalDecision";
    public final static String AMEND_ACTION = "amendAction";
  }

  enum Expressions {
    ;

    final static String LOAD_APPROVAL_REQUEST = "loadApprovalRequestDelegate";
    final static String DETERMINE_APPROVAL_STRATEGY = "determineApprovalStrategyDelegate";
    final static String AUTO_APPROVE_REQUEST = "autoApproveRequestDelegate";
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
