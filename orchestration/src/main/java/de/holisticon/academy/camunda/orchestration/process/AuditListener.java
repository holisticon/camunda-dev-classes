package de.holisticon.academy.camunda.orchestration.process;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("audit")
public class AuditListener {

  private static final Logger logger = LoggerFactory.getLogger(AuditListener.class);

  public void log(String message) {
    logger.info("AUDIT: {}", message);
  }
}
