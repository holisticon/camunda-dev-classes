package de.holisticon.academy.camunda.orchestration.service;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "APP_APPROVAL_REQUEST")
public class ApprovalRequest {

  @Id
  private String id;
  private String subject;
  private String applicant;
  @Column(name = "amount", precision = 10, scale = 2)
  private BigDecimal amount;

  public ApprovalRequest() {
  }

  public ApprovalRequest(String id, String subject, String applicant, BigDecimal amount) {
    this.id = id;
    this.subject = subject;
    this.applicant = applicant;
    this.amount = amount;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public String getApplicant() {
    return applicant;
  }

  public void setApplicant(String applicant) {
    this.applicant = applicant;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ApprovalRequest that = (ApprovalRequest) o;

    return id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  @Override
  public String toString() {
    return "ApprovalRequest{" +
      "id='" + id + '\'' +
      ", subject='" + subject + '\'' +
      ", applicant='" + applicant + '\'' +
      ", amount=" + amount +
      '}';
  }
}
