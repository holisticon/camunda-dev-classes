package de.holisticon.academy.camunda.choreography.external;

import io.holunda.camunda.bpm.data.CamundaBpmData;
import org.camunda.bpm.engine.ExternalTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static de.holisticon.academy.camunda.choreography.PizzaDeliveryProcess.ExternalTasks.PackPizza;

@Component
public class PackPizzaExternalTaskWorker {

  public static final String WORKER_ID = "packPizzaWorker";

  private static final Logger LOGGER = LoggerFactory.getLogger(PackPizzaExternalTaskWorker.class);

  private final ExternalTaskService externalTaskService;

  public PackPizzaExternalTaskWorker(ExternalTaskService externalTaskService) {
    this.externalTaskService = externalTaskService;
  }

  @Scheduled(initialDelayString = "500", fixedDelayString = "2000")
  public void run() {
    externalTaskService.fetchAndLock(5, WORKER_ID)
      .topic(PackPizza.TOPIC, 1000L)
      .execute()
      .forEach(it -> {
        final var type = PackPizza.Consumes.TYPE.from(it).get();

        LOGGER.info("Packing pizza of type {}", type);

        externalTaskService.complete(
          it.getId(),
          WORKER_ID,
          CamundaBpmData.builder()
            .set(PackPizza.Produces.PACKED, true)
            .build()
        );
      });
  }
}
