package de.holisticon.academy.camunda.choreography;

import com.tngtech.jgiven.annotation.BeforeStage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.Hidden;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.junit.ScenarioTest;
import io.holunda.camunda.bpm.data.CamundaBpmData;
import io.holunda.camunda.bpm.extension.jgiven.ProcessStage;
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
public class PizzaDeliveryProcessTest extends ScenarioTest<PizzaDeliveryProcessTest.GivenWhenStage, PizzaDeliveryProcessTest.GivenWhenStage, PizzaDeliveryProcessTest.ThenStage> {

  @Rule
  @ProvidedScenarioState
  public final ProcessEngineRule camunda = createEngine();

  @Test
  @Hidden
  public void shouldDeploy() {
    then()
      .process_is_deployed("pizza_delivery");
  }

  @Test
  public void shouldStartEnd() {
    when()
      .a_pizza_is_ordered()
    ;
    then()
      .process_waits_in(PizzaDeliveryProcess.Elements.ORDER_PLACED)
    ;
    when()
      .job_is_executed()
    ;
    then()
      .process_waits_in(PizzaDeliveryProcess.Elements.PACK_PIZZA)
    ;
    when()
      .external_task_is_completed(
        PizzaDeliveryProcess.ExternalTasks.PackPizza.TOPIC,
        CamundaBpmData.builder()
          .set(PizzaDeliveryProcess.ExternalTasks.PackPizza.Produces.PACKED, true)
          .build(),
        true
      )
    ;
    then()
      .process_is_finished()
      .and()
      .the_pizza_was_delivered()
    ;
  }

  private static ProcessEngineRule createEngine() {
    StandaloneInMemoryTestConfiguration config = new StandaloneInMemoryTestConfiguration();
    config.getProcessEnginePlugins().add(new SpinProcessEnginePlugin());
    return config.rule();
  }

  public static class GivenWhenStage extends ProcessStage<GivenWhenStage, PizzaDeliveryProcess.PizzaDeliveryProcessInstance> {

    @ExpectedScenarioState
    private PizzaDeliveryProcess pizzaDeliveryProcess;

    @ProvidedScenarioState
    private String orderId;

    @ProvidedScenarioState
    private VariableMap payload;

    @BeforeStage
    public void before() {
      init(camunda.getProcessEngine());

      CamundaMockito.registerJavaDelegateMock(PizzaDeliveryProcess.Expressions.MAKE_PIZZA_DELEGATE);
      CamundaMockito.registerJavaDelegateMock(PizzaDeliveryProcess.Expressions.DELIVER_PIZZA_DELEGATE)
        .onExecutionSetVariables(CamundaBpmData.builder().set(PizzaOrderProcess.Variables.DELIVERED, true).build());

      this.orderId = "Pizza-Order-" + UUID.randomUUID();
      this.payload = PizzaOrderProcess.createOrder();

      this.pizzaDeliveryProcess = new PizzaDeliveryProcess(camunda.getRuntimeService());
    }

    public GivenWhenStage a_pizza_is_ordered() {
      this.camunda.getRuntimeService().correlateMessage(
        PizzaDeliveryProcess.Expressions.MESSAGE_PLACE_ORDER,
        this.orderId,
        this.payload
      );

      processInstanceSupplier = pizzaDeliveryProcess.getByOrderId(this.orderId);

      return self();
    }
  }

  public static class ThenStage extends ProcessStage<ThenStage, PizzaDeliveryProcess.PizzaDeliveryProcessInstance> {
    public ThenStage the_pizza_was_delivered() {
      assertThat(processInstanceSupplier.get()).variables().containsEntry(PizzaOrderProcess.Variables.DELIVERED.getName(), true);

      return self();
    }
  }

}
