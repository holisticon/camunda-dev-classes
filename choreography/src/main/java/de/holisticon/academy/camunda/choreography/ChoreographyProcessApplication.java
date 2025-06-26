package de.holisticon.academy.camunda.choreography;

import org.camunda.bpm.application.ProcessApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@ProcessApplication
@SpringBootApplication
@EnableScheduling
public class ChoreographyProcessApplication {

  public static void main(String[] args) {
    SpringApplication.run(ChoreographyProcessApplication.class, args);
  }

}
