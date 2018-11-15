package de.holisticon.academy.camunda.orchestration.rest;

import de.holisticon.academy.camunda.orchestration.process.SimpleDataProcessingProcessBean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.ok;

@Api(value = "/process/start", description = "Starter for processes.")
@RestController
@RequestMapping("/process/start")
public class ProcessStarterController {

  private final SimpleDataProcessingProcessBean simpleDataProcessingProcessBean;

  public ProcessStarterController(RuntimeService runtimeService) {
    this.simpleDataProcessingProcessBean = new SimpleDataProcessingProcessBean(runtimeService);
  }

  @PostMapping(path = "/simple-data-processing/{id}")
  @ApiOperation(httpMethod = "POST", value = "Starts simple_data_processing process.", response = String.class)
  public ResponseEntity<String> startSimpleDataProcessingProcess(@PathVariable(name = "id") String id) {
    ProcessInstance instance = simpleDataProcessingProcessBean.start(id);
    return ok(instance.getProcessInstanceId());
  }

}
