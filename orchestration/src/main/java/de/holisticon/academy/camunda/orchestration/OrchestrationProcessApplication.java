package de.holisticon.academy.camunda.orchestration;

import org.camunda.bpm.application.ProcessApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@ProcessApplication
@SpringBootApplication
public class OrchestrationProcessApplication {

  public static void main(String[] args) {
    SpringApplication.run(OrchestrationProcessApplication.class, args);
  }

}
