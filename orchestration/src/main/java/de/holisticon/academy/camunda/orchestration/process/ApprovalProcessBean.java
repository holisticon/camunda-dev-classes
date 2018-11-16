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
  }

  enum Variables {
    ;

    // variables goes here
    final static String APPROVAL_ID = "approvalId";
    final static String AMOUNT = "amount";
    final static String APPROVAL_STRATEGY = "approvalStrategy";
  }

  enum Expressions {
    ;

    final static String LOAD_APPROVAL_REQUEST = "loadApprovalRequestDelegate";
    final static String DETERMINE_APPROVAL_STRATEGY ="determineApprovalStrategyDelegate";
  }
}
