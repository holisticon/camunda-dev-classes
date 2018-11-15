package de.holisticon.academy.camunda.orchestration.process;

import de.holisticon.academy.camunda.orchestration.service.ApprovalRequestRepository;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.engine.test.mock.Mocks;
import org.camunda.bpm.spring.boot.starter.test.helper.ProcessEngineRuleRunner;
import org.camunda.bpm.spring.boot.starter.test.helper.StandaloneInMemoryTestConfiguration;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static de.holisticon.academy.camunda.orchestration.process.SimpleDataProcessingProcessBean.Elements;
import static de.holisticon.academy.camunda.orchestration.process.SimpleDataProcessingProcessBean.Expressions;
import static org.camunda.bpm.engine.test.assertions.bpmn.AbstractAssertions.init;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareAssertions.assertThat;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.execute;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.job;
import static org.mockito.Mockito.mock;

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

    Mocks.register(Expressions.LOAD_DATA_DELEGATE, new LoadDataDelegate(mock(ApprovalRequestRepository.class)));
  }

  @Test
  public void shouldDeploy() {
    // no asserts, deployment would throw exception and fail the test on errors
  }

  @Test
  public void shouldStartWaitInProcessingStarted() {
    ProcessInstance instance = this.processBean.start("1");

    assertThat(instance).isNotNull();
    assertThat(instance).isWaitingAt(Elements.STARTED);

  }

  @Test
  public void shouldStartAndLoadAndComplete() {
    ProcessInstance instance = this.processBean.start("1");

    assertThat(instance).isNotNull();
    assertThat(instance).isWaitingAt(Elements.STARTED);

    execute(job());

    assertThat(instance).isEnded();
    assertThat(instance).hasPassedInOrder(
      Elements.STARTED, Elements.LOAD_DATA, Elements.COMPLETED);

  }

}
