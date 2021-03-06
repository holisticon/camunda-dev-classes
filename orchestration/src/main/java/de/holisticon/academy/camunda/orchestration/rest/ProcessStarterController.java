package de.holisticon.academy.camunda.orchestration.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.ok;

@Api(value = "/process/start")
@RestController
@RequestMapping("/process/start")
public class ProcessStarterController {

  private final RuntimeService runtimeService;

  public ProcessStarterController(RuntimeService runtimeService) {
    this.runtimeService = runtimeService;
  }

  @PostMapping(path = "/approval")
  @ApiOperation(httpMethod = "POST", value = "Starts approval process.", response = String.class)
  public ResponseEntity<String> startApprovalProcess() {
    ProcessInstance instance = this.runtimeService.startProcessInstanceByKey("approval");
    return ok(instance.getProcessInstanceId());
  }

}
