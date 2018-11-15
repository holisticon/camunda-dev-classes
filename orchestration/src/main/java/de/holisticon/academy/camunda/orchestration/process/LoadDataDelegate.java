package de.holisticon.academy.camunda.orchestration.process;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static de.holisticon.academy.camunda.orchestration.process.SimpleDataProcessingProcessBean.Expressions.LOAD_DATA_DELEGATE;

@Component(LOAD_DATA_DELEGATE)
public class LoadDataDelegate implements JavaDelegate {

  private static final Logger logger = LoggerFactory.getLogger(LoadDataDelegate.class);

  public void execute(DelegateExecution execution) {
    logger.info("Executed by process instance {}", execution.getProcessInstanceId());
  }
}
