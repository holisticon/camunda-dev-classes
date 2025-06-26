package de.holisticon.academy.camunda.orchestration.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import de.holisticon.academy.camunda.orchestration.process.ApprovalProcessBean;
import de.holisticon.academy.camunda.orchestration.process.ApprovalProcessInstance;
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

  private final ApprovalProcessBean approvalProcessBean;

  public ProcessStarterController(RuntimeService runtimeService) {
    this.approvalProcessBean = new ApprovalProcessBean(runtimeService);
  }

  @PostMapping(path = "/approval")
  @Operation(summary = "Starts approval process.", description = "This operation starts the process.")
  public ResponseEntity<String> startApprovalProcess() {
    ApprovalProcessInstance instance = approvalProcessBean.start();
    return ok(instance.get().getProcessInstanceId());
  }

}
