package de.holisticon.academy.camunda.choreography.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component(value = "deliverPizzaDelegate")
public class DeliverPizzaDelegate implements JavaDelegate {

  @Override
  public void execute(DelegateExecution delegateExecution) {
    // TODO implement message correlation
  }
}
