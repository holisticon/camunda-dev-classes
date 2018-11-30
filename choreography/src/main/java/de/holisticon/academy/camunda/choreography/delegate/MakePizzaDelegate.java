package de.holisticon.academy.camunda.choreography.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component(value = "makePizzaDelegate")
public class MakePizzaDelegate implements JavaDelegate {

  private Logger LOGGER = LoggerFactory.getLogger(MakePizzaDelegate.class);

  @Override
  public void execute(DelegateExecution delegateExecution) {
    LOGGER.info("Pizza is being made!");
  }
}
