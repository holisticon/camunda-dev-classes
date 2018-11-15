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

    final static String STARTED = "processing_started";
    final static String LOAD_DATA = "service_load_data";
    final static String COMPLETED = "processing_completed";
  }

  enum Variables {
    ;

    final static String APPROVAL_ID = "approvalId";
    final static String AMOUNT = "amount";
  }

  enum Expressions {
    ;
    final static String LOAD_DATA_DELEGATE = "loadDataDelegate";
  }
}
