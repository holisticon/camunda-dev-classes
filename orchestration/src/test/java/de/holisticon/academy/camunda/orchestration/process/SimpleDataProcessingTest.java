package de.holisticon.academy.camunda.orchestration.process;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.spring.boot.starter.test.helper.ProcessEngineRuleRunner;
import org.camunda.bpm.spring.boot.starter.test.helper.StandaloneInMemoryTestConfiguration;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.camunda.bpm.engine.test.assertions.bpmn.AbstractAssertions.init;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareAssertions.assertThat;

@RunWith(ProcessEngineRuleRunner.class)
@Deployment(resources = "simple-data-processing.bpmn")
public class SimpleDataProcessingTest {


  @Rule
  public final ProcessEngineRule engine = new StandaloneInMemoryTestConfiguration().rule();
  private SimpleDataProcessingProcessBean processBean;

  @Before
  public void before() {
    this.processBean = new SimpleDataProcessingProcessBean(this.engine.getRuntimeService());
    init(engine.getProcessEngine());
  }

  @Test
  public void shouldDeploy() {
    // no asserts, deployment would throw exception and fail the test on errors
  }

  @Test
  public void shouldStartWaitInProcessingStarted() {
    ProcessInstance instance = this.processBean.start();

    assertThat(instance).isNotNull();
    assertThat(instance).isWaitingAt(SimpleDataProcessingProcessBean.Elements.STARTED);
  }

}
