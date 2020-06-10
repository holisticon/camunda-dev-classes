package de.holisticon.academy.camunda.choreography.delegate;

import de.holisticon.academy.camunda.choreography.PizzaDeliveryProcess;
import de.holisticon.academy.camunda.choreography.PizzaOrderProcess;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.variable.Variables;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component(value = "placeOrderDelegate")
public class PlaceOrderDelegate implements JavaDelegate {

  private final RuntimeService runtimeService;

  public PlaceOrderDelegate(RuntimeService runtimeService) {
    this.runtimeService = runtimeService;
  }

  @Override
  public void execute(DelegateExecution delegateExecution) {

    this.runtimeService.correlateMessage(PizzaDeliveryProcess.Expressions.MESSAGE_PLACE_ORDER,
      delegateExecution.getProcessBusinessKey(),
      Variables.putValue(PizzaOrderProcess.Variables.SIZE, delegateExecution.getVariable(PizzaOrderProcess.Variables.SIZE))
        .putValue(PizzaOrderProcess.Variables.TYPE, delegateExecution.getVariable(PizzaOrderProcess.Variables.TYPE))
        .putValue(PizzaOrderProcess.Variables.DELIVERED, delegateExecution.getVariable(PizzaOrderProcess.Variables.DELIVERED))
    );

  }
}
