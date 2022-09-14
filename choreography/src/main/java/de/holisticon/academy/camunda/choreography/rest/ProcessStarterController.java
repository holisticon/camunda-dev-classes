package de.holisticon.academy.camunda.choreography.rest;

import de.holisticon.academy.camunda.choreography.PizzaOrderProcess;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static org.springframework.http.ResponseEntity.ok;

@Tag(name = "/process/start")
@RestController
@RequestMapping("/process/start")
public class ProcessStarterController {

  private final RuntimeService runtimeService;

  public ProcessStarterController(RuntimeService runtimeService) {
    this.runtimeService = runtimeService;
  }

  @PostMapping(path = "/pizza-order")
  @Operation(summary = "Starts pizza order process.")
  public ResponseEntity<String> startPizzaOrderProcess() {

    final ProcessInstance instance = this.runtimeService.startProcessInstanceByKey(
      PizzaOrderProcess.KEY,
      "Pizza-Order-" + UUID.randomUUID(),
      PizzaOrderProcess.createOrder()
    );
    return ok(instance.getProcessInstanceId());
  }

}
