package de.holisticon.academy.camunda.choreography;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.camunda.bpm.application.ProcessApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@ProcessApplication
@SpringBootApplication
@EnableScheduling
public class ChoreographyProcessApplication {

  public static void main(String[] args) {
    SpringApplication.run(ChoreographyProcessApplication.class, args);
  }

}
