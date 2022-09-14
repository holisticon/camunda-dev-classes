package de.holisticon.academy.camunda.orchestration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.camunda.bpm.application.ProcessApplication;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.cfg.ProcessEnginePlugin;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@ProcessApplication @SpringBootApplication public class OrchestrationProcessApplication {

  public static void main(String[] args) {
    SpringApplication.run(OrchestrationProcessApplication.class, args);
  }

  @Bean ProcessEnginePlugin disableTelemetry() {

    return new ProcessEnginePlugin() {
      @Override public void preInit(ProcessEngineConfigurationImpl processEngineConfiguration) {
        processEngineConfiguration.setTelemetryReporterActivate(false);
        processEngineConfiguration.setInitializeTelemetry(false);
      }

      @Override public void postInit(ProcessEngineConfigurationImpl processEngineConfiguration) {
      }

      @Override public void postProcessEngineBuild(ProcessEngine processEngine) {
      }
    };
  }

  @Bean public OpenAPI orchestrationApi() {
    return new OpenAPI().info(new Info().title("Camunda Developer Workshop Classes").description("REST endpoints for process application control.")
      .version("v0.0.1").contact(new Contact().name("Holisticon AG").url("https://holisticon.de/")));
  }
}
