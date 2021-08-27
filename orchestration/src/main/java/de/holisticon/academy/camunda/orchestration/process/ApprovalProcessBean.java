package de.holisticon.academy.camunda.orchestration.process;

import org.camunda.bpm.engine.RuntimeService;

/**
 * Encapsulates all API methods around the process and Camunda
 */
public class ApprovalProcessBean {

  private final RuntimeService runtimeService;

  public ApprovalProcessBean(RuntimeService runtimeService) {
    this.runtimeService = runtimeService;
  }

  public ApprovalProcessInstance start() {
    return ApprovalProcessInstance.wrap(this.runtimeService.startProcessInstanceByKey("approval"));
  }

  enum Elements {
    ;

    final static String APPROVAL_REQUESTED = "approval_requested";
    final static String LOAD_APPROVAL_REQUEST = "service_load_approval_request";
    final static String COMPLETED = "processing_completed";
  }

  enum Variables {
    ;

    // variables goes here
    final static String APPROVAL_ID = "approvalId";
  }

  enum Expressions {
    ;

    final static String LOAD_APPROVAL_REQUEST = "loadApprovalRequestDelegate";
  }
}
