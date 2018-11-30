package de.holisticon.academy.camunda.orchestration.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AutomaticApprovalService {

  private static final BigDecimal MAGIC_AMOUNT = new BigDecimal("83.12");

  public boolean approve(ApprovalRequest approvalRequest) {

    if (approvalRequest.getAmount().compareTo(MAGIC_AMOUNT) == 0) {
      throw new RuntimeException("Something bad happened during approval");
    }

    return true;
  }
}
