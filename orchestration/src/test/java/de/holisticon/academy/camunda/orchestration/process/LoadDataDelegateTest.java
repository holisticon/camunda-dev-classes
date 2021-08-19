package de.holisticon.academy.camunda.orchestration.process;

import de.holisticon.academy.camunda.orchestration.process.ApprovalProcessBean.Variables;
import de.holisticon.academy.camunda.orchestration.service.ApprovalRequest;
import de.holisticon.academy.camunda.orchestration.service.ApprovalRequestRepository;
import io.holunda.camunda.bpm.data.CamundaBpmData;
import io.holunda.camunda.bpm.data.reader.VariableReader;
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

  private LoadApprovalRequestDelegate delegate = new LoadApprovalRequestDelegate(approvalRequestRepository);

  @Test
  public void shouldSetVariable() {
    final ApprovalRequest approvalRequest = new ApprovalRequest("1", "Salary increase", "kermit", BigDecimal.valueOf(1000L));

    when(approvalRequestRepository.findById(any())).thenReturn(Optional.of(approvalRequest));

    DelegateExecutionFake execution = new DelegateExecutionFake().withVariable(Variables.APPROVAL_ID, approvalRequest.getId());

    delegate.execute(execution);

    verify(approvalRequestRepository).findById(approvalRequest.getId());
    verifyNoMoreInteractions(approvalRequestRepository);

    final var reader = CamundaBpmData.reader(execution);
    assertThat(reader.get(Variables.REQUEST)).isNotNull();
    assertThat(reader.get(Variables.REQUEST)).isEqualTo(approvalRequest);
  }
}
