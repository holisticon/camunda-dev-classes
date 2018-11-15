package de.holisticon.academy.camunda.orchestration.process;

import de.holisticon.academy.camunda.orchestration.process.SimpleDataProcessingProcessBean.Variables;
import de.holisticon.academy.camunda.orchestration.service.ApprovalRequest;
import de.holisticon.academy.camunda.orchestration.service.ApprovalRequestRepository;
import org.camunda.bpm.extension.mockito.delegate.DelegateExecutionFake;
import org.junit.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class LoadDataDelegateTest {

  private ApprovalRequestRepository approvalRequestRepository = Mockito.mock(ApprovalRequestRepository.class);

  private LoadDataDelegate delegate = new LoadDataDelegate(approvalRequestRepository);

  @Test
  public void shouldSetVariable() {
    final ApprovalRequest approvalRequest = new ApprovalRequest("1", "Salary increase", "kermit", BigDecimal.valueOf(1000L));

    when(approvalRequestRepository.findById(any())).thenReturn(Optional.of(approvalRequest));

    DelegateExecutionFake execution = new DelegateExecutionFake().withVariable(Variables.APPROVAL_ID, approvalRequest.getId());

    delegate.execute(execution);

    verify(approvalRequestRepository).findById(approvalRequest.getId());
    verifyNoMoreInteractions(approvalRequestRepository);

    assertThat(execution.getVariable(Variables.AMOUNT)).isNotNull();
    assertThat(execution.getVariable(Variables.AMOUNT)).isEqualTo(approvalRequest.getAmount());
  }
}
