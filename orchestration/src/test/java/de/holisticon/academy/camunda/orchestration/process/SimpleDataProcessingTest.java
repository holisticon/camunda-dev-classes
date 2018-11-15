package de.holisticon.academy.camunda.orchestration.process;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.spring.boot.starter.test.helper.ProcessEngineRuleRunner;
import org.camunda.bpm.spring.boot.starter.test.helper.StandaloneInMemoryTestConfiguration;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import static org.camunda.bpm.engine.test.assertions.bpmn.AbstractAssertions.init;

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

}
