package de.holisticon.academy.camunda.orchestration.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.ok;

@Tag(name = "/process/start")
@RestController
@RequestMapping("/process/start")
public class ProcessStarterController {

  private final RuntimeService runtimeService;

  public ProcessStarterController(RuntimeService runtimeService) {
    this.runtimeService = runtimeService;
  }

  @PostMapping(path = "/approval")
  @Operation(summary = "Starts approval process.", description = "This operation starts the process.")
  public ResponseEntity<String> startApprovalProcess() {
    ProcessInstance instance = this.runtimeService.startProcessInstanceByKey("approval");
    return ok(instance.getProcessInstanceId());
  }

}
