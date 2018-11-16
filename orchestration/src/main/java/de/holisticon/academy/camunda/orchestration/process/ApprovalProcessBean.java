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

  public ProcessInstance start() {
    return this.runtimeService.startProcessInstanceByKey("approval");
  }

  enum Elements {
    ;

    static String APPROVAL_REQUESTED = "approval_requested";
    static String LOAD_APPROVAL_REQUEST = "service_load_approval_request";
    static String COMPLETED = "processing_completed";
  }

  enum Variables {
    ;

    // variables goes here
    static String APPROVAL_ID = "approvalId";
  }
}
