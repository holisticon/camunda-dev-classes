package de.holisticon.academy.camunda.choreography.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component(value = "placeOrderDelegate")
public class PlaceOrderDelegate implements JavaDelegate {

  @Override
  public void execute(DelegateExecution delegateExecution) {
    // TODO implement message correlation
  }
}
