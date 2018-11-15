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

@Api(value = "/process/start", description = "Starter for processes.")
@RestController
@RequestMapping("/process/start")
public class ProcessStarterController {

  private final RuntimeService runtimeService;

  public ProcessStarterController(RuntimeService runtimeService) {
    this.runtimeService = runtimeService;
  }

  @PostMapping(path = "/simple-data-processing")
  @ApiOperation(httpMethod = "POST", value = "Starts simple_data_processing process.", response = String.class)
  public ResponseEntity<String> startSimpleDataProcessingProcess() {
    ProcessInstance instance = this.runtimeService.startProcessInstanceByKey("simple_data_processing");
    return ok(instance.getProcessInstanceId());
  }

}
