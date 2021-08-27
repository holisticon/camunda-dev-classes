package de.holisticon.academy.camunda.choreography;

import io.holunda.camunda.bpm.data.CamundaBpmData;
import io.holunda.camunda.bpm.data.factory.VariableFactory;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.variable.VariableMap;

import java.util.function.Supplier;

import static io.holunda.camunda.bpm.data.CamundaBpmData.booleanVariable;
import static io.holunda.camunda.bpm.data.CamundaBpmData.stringVariable;

public class PizzaOrderProcess {
  public static final String KEY = "pizza_order";

  enum Elements {
    ;
    public static final String FOOD_NEEDED = "food_needed";
    public static final String DATA_EXCLUSIVE = "data_exclusive";
    public static final String PLACE_ORDER = "task_place_order";
    public static final String TIMER = "timer";
    public static final String END_NOT_RECEIVED = "pizza_not_received";
    public static final String MESSAGE_RECEIVE = "message_event_pizza_received";
    public static final String END_RECEIVED = "pizza_received";
  }

  public enum Expressions {
    ;
    public static final String PLACE_ORDER_DELEGATE = "placeOrderDelegate";
    public static final String MESSAGE_PIZZA_RECEIVED = "pizzaReceived";
    public static final String AUDIT = "audit";
  }

  public enum Variables {
    ;
    public static final VariableFactory<String> SIZE = stringVariable("size");
    public static final VariableFactory<String> TYPE = stringVariable("type");
    public static final VariableFactory<Boolean> DELIVERED = booleanVariable("delivered");
  }

  public static VariableMap createOrder() {
    return CamundaBpmData.builder()
      .set(Variables.TYPE, "Funghi")
      .set(Variables.SIZE, "medium")
      .set(Variables.DELIVERED, false)
      .build();
  }

  public static class PizzaOrderProcessInstance implements Supplier<ProcessInstance> {

    static PizzaOrderProcessInstance wrap(ProcessInstance processInstance) {
      return new PizzaOrderProcessInstance(processInstance);
    }

    private final ProcessInstance delegate;

    private PizzaOrderProcessInstance(ProcessInstance processInstance) {
      this.delegate = processInstance;
    }

    @Override
    public ProcessInstance get() {
      return delegate;
    }
  }
}
