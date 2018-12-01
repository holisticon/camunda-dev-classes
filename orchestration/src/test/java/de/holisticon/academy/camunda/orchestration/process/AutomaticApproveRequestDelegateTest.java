package de.holisticon.academy.camunda.orchestration.process;

import de.holisticon.academy.camunda.orchestration.service.ApprovalRequest;
import de.holisticon.academy.camunda.orchestration.service.ApprovalRequestRepository;
import de.holisticon.academy.camunda.orchestration.service.AutomaticApprovalService;
import org.camunda.bpm.extension.mockito.delegate.DelegateExecutionFake;
import org.junit.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AutomaticApproveRequestDelegateTest {

  private AutomaticApprovalService approvalService = mock(AutomaticApprovalService.class);
  private ApprovalRequestRepository approvalRequestRepository = Mockito.mock(ApprovalRequestRepository.class);

  private AutomaticApproveRequestDelegate delegate = new AutomaticApproveRequestDelegate(approvalRequestRepository, approvalService);

  @Test
  public void test_approve() {

    final ApprovalRequest approvalRequest = new ApprovalRequest("1", "Salary increase", "kermit", BigDecimal.valueOf(10L));
    when(approvalRequestRepository.findById(any())).thenReturn(Optional.of(approvalRequest));
    when(approvalService.approve(any())).thenReturn(true);
    DelegateExecutionFake execution = new DelegateExecutionFake()
      .withVariable(ApprovalProcessBean.Variables.APPROVAL_ID, approvalRequest.getId());

    delegate.execute(execution);

    verify(approvalRequestRepository).findById(approvalRequest.getId());
    verifyNoMoreInteractions(approvalRequestRepository);
    verify(approvalService).approve(approvalRequest);

    assertThat(execution.getVariable(ApprovalProcessBean.Variables.APPROVAL_DECISION)).isNotNull();
    assertThat(execution.getVariable(ApprovalProcessBean.Variables.APPROVAL_DECISION)).isEqualTo(ApprovalProcessBean.Values.APPROVAL_DECISION_APPROVED);
  }

  @Test
  public void test_reject() {

    final ApprovalRequest approvalRequest = new ApprovalRequest("1", "Salary increase", "kermit", BigDecimal.valueOf(10L));
    when(approvalRequestRepository.findById(any())).thenReturn(Optional.of(approvalRequest));
    when(approvalService.approve(any())).thenReturn(false);
    DelegateExecutionFake execution = new DelegateExecutionFake()
      .withVariable(ApprovalProcessBean.Variables.APPROVAL_ID, approvalRequest.getId());

    delegate.execute(execution);

    verify(approvalRequestRepository).findById(approvalRequest.getId());
    verifyNoMoreInteractions(approvalRequestRepository);
    verify(approvalService).approve(approvalRequest);

    assertThat(execution.getVariable(ApprovalProcessBean.Variables.APPROVAL_DECISION)).isNotNull();
    assertThat(execution.getVariable(ApprovalProcessBean.Variables.APPROVAL_DECISION)).isEqualTo(ApprovalProcessBean.Values.APPROVAL_DECISION_REJECTED);

  }

}
