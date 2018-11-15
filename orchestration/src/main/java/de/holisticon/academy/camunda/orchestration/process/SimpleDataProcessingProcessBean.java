package de.holisticon.academy.camunda.orchestration.process;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;

/**
 * Encapsulates all API methods around the process and Camunda
 */
public class SimpleDataProcessingProcessBean {

  private final RuntimeService runtimeService;

  public SimpleDataProcessingProcessBean(RuntimeService runtimeService) {
    this.runtimeService = runtimeService;
  }

  public ProcessInstance start() {
    return this.runtimeService.startProcessInstanceByKey("simple_data_processing");
  }

  enum Elements {
    ;

    static String STARTED = "processing_started";
    static String LOAD_DATA = "service_load_data";
    static String COMPLETED = "processing_completed";
  }

  enum Variables {
    ;

    // variables goes here
    static String APPROVAL_ID = "approvalId";
  }
}
