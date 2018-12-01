package de.holisticon.academy.camunda.orchestration.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApprovalRequestConfiguration {

  private static final Logger logger = LoggerFactory.getLogger(ApprovalRequestConfiguration.class);

  @Autowired
  private ApprovalRequestRepository repository;

  @Bean
  public ApplicationRunner showApprovalRequests() {
    return args -> {
      logger.info("Found {} approvals.", repository.count());
      repository.findAll().stream().forEach(
        approval -> logger.info("{}", approval)
      );
    };
  }
}
