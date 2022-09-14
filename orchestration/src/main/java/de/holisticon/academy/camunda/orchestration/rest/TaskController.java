package de.holisticon.academy.camunda.orchestration.rest;


import de.holisticon.academy.camunda.orchestration.process.ApprovalProcessBean;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.variable.Variables;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.http.ResponseEntity.ok;

@Tag(name = "/task")
@RestController
@RequestMapping("/task")
public class TaskController {

  private final ApprovalProcessBean processBean;

  public TaskController(RuntimeService runtimeService, TaskService taskService) {
    this.processBean = new ApprovalProcessBean(runtimeService, taskService);
  }

  @GetMapping
  @Operation(summary = "Retrieves all user tasks.")
  public ResponseEntity<List<TaskDto>> getTasks() {
    return ok(
      processBean.getTasks()
        .stream()
        .map(task -> new TaskDto(task.getId(), task.getName()))
        .collect(Collectors.toList()));
  }

  @PostMapping(path = "/approve/{taskId}/{approvalDecision}")
  @Operation(summary = "Completes the approve request task")
  public ResponseEntity<Void> completeApproveTask(@PathVariable(name = "taskId") String taskId, @PathVariable(name = "approvalDecision") String approvalDecision) {

    if (!processBean.isValidApprovalDecision(approvalDecision)) {
      return ResponseEntity.badRequest().build();
    }

    Optional<Task> task = processBean.approveTask(taskId);
    if (task.isEmpty()) {
      return ResponseEntity.badRequest().build();
    }

    processBean.complete(task.get().getId(), Variables.putValue(ApprovalProcessBean.Variables.APPROVAL_DECISION, approvalDecision));
    return ResponseEntity.ok().build();
  }

  @PostMapping(path = "/amend/{taskId}/{amendAction}")
  @Operation(summary = "Starts approval process.")
  public ResponseEntity<Void> completeAmendTask(@PathVariable(name = "taskId") String taskId, @PathVariable(name = "amendAction") String amendAction) {

    if (!processBean.isValidAmendAction(amendAction)) {
      return ResponseEntity.badRequest().build();
    }

    Optional<Task> task = processBean.amendTask(taskId);
    if (task.isEmpty()) {
      return ResponseEntity.badRequest().build();
    }

    processBean.complete(task.get().getId(), Variables.putValue(ApprovalProcessBean.Variables.AMEND_ACTION, amendAction));
    return ResponseEntity.ok().build();
  }


  public static class TaskDto {
    private String taskId;
    private String taskName;

    TaskDto(String taskId, String taskName) {
      this.taskId = taskId;
      this.taskName = taskName;
    }

    public String getTaskId() {
      return taskId;
    }
    public void setTaskId(String taskId) {
      this.taskId = taskId;
    }
    public String getTaskName() {
      return taskName;
    }
    public void setTaskName(String taskName) {
      this.taskName = taskName;
    }
  }
}
