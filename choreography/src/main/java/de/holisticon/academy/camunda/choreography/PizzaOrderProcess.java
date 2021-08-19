package de.holisticon.academy.camunda.choreography;

import io.holunda.camunda.bpm.data.CamundaBpmData;
import io.holunda.camunda.bpm.data.factory.VariableFactory;
import org.camunda.bpm.engine.variable.VariableMap;

import static io.holunda.camunda.bpm.data.CamundaBpmData.*;

public class PizzaOrderProcess {
  public static final String KEY = "pizza_order";

  enum Elements {
    ;
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
}
