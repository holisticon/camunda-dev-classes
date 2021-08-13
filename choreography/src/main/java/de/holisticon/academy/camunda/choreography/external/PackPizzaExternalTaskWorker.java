package de.holisticon.academy.camunda.choreography.external;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
public class PackPizzaExternalTaskWorker {

  private static final Logger LOGGER = LoggerFactory.getLogger(PackPizzaExternalTaskWorker.class);

  @Scheduled(initialDelayString = "500", fixedDelayString = "2000")
  public void run() {
    // TODO implement the external task worker
    throw new UnsupportedOperationException();
  }
}
