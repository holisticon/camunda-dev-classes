package de.holisticon.academy.camunda.choreography;

import io.holunda.camunda.bpm.data.factory.VariableFactory;

import static io.holunda.camunda.bpm.data.CamundaBpmData.booleanVariable;
import static io.holunda.camunda.bpm.data.CamundaBpmData.stringVariable;

public class PizzaDeliveryProcess {

  enum Elements {
    ;
    public static final String ORDER_PLACED = "order_placed";
    public static final String PACK_PIZZA = "task_pack_pizza";
  }

  public enum Variables {
    ;
    public static final VariableFactory<String> TYPE = stringVariable("type");
    public static final VariableFactory<Boolean> PACKED = booleanVariable("packed");
  }

  public enum Expressions {
    ;
    public static final String MESSAGE_PLACE_ORDER = "placeOrder";
    public static final String DELIVER_PIZZA_DELEGATE = "deliverPizzaDelegate";
    public static final String MAKE_PIZZA_DELEGATE = "makePizzaDelegate";
  }

  public enum ExternalTasks {
    ;
    public enum PackPizza {
      ;
      public static final String TOPIC = "orderPizza:packPizza";

      public enum Consumes {
        ;
        public static final VariableFactory<String> TYPE = Variables.TYPE;
      }

      public enum Produces {
        ;
        public static final VariableFactory<Boolean> PACKED = Variables.PACKED;
      }
    }
  }
}
