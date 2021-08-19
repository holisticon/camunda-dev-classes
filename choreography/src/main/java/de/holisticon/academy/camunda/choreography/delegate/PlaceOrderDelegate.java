package de.holisticon.academy.camunda.choreography.delegate;

import de.holisticon.academy.camunda.choreography.PizzaDeliveryProcess;
import de.holisticon.academy.camunda.choreography.PizzaOrderProcess;
import io.holunda.camunda.bpm.data.CamundaBpmData;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component(value = "placeOrderDelegate")
public class PlaceOrderDelegate implements JavaDelegate {

  private final RuntimeService runtimeService;

  public PlaceOrderDelegate(RuntimeService runtimeService) {
    this.runtimeService = runtimeService;
  }

  @Override
  public void execute(DelegateExecution delegateExecution) {
    final var reader = CamundaBpmData.reader(delegateExecution);

    this.runtimeService.correlateMessage(PizzaDeliveryProcess.Expressions.MESSAGE_PLACE_ORDER,
      delegateExecution.getProcessBusinessKey(),
      CamundaBpmData.builder()
        .set(PizzaOrderProcess.Variables.SIZE, reader.get(PizzaOrderProcess.Variables.SIZE))
        .set(PizzaOrderProcess.Variables.TYPE, reader.get(PizzaOrderProcess.Variables.TYPE))
        .set(PizzaOrderProcess.Variables.DELIVERED, reader.get(PizzaOrderProcess.Variables.DELIVERED))
        .build()
    );

  }
}
