package de.holisticon.academy.camunda.choreography;

import de.holisticon.academy.camunda.choreography.listener.AuditListener;
import org.camunda.bpm.engine.impl.util.ClockUtil;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.engine.test.mock.Mocks;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.extension.mockito.CamundaMockito;
import org.camunda.bpm.spring.boot.starter.test.helper.StandaloneInMemoryTestConfiguration;
import org.camunda.spin.plugin.impl.SpinProcessEnginePlugin;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Calendar;
import java.util.UUID;

import static org.camunda.bpm.engine.test.assertions.bpmn.AbstractAssertions.init;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareAssertions.assertThat;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.execute;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.job;


@Deployment(resources = {"pizzaOrder.bpmn"})
public class PizzaOrderProcessTest {

  private String orderId;
  private VariableMap pizzaOrder;
  private AuditListener auditListener;

  @Rule
  public final ProcessEngineRule engine = createEngine();

  @Before
  public void before() {
    init(engine.getProcessEngine());
    CamundaMockito.registerJavaDelegateMock(PizzaOrderProcess.Expressions.PLACE_ORDER_DELEGATE);
    this.orderId = "Pizza-Order-" + UUID.randomUUID().toString();
    this.pizzaOrder = PizzaOrderProcess.createOrder();
    this.auditListener = new AuditListener();
    Mocks.register(PizzaOrderProcess.Expressions.AUDIT, this.auditListener);
  }


  @Test
  public void shouldDeploy() {
    // no asserts, deployment would throw exception and fail the test on errors
  }

  @Test
  public void shouldStartPizzaOrderAndWait() {
    final ProcessInstance orderInstance = engine.getRuntimeService().startProcessInstanceByKey(PizzaOrderProcess.KEY, orderId, pizzaOrder);
    assertThat(orderInstance).isStarted();
    execute(job()); // async on start
    assertThat(orderInstance).isWaitingAt(PizzaOrderProcess.Elements.DATA_EXCLUSIVE);
  }

  @Test
  public void shouldStartPizzaOrderAndTimeout() {

    final ProcessInstance orderInstance = engine.getRuntimeService().startProcessInstanceByKey(PizzaOrderProcess.KEY, orderId, pizzaOrder);
    assertThat(orderInstance).isStarted();
    execute(job()); // async on start
    assertThat(orderInstance).isWaitingAt(PizzaOrderProcess.Elements.DATA_EXCLUSIVE);
    assertThat(orderInstance).variables().containsEntry(PizzaOrderProcess.Variables.DELIVERED, false);

    // timeout the timer
    final Calendar now = Calendar.getInstance();
    now.setTime(ClockUtil.getCurrentTime());
    now.add(Calendar.HOUR, 2);
    ClockUtil.setCurrentTime(now.getTime());
    execute(job());

    // assert the right end
    assertThat(orderInstance).isEnded();
    assertThat(orderInstance).hasPassedInOrder(
      PizzaOrderProcess.Elements.PLACE_ORDER,
      PizzaOrderProcess.Elements.TIMER,
      PizzaOrderProcess.Elements.END_NOT_RECEIVED
    );

    assertThat(auditListener.getLastMessage()).isEqualTo("timeout");
  }

  @Test
  public void shouldStartPizzaOrderAndReceivePizza() {

    final ProcessInstance orderInstance = engine.getRuntimeService().startProcessInstanceByKey(PizzaOrderProcess.KEY, orderId, pizzaOrder);
    assertThat(orderInstance).isStarted();
    execute(job()); // async on start
    assertThat(orderInstance).isWaitingAt(PizzaOrderProcess.Elements.DATA_EXCLUSIVE);
    assertThat(orderInstance).variables().containsEntry(PizzaOrderProcess.Variables.DELIVERED, false);

    // correlate message
    engine.getRuntimeService().correlateMessage(
      PizzaOrderProcess.Expressions.MESSAGE_PIZZA_RECEIVED,
      orderId,
      Variables.putValue(
        PizzaOrderProcess.Variables.DELIVERED, true
      )
    );

    // assert the correct end
    assertThat(orderInstance).isEnded();
    // correct activities
    assertThat(orderInstance).hasPassedInOrder(
      PizzaOrderProcess.Elements.PLACE_ORDER,
      PizzaOrderProcess.Elements.MESSAGE_RECEIVE,
      PizzaOrderProcess.Elements.END_RECEIVED
    );
    // correlated variable
    assertThat(orderInstance).variables().containsEntry(PizzaOrderProcess.Variables.DELIVERED, true);
    assertThat(auditListener.getLastMessage()).isEqualTo("order delivered: true");
  }


  private static ProcessEngineRule createEngine() {
    StandaloneInMemoryTestConfiguration config = new StandaloneInMemoryTestConfiguration();
    config.getProcessEnginePlugins().add(new SpinProcessEnginePlugin());
    return config.rule();
  }

}
