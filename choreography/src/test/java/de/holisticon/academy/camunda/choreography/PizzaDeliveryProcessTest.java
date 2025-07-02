package de.holisticon.academy.camunda.choreography;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.junit5.ProcessEngineExtension;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.extension.mockito.CamundaMockito;
import org.camunda.bpm.spring.boot.starter.test.helper.StandaloneInMemoryTestConfiguration;
import org.camunda.spin.plugin.impl.SpinProcessEnginePlugin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.UUID;

import static org.camunda.bpm.engine.test.assertions.bpmn.AbstractAssertions.init;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.complete;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.execute;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.externalTask;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.job;

@Deployment(resources = {"pizzaOrder.bpmn"})
class PizzaDeliveryProcessTest {

    @RegisterExtension
    static ProcessEngineExtension extension = createExtension();

    private String orderId;
    private VariableMap payload;

    @BeforeEach
    void before() {
        init(extension.getProcessEngine());
        CamundaMockito.registerJavaDelegateMock(PizzaDeliveryProcess.Expressions.MAKE_PIZZA_DELEGATE);
        CamundaMockito.registerJavaDelegateMock(PizzaDeliveryProcess.Expressions.DELIVER_PIZZA_DELEGATE)
                .onExecutionSetVariables(Variables.putValue(PizzaOrderProcess.Variables.DELIVERED, true));

        this.orderId = "Pizza-Order-" + UUID.randomUUID().toString();
        this.payload = PizzaOrderProcess.createOrder();

    }

    @Test
    void shouldDeploy() {
        //
    }

    @Test
    void shouldStartEnd() {

        extension.getRuntimeService().correlateMessage(
                PizzaDeliveryProcess.Expressions.MESSAGE_PLACE_ORDER,
                this.orderId,
                this.payload
        );

        ProcessInstance deliveryInstance = extension.getRuntimeService().createProcessInstanceQuery().processInstanceBusinessKey(this.orderId).singleResult();
        assertThat(deliveryInstance).isNotNull();

        // async on start
        assertThat(deliveryInstance).isWaitingAt(PizzaDeliveryProcess.Elements.ORDER_PLACED);
        execute(job());

        // The external task ist asyncAfter, so we need to execute that job
        // process is waiting for the external task to be completed
        assertThat(deliveryInstance).isWaitingAt(PizzaDeliveryProcess.Elements.PACK_PIZZA);
        complete(externalTask());

        // The external task ist asyncAfter, so we need to execute that job
        assertThat(deliveryInstance).isWaitingAt(PizzaDeliveryProcess.Elements.PACK_PIZZA);
        execute(job());

        assertThat(deliveryInstance).isEnded();
        assertThat(deliveryInstance).variables().containsEntry(PizzaOrderProcess.Variables.DELIVERED, true);
    }

    static ProcessEngineExtension createExtension() {
        StandaloneInMemoryTestConfiguration config = new StandaloneInMemoryTestConfiguration();
        config.getProcessEnginePlugins().add(new SpinProcessEnginePlugin());

        return ProcessEngineExtension.builder()
                .useProcessEngine(config.buildProcessEngine())
                .build();
    }
}
