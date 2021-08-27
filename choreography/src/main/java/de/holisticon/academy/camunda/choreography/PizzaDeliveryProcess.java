package de.holisticon.academy.camunda.choreography;

import io.holunda.camunda.bpm.data.acl.AntiCorruptionLayer;
import io.holunda.camunda.bpm.data.acl.CamundaBpmDataACL;
import io.holunda.camunda.bpm.data.acl.transform.IdentityVariableMapTransformer;
import io.holunda.camunda.bpm.data.factory.VariableFactory;
import io.holunda.camunda.bpm.data.guard.VariablesGuard;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;

import java.util.List;
import java.util.function.Supplier;

import static io.holunda.camunda.bpm.data.CamundaBpmData.booleanVariable;
import static io.holunda.camunda.bpm.data.CamundaBpmData.stringVariable;
import static io.holunda.camunda.bpm.data.guard.CamundaBpmDataGuards.exists;

public class PizzaDeliveryProcess {

  private final RuntimeService runtimeService;

  public PizzaDeliveryProcess(RuntimeService runtimeService) {
    this.runtimeService = runtimeService;
  }

  public PizzaDeliveryProcessInstance getByOrderId(String orderId) {
    return PizzaDeliveryProcessInstance.wrap(
      runtimeService.createProcessInstanceQuery().processInstanceBusinessKey(orderId).singleResult()
    );
  }

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

      public static final AntiCorruptionLayer ACL = CamundaBpmDataACL.guardTransformingGlobalReplace(
        "__transient",
        new VariablesGuard(List.of(
          exists(Produces.PACKED) // conditions
        )),
        IdentityVariableMapTransformer.INSTANCE
      );

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

  public static class PizzaDeliveryProcessInstance implements Supplier<ProcessInstance> {

    static PizzaDeliveryProcessInstance wrap(ProcessInstance processInstance) {
      return new PizzaDeliveryProcessInstance(processInstance);
    }

    private final ProcessInstance delegate;

    private PizzaDeliveryProcessInstance(ProcessInstance processInstance) {
      this.delegate = processInstance;
    }

    @Override
    public ProcessInstance get() {
      return delegate;
    }
  }
}
