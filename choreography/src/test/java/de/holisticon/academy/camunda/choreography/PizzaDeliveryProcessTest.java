package de.holisticon.academy.camunda.choreography;

import io.holunda.camunda.bpm.data.CamundaBpmData;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.extension.mockito.CamundaMockito;
import org.camunda.bpm.spring.boot.starter.test.helper.StandaloneInMemoryTestConfiguration;
import org.camunda.spin.plugin.impl.SpinProcessEnginePlugin;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.UUID;

import static org.camunda.bpm.engine.test.assertions.bpmn.AbstractAssertions.init;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.complete;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.execute;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.externalTask;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.job;

@Deployment(resources = {"pizzaOrder.bpmn"})
public class PizzaDeliveryProcessTest {

  @Rule
  public final ProcessEngineRule engine = createEngine();
  private String orderId;
  private VariableMap payload;

  @Before
  public void before() {
    init(engine.getProcessEngine());
    CamundaMockito.registerJavaDelegateMock(PizzaDeliveryProcess.Expressions.MAKE_PIZZA_DELEGATE);
    CamundaMockito.registerJavaDelegateMock(PizzaDeliveryProcess.Expressions.DELIVER_PIZZA_DELEGATE)
      .onExecutionSetVariables(CamundaBpmData.builder().set(PizzaOrderProcess.Variables.DELIVERED, true).build());

    this.orderId = "Pizza-Order-" + UUID.randomUUID();
    this.payload = PizzaOrderProcess.createOrder();

  }

  @Test
  public void shouldDeploy() {
    //
  }

  @Test
  public void shouldStartEnd() {

    this.engine.getRuntimeService().correlateMessage(
      PizzaDeliveryProcess.Expressions.MESSAGE_PLACE_ORDER,
      this.orderId,
      this.payload
    );

    ProcessInstance deliveryInstance = this.engine.getRuntimeService().createProcessInstanceQuery().processInstanceBusinessKey(this.orderId).singleResult();
    assertThat(deliveryInstance).isNotNull();

    // async on start
    assertThat(deliveryInstance).isWaitingAt(PizzaDeliveryProcess.Elements.ORDER_PLACED);
    execute(job());

    // process is waiting for the external task to be completed
    assertThat(deliveryInstance).isWaitingAt(PizzaDeliveryProcess.Elements.PACK_PIZZA);
    complete(externalTask());

    // The external task ist asyncAfter, so we need to execute that job
    assertThat(deliveryInstance).isWaitingAt(PizzaDeliveryProcess.Elements.PACK_PIZZA);
    execute(job());

    assertThat(deliveryInstance).isEnded();

    assertThat(deliveryInstance).variables().containsEntry(PizzaOrderProcess.Variables.DELIVERED.getName(), true);
  }

  private static ProcessEngineRule createEngine() {
    StandaloneInMemoryTestConfiguration config = new StandaloneInMemoryTestConfiguration();
    config.getProcessEnginePlugins().add(new SpinProcessEnginePlugin());
    return config.rule();
  }

}
