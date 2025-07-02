package de.holisticon.academy.camunda.choreography;

import com.tngtech.jgiven.annotation.BeforeStage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.Hidden;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.junit5.DualScenarioTest;
import io.holunda.camunda.bpm.data.CamundaBpmData;
import io.holunda.camunda.bpm.extension.jgiven.ProcessStage;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.junit5.ProcessEngineExtension;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.extension.mockito.CamundaMockito;
import org.camunda.bpm.spring.boot.starter.test.helper.StandaloneInMemoryTestConfiguration;
import org.camunda.spin.plugin.impl.SpinProcessEnginePlugin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.UUID;

import static org.camunda.bpm.engine.test.assertions.bpmn.AbstractAssertions.init;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;

@Deployment(resources = {"pizzaOrder.bpmn"})
class PizzaDeliveryProcessTest extends DualScenarioTest<PizzaDeliveryProcessTest.GivenWhenStage, PizzaDeliveryProcessTest.ThenStage> {

    @RegisterExtension
    static ProcessEngineExtension extension = createExtension();

    @ProvidedScenarioState
    static ProcessEngine camunda = extension.getProcessEngine();


    @Test
    @Hidden
    void shouldDeploy() {
        then()
                .process_is_deployed("pizza_delivery");
    }

    @Test
    void shouldStartEnd() {
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

    static ProcessEngineExtension createExtension() {
        StandaloneInMemoryTestConfiguration config = new StandaloneInMemoryTestConfiguration();
        config.getProcessEnginePlugins().add(new SpinProcessEnginePlugin());

        return ProcessEngineExtension.builder()
                .useProcessEngine(config.buildProcessEngine())
                .build();
    }

    static class GivenWhenStage extends ProcessStage<GivenWhenStage, PizzaDeliveryProcess.PizzaDeliveryProcessInstance> {

        @ExpectedScenarioState
        private PizzaDeliveryProcess pizzaDeliveryProcess;

        @ProvidedScenarioState
        private String orderId;

        @ProvidedScenarioState
        private VariableMap payload;

        @BeforeStage
        void before() {
            init(camunda);

            CamundaMockito.registerJavaDelegateMock(PizzaDeliveryProcess.Expressions.MAKE_PIZZA_DELEGATE);
            CamundaMockito.registerJavaDelegateMock(PizzaDeliveryProcess.Expressions.DELIVER_PIZZA_DELEGATE)
                    .onExecutionSetVariables(CamundaBpmData.builder().set(PizzaOrderProcess.Variables.DELIVERED, true).build());

            this.orderId = "Pizza-Order-" + UUID.randomUUID();
            this.payload = PizzaOrderProcess.createOrder();

            this.pizzaDeliveryProcess = new PizzaDeliveryProcess(camunda.getRuntimeService());
        }

        GivenWhenStage a_pizza_is_ordered() {
            this.camunda.getRuntimeService().correlateMessage(
                    PizzaDeliveryProcess.Expressions.MESSAGE_PLACE_ORDER,
                    this.orderId,
                    this.payload
            );

            processInstanceSupplier = pizzaDeliveryProcess.getByOrderId(this.orderId);

            return self();
        }
    }

    static class ThenStage extends ProcessStage<ThenStage, PizzaDeliveryProcess.PizzaDeliveryProcessInstance> {
        ThenStage the_pizza_was_delivered() {
            assertThat(processInstanceSupplier.get()).variables().containsEntry(PizzaOrderProcess.Variables.DELIVERED.getName(), true);

            return self();
        }
    }

}
