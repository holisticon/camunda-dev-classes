package de.holisticon.academy.camunda.choreography;

import org.camunda.bpm.application.ProcessApplication;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.cfg.ProcessEnginePlugin;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@ProcessApplication
@SpringBootApplication
public class ChoreographyProcessApplication {

  public static void main(String[] args) {
    SpringApplication.run(ChoreographyProcessApplication.class, args);
  }

  @Bean
  ProcessEnginePlugin disableTelemetry() {

    return new ProcessEnginePlugin() {
      @Override
      public void preInit(ProcessEngineConfigurationImpl processEngineConfiguration) {
        processEngineConfiguration.setTelemetryReporterActivate(false);
        processEngineConfiguration.setInitializeTelemetry(false);
      }

      @Override
      public void postInit(ProcessEngineConfigurationImpl processEngineConfiguration) {
      }

      @Override
      public void postProcessEngineBuild(ProcessEngine processEngine) {
      }
    };
  }

}
