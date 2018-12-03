package de.holisticon.academy.camunda.choreography;

import org.camunda.bpm.engine.variable.VariableMap;

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

  enum Expressions {
    ;
    public static final String PLACE_ORDER_DELEGATE = "placeOrderDelegate";
    public static final String MESSAGE_PIZZA_RECEIVED = "pizzaReceived";
    public static final String AUDIT = "audit";
  }

  enum Variables {
    ;
    public static final String SIZE = "size";
    public static final String TYPE = "type";
    public static final String DELIVERED = "delivered";
  }

  public static VariableMap createOrder() {
    return org.camunda.bpm.engine.variable.Variables
      .putValue(Variables.TYPE, "Funghi")
      .putValue(Variables.SIZE, "medium")
      .putValue(Variables.DELIVERED, false);
  }
}
