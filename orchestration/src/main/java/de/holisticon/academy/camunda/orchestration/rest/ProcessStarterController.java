package de.holisticon.academy.camunda.orchestration.rest;

import de.holisticon.academy.camunda.orchestration.process.ApprovalProcessBean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.ok;

@Api(value = "/process/start")
@RestController
@RequestMapping("/process/start")
public class ProcessStarterController {

  private final ApprovalProcessBean approvalProcessBean;

  public ProcessStarterController(RuntimeService runtimeService, TaskService taskService) {
    this.approvalProcessBean = new ApprovalProcessBean(runtimeService, taskService);
  }

  @PostMapping(path = "/approval/{id}")
  @ApiOperation(httpMethod = "POST", value = "Starts approval process.", response = String.class)
  public ResponseEntity<String> startApprovalProcess(@PathVariable(name = "id") String id) {
    ProcessInstance instance = approvalProcessBean.start(id);
    return ok(instance.getProcessInstanceId());
  }

}
