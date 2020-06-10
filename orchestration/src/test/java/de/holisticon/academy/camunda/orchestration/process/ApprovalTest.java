package de.holisticon.academy.camunda.orchestration.process;

import de.holisticon.academy.camunda.orchestration.process.ApprovalProcessBean.Elements;
import de.holisticon.academy.camunda.orchestration.process.ApprovalProcessBean.Expressions;
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

import static org.camunda.bpm.engine.test.assertions.bpmn.AbstractAssertions.init;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.*;

@RunWith(ProcessEngineRuleRunner.class)
@Deployment(resources = "approval.bpmn")
public class ApprovalTest {


    @Rule
    public final ProcessEngineRule engine = new StandaloneInMemoryTestConfiguration().rule();
    private ApprovalProcessBean processBean;

    @Before
    public void before() {
        this.processBean = new ApprovalProcessBean(this.engine.getRuntimeService());
        init(engine.getProcessEngine());

        Mocks.register(Expressions.LOAD_APPROVAL_REQUEST, new LoadApprovalRequestDelegate());
    }

    @Test
    public void shouldDeploy() {
        // no asserts, deployment would throw exception and fail the test on errors
    }

    @Test
    public void shouldStartWaitInApprovalRequested() {
        ProcessInstance instance = this.processBean.start();

        assertThat(instance).isNotNull();
        assertThat(instance).isWaitingAt(Elements.APPROVAL_REQUESTED);
    }


    @Test
    public void shouldStartAndLoadAndComplete() {
        ProcessInstance instance = this.processBean.start();

        assertThat(instance).isNotNull();
        assertThat(instance).isWaitingAt(Elements.APPROVAL_REQUESTED);

        execute(job());

        assertThat(instance).isEnded();
        assertThat(instance).hasPassedInOrder(
            Elements.APPROVAL_REQUESTED, Elements.LOAD_APPROVAL_REQUEST, Elements.COMPLETED);

    }

}
