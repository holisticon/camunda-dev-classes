package de.holisticon.academy.camunda.choreography;

import com.tngtech.jgiven.annotation.BeforeScenario;
import com.tngtech.jgiven.annotation.BeforeStage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.Hidden;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.annotation.Quoted;
import com.tngtech.jgiven.junit.ScenarioTest;
import de.holisticon.academy.camunda.choreography.listener.AuditListener;
import io.holunda.camunda.bpm.data.CamundaBpmData;
import io.holunda.camunda.bpm.extension.jgiven.ProcessStage;
import org.assertj.core.api.Assertions;
import org.camunda.bpm.engine.impl.util.ClockUtil;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.engine.test.mock.Mocks;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.extension.mockito.CamundaMockito;
import org.camunda.bpm.spring.boot.starter.test.helper.StandaloneInMemoryTestConfiguration;
import org.camunda.spin.plugin.impl.SpinProcessEnginePlugin;
import org.junit.Rule;
import org.junit.Test;

import java.time.Duration;
import java.util.UUID;

import static org.camunda.bpm.engine.test.assertions.bpmn.AbstractAssertions.init;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;


@Deployment(resources = {"pizzaOrder.bpmn"})
public class PizzaOrderProcessTest extends ScenarioTest<PizzaOrderProcessTest.GivenWhenStage, PizzaOrderProcessTest.GivenWhenStage, PizzaOrderProcessTest.ThenStage> {

  @Rule
  @ProvidedScenarioState
  public final ProcessEngineRule camunda = createEngine();

  @Test
  @Hidden
  public void shouldDeploy() {
    then()
      .process_is_deployed(PizzaOrderProcess.KEY);
  }

  @Test
  public void shouldStartPizzaOrderAndWait() {
    given()
      .food_is_needed()
      .and()
      .process_waits_in(PizzaOrderProcess.Elements.FOOD_NEEDED)
    ;
    when()
      .job_is_executed()
    ;
    then()
      .process_waits_in(PizzaOrderProcess.Elements.DATA_EXCLUSIVE)
    ;
  }

  @Test
  public void shouldStartPizzaOrderAndTimeout() {
    given()
      .food_is_needed()
      .and()
      .process_waits_in(PizzaOrderProcess.Elements.FOOD_NEEDED)
    ;
    when()
      .job_is_executed()
    ;
    then()
      .process_waits_in(PizzaOrderProcess.Elements.DATA_EXCLUSIVE)
      .and()
      .the_pizza_is_not_yet_delivered()
    ;
    when()
      .$_hours_pass(2L)
      .and()
      .job_is_executed()
    ;
    then()
      .process_is_finished()
      .and()
      .process_has_passed(
        PizzaOrderProcess.Elements.PLACE_ORDER,
        PizzaOrderProcess.Elements.TIMER,
        PizzaOrderProcess.Elements.END_NOT_RECEIVED
      )
      .and()
      .$_is_written_to_audit_log("timeout")
    ;
  }

  @Test
  public void shouldStartPizzaOrderAndReceivePizza() {
    given()
      .food_is_needed()
      .and()
      .process_waits_in(PizzaOrderProcess.Elements.FOOD_NEEDED)
    ;
    when()
      .job_is_executed()
    ;
    then()
      .process_waits_in(PizzaOrderProcess.Elements.DATA_EXCLUSIVE)
      .and()
      .the_pizza_is_not_yet_delivered()
    ;
    when()
      .the_pizza_is_delivered()
    ;
    then()
      .process_is_finished()
      .and()
      .process_has_passed(
        PizzaOrderProcess.Elements.PLACE_ORDER,
        PizzaOrderProcess.Elements.MESSAGE_RECEIVE,
        PizzaOrderProcess.Elements.END_RECEIVED
      )
      .and()
      .the_pizza_was_delivered()
      .and()
      .$_is_written_to_audit_log("order delivered: true")
      ;
  }


  private static ProcessEngineRule createEngine() {
    StandaloneInMemoryTestConfiguration config = new StandaloneInMemoryTestConfiguration();
    config.getProcessEnginePlugins().add(new SpinProcessEnginePlugin());
    return config.rule();
  }

  public static class GivenWhenStage extends ProcessStage<GivenWhenStage, PizzaOrderProcess.PizzaOrderProcessInstance> {
    @ProvidedScenarioState
    private String orderId;

    @ProvidedScenarioState
    private VariableMap pizzaOrder;

    @ProvidedScenarioState
    private AuditListener auditListener;

    @BeforeStage
    public void before() {
      init(camunda.getProcessEngine());
      CamundaMockito.registerJavaDelegateMock(PizzaOrderProcess.Expressions.PLACE_ORDER_DELEGATE);
      this.orderId = "Pizza-Order-" + UUID.randomUUID();
      this.pizzaOrder = PizzaOrderProcess.createOrder();
      this.auditListener = new AuditListener();
      Mocks.register(PizzaOrderProcess.Expressions.AUDIT, this.auditListener);
    }

    public GivenWhenStage food_is_needed() {
      processInstanceSupplier = PizzaOrderProcess.PizzaOrderProcessInstance.wrap(camunda.getRuntimeService().startProcessInstanceByKey(PizzaOrderProcess.KEY, orderId, pizzaOrder));
      return self();
    }

    public GivenWhenStage $_hours_pass(Long hours) {
      ClockUtil.offset(Duration.ofHours(hours).toMillis());

      return self();
    }


    public GivenWhenStage the_pizza_is_delivered() {
      camunda.getRuntimeService().correlateMessage(
        PizzaOrderProcess.Expressions.MESSAGE_PIZZA_RECEIVED,
        orderId,
        CamundaBpmData.builder()
          .set(PizzaOrderProcess.Variables.DELIVERED, true)
          .build()
      );

      return self();
    }
  }

  public static class ThenStage extends ProcessStage<ThenStage, PizzaOrderProcess.PizzaOrderProcessInstance> {
    @ExpectedScenarioState
    private AuditListener auditListener;

    public ThenStage $_is_written_to_audit_log(@Quoted String message) {
      Assertions.assertThat(auditListener.getLastMessage()).isEqualTo(message);
      return self();
    }

    public ThenStage the_pizza_is_not_yet_delivered() {
      assertThat(processInstanceSupplier.get()).variables().containsEntry(PizzaOrderProcess.Variables.DELIVERED.getName(), false);

      return self();
    }

    public ThenStage the_pizza_was_delivered() {
      assertThat(processInstanceSupplier.get()).variables().containsEntry(PizzaOrderProcess.Variables.DELIVERED.getName(), true);

      return self();
    }
  }

}
