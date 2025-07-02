package de.holisticon.academy.camunda.orchestration.process;

import de.holisticon.academy.camunda.orchestration.process.ApprovalProcessBean.Variables;
import de.holisticon.academy.camunda.orchestration.service.ApprovalRequest;
import de.holisticon.academy.camunda.orchestration.service.ApprovalRequestRepository;
import org.camunda.bpm.extension.mockito.delegate.DelegateExecutionFake;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class LoadDataDelegateTest {

  private final ApprovalRequestRepository approvalRequestRepository = Mockito.mock(ApprovalRequestRepository.class);

  private final LoadApprovalRequestDelegate delegate = new LoadApprovalRequestDelegate(approvalRequestRepository);

  @Test
  void shouldSetVariable() {
    final ApprovalRequest approvalRequest = new ApprovalRequest("1", "Salary increase", "kermit", BigDecimal.valueOf(1000L));

    when(approvalRequestRepository.findById(any())).thenReturn(Optional.of(approvalRequest));

    DelegateExecutionFake execution = new DelegateExecutionFake().withVariable(Variables.APPROVAL_ID, approvalRequest.getId());

    delegate.execute(execution);

    verify(approvalRequestRepository).findById(approvalRequest.getId());
    verifyNoMoreInteractions(approvalRequestRepository);

    assertThat(execution.getVariable(Variables.REQUEST)).isNotNull();
    assertThat(execution.getVariable(Variables.REQUEST)).isEqualTo(approvalRequest);
  }
}
