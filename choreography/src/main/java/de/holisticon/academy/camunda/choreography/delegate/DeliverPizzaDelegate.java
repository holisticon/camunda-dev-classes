package de.holisticon.academy.camunda.choreography.delegate;

import de.holisticon.academy.camunda.choreography.PizzaOrderProcess;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component(value = "deliverPizzaDelegate")
public class DeliverPizzaDelegate implements JavaDelegate {

  private final RuntimeService runtimeService;

  public DeliverPizzaDelegate(RuntimeService runtimeService) {
    this.runtimeService = runtimeService;
  }

  @Override
  public void execute(DelegateExecution delegateExecution) {
    Optional
      .ofNullable(
        this.runtimeService.createExecutionQuery()
          .processInstanceBusinessKey(delegateExecution.getProcessBusinessKey())
          .messageEventSubscriptionName(PizzaOrderProcess.Expressions.MESSAGE_PIZZA_RECEIVED)
          .singleResult()
      ).ifPresent(e ->
      this.runtimeService.createMessageCorrelation(PizzaOrderProcess.Expressions.MESSAGE_PIZZA_RECEIVED)
        .processInstanceId(e.getProcessInstanceId())
        .setVariable(PizzaOrderProcess.Variables.DELIVERED, true)
        .correlate()
    );
  }
}
