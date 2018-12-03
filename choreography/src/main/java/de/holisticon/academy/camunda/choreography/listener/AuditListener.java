package de.holisticon.academy.camunda.choreography.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("audit")
public class AuditListener {

  private static final Logger logger = LoggerFactory.getLogger(AuditListener.class);

  private String lastMessage;

  public void log(String message) {
    this.lastMessage = message;
    logger.info("AUDIT: {}", this.lastMessage);
  }

  public void log(String message, String variableValue) {
    log(message + variableValue);
  }


  public String getLastMessage() {
    return lastMessage;
  }

}
