package de.holisticon.academy.camunda.orchestration.process;

import org.camunda.bpm.engine.runtime.Execution;
import org.camunda.bpm.engine.runtime.ProcessInstance;

import java.util.function.Supplier;

public class ApprovalProcessInstance implements Execution, ProcessInstance, Supplier<ProcessInstance> {

  private final ProcessInstance delegate;

  private ApprovalProcessInstance(ProcessInstance processInstance) {
    this.delegate = processInstance;
  }

  public static ApprovalProcessInstance wrap(ProcessInstance processInstance) {
    return new ApprovalProcessInstance(processInstance);
  }

  @Override
  public ProcessInstance get() {
    return delegate;
  }

  @Override
  public String getProcessDefinitionId() {
    return delegate.getProcessDefinitionId();
  }

  @Override
  public String getBusinessKey() {
    return delegate.getProcessDefinitionId();
  }

  @Override
  public String getRootProcessInstanceId() {
    return delegate.getRootProcessInstanceId();
  }

  @Override
  public String getCaseInstanceId() {
    return delegate.getCaseInstanceId();
  }

  @Override
  public String getId() {
    return delegate.getId();
  }

  @Override
  public boolean isSuspended() {
    return delegate.isSuspended();
  }

  @Override
  public boolean isEnded() {
    return delegate.isEnded();
  }

  @Override
  public String getProcessInstanceId() {
    return delegate.getProcessInstanceId();
  }

  @Override
  public String getTenantId() {
    return delegate.getTenantId();
  }
}
