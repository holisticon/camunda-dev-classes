package de.holisticon.academy.camunda.orchestration.process;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LoggingListener implements ExecutionListener {

  private final Logger log = LoggerFactory.getLogger(LoggingListener.class);

  @Override
  public void notify(DelegateExecution execution) {
    log.error("Process ended with {}", execution.getCurrentActivityId());
  }
}


