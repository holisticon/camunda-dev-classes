package de.holisticon.academy.camunda.choreography;

public class PizzaDeliveryProcess {

  enum Elements {
    ;
    public static final String ORDER_PLACED = "order_placed";
  }

  public enum Expressions {
    ;
    public static final String MESSAGE_PLACE_ORDER = "placeOrder";
    public static final String DELIVER_PIZZA_DELEGATE = "deliverPizzaDelegate";
    public static final String MAKE_PIZZA_DELEGATE = "makePizzaDelegate";
  }
}
