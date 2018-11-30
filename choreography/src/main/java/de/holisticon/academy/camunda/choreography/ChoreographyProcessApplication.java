package de.holisticon.academy.camunda.choreography;

import org.camunda.bpm.application.ProcessApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@ProcessApplication
@SpringBootApplication
public class ChoreographyProcessApplication {

  public static void main(String[] args) {
    SpringApplication.run(ChoreographyProcessApplication.class, args);
  }

}
