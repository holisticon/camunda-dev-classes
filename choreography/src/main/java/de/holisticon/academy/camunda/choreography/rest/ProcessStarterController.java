package de.holisticon.academy.camunda.choreography.rest;

import de.holisticon.academy.camunda.choreography.PizzaOrderProcess;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.EventSubscription;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.model.bpmn.instance.Event;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static org.springframework.http.ResponseEntity.ok;

@Api(value = "/process/start")
@RestController
@RequestMapping("/process/start")
public class ProcessStarterController {

  private final RuntimeService runtimeService;

  public ProcessStarterController(RuntimeService runtimeService) {
    this.runtimeService = runtimeService;
  }

  @PostMapping(path = "/pizza-order")
  @ApiOperation(httpMethod = "POST", value = "Starts pizza order process.", response = String.class)
  public ResponseEntity<String> startPizzaOrderProcess() {

    EventSubscription eventSubscription = this.runtimeService.createEventSubscriptionQuery().eventType("message").eventName("foo").singleResult();
    this.runtimeService.

    final ProcessInstance instance = this.runtimeService.startProcessInstanceByKey(
      PizzaOrderProcess.KEY,
      "Pizza-Order-" + UUID.randomUUID().toString(),
      PizzaOrderProcess.createOrder()
    );
    return ok(instance.getProcessInstanceId());
  }

}
