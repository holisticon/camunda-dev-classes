package de.holisticon.academy.camunda.orchestration.process;

import de.holisticon.academy.camunda.orchestration.process.ApprovalProcessBean.Elements;
import de.holisticon.academy.camunda.orchestration.process.ApprovalProcessBean.Expressions;
import de.holisticon.academy.camunda.orchestration.service.ApprovalRequest;
import io.holunda.camunda.bpm.data.CamundaBpmData;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.impl.util.ClockUtil;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.junit5.ProcessEngineExtension;
import org.camunda.bpm.engine.test.mock.Mocks;
import org.camunda.bpm.extension.mockito.CamundaMockito;
import org.camunda.bpm.spring.boot.starter.test.helper.StandaloneInMemoryTestConfiguration;
import org.camunda.spin.plugin.impl.SpinProcessEnginePlugin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.math.BigDecimal;
import java.time.Duration;

import static org.camunda.bpm.engine.test.assertions.bpmn.AbstractAssertions.init;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.*;

@Deployment(resources = {"approval.bpmn", "approvalStrategy.dmn"})
class ApprovalTest {

    @RegisterExtension
    static ProcessEngineExtension extension = createExtension();

    private ApprovalProcessBean processBean;

    @BeforeEach
    void setUp() {
        this.processBean = new ApprovalProcessBean(extension.getRuntimeService(), extension.getTaskService());
        init(extension.getProcessEngine());

        CamundaMockito.registerJavaDelegateMock(Expressions.DETERMINE_APPROVAL_STRATEGY);
        CamundaMockito.registerJavaDelegateMock(Expressions.AUTO_APPROVE_REQUEST);

        Mocks.register(Expressions.AUDIT, new AuditListener());
    }

    @Test
    void shouldDeploy() {
        // no asserts, deployment would throw exception and fail the test on errors
    }

    @Test
    void shouldStartWaitInApprovalRequested() {
        ProcessInstance instance = this.processBean.start("1");

        assertThat(instance).isNotNull();
        assertThat(instance).isWaitingAt(Elements.APPROVAL_REQUESTED);
    }


    @Test
    void shouldStartAndLoadAndApprove() {
        ProcessInstance instance = this.processBean.start("1");

        assertThat(instance).isNotNull();
        assertThat(instance).isWaitingAt(Elements.APPROVAL_REQUESTED);

        execute(job());

        assertThat(instance).isWaitingAt(Elements.LOAD_APPROVAL_REQUEST);
        complete(externalTask(),
          CamundaBpmData.builder()
            .set(ApprovalProcessBean.Variables.REQUEST, new ApprovalRequest("id", "subj", "kermit", new BigDecimal("7.81")))
            .build()
        );
        execute(job());

        assertThat(instance).isEnded();
        assertThat(instance).hasPassedInOrder(
                Elements.APPROVAL_REQUESTED, Elements.LOAD_APPROVAL_REQUEST, Elements.DETERMINE_APPROVAL_STRATEGY, Elements.REQUEST_APPROVED);
    }

    @Test
    void shouldStartAndLoadAndReject() {
        ProcessInstance instance = this.processBean.start("1");

        assertThat(instance).isNotNull();
        assertThat(instance).isWaitingAt(Elements.APPROVAL_REQUESTED);

        execute(job());

        assertThat(instance).isWaitingAt(Elements.LOAD_APPROVAL_REQUEST);
        complete(externalTask(),
          CamundaBpmData.builder()
            .set(ApprovalProcessBean.Variables.REQUEST, new ApprovalRequest("id", "subj", "kermit", new BigDecimal("117.81")))
            .build()
        );
        execute(job());

        assertThat(instance).isWaitingAt(Elements.USER_APPROVE_REQUEST);

    }

    @Test
    void shouldStartAndLoadAndApproveAndFail() {

        CamundaMockito.getJavaDelegateMock(Expressions.AUTO_APPROVE_REQUEST)
                .onExecutionThrowBpmnError(new BpmnError(Expressions.ERROR));

        ProcessInstance instance = this.processBean.start("1");

        assertThat(instance).isNotNull();
        assertThat(instance).isWaitingAt(Elements.APPROVAL_REQUESTED);

        execute(job());

        assertThat(instance).isWaitingAt(Elements.LOAD_APPROVAL_REQUEST);
        complete(externalTask(),
          CamundaBpmData.builder()
            .set(ApprovalProcessBean.Variables.REQUEST, new ApprovalRequest("id", "subj", "kermit", new BigDecimal("83.12")))
            .build()
        );
        execute(job());

        assertThat(instance).isWaitingAt(Elements.USER_APPROVE_REQUEST);

        CamundaMockito.verifyJavaDelegateMock(Expressions.AUTO_APPROVE_REQUEST).executed();
    }

    @Test
    void shouldStartAndLoadAndManual() {

        ProcessInstance instance = this.processBean.start("1");

        assertThat(instance).isNotNull();
        assertThat(instance).isWaitingAt(Elements.APPROVAL_REQUESTED);

        execute(job());

        assertThat(instance).isWaitingAt(Elements.LOAD_APPROVAL_REQUEST);
        complete(externalTask(),
          CamundaBpmData.builder()
            .set(ApprovalProcessBean.Variables.REQUEST, new ApprovalRequest("id", "subj", "kermit", new BigDecimal("117.81")))
            .build()
        );
        execute(job());

        assertThat(instance).isWaitingAt(Elements.USER_APPROVE_REQUEST);

    }

    @Test
    void shouldStartAndLoadAndManualAndApprove() {
        ProcessInstance instance = this.processBean.start("1");

        assertThat(instance).isNotNull();
        assertThat(instance).isWaitingAt(Elements.APPROVAL_REQUESTED);

        execute(job());

        assertThat(instance).isWaitingAt(Elements.LOAD_APPROVAL_REQUEST);
        complete(externalTask(),
          CamundaBpmData.builder()
            .set(ApprovalProcessBean.Variables.REQUEST, new ApprovalRequest("id", "subj", "kermit", new BigDecimal("117.81")))
            .build()
        );
        execute(job());

        assertThat(instance).isWaitingAt(Elements.USER_APPROVE_REQUEST);
        this.processBean.complete(task().getId(), CamundaBpmData.builder().set(ApprovalProcessBean.Variables.APPROVAL_DECISION, ApprovalProcessBean.Values.APPROVAL_DECISION_APPROVED).build());
        execute(job());

        assertThat(instance).isEnded();
        assertThat(instance).hasPassedInOrder(
                Elements.APPROVAL_REQUESTED, Elements.LOAD_APPROVAL_REQUEST, Elements.DETERMINE_APPROVAL_STRATEGY, Elements.USER_APPROVE_REQUEST, Elements.REQUEST_APPROVED
        );
    }

    @Test
    void shouldStartAndLoadAndManualAndReject() {
        ProcessInstance instance = this.processBean.start("1");

        assertThat(instance).isNotNull();
        assertThat(instance).isWaitingAt(Elements.APPROVAL_REQUESTED);

        execute(job());

        assertThat(instance).isWaitingAt(Elements.LOAD_APPROVAL_REQUEST);
        complete(externalTask(),
          CamundaBpmData.builder()
            .set(ApprovalProcessBean.Variables.REQUEST, new ApprovalRequest("id", "subj", "kermit", new BigDecimal("117.81")))
            .build()
        );
        execute(job());

        assertThat(instance).isWaitingAt(Elements.USER_APPROVE_REQUEST);
        this.processBean.complete(task().getId(), CamundaBpmData.builder().set(ApprovalProcessBean.Variables.APPROVAL_DECISION, ApprovalProcessBean.Values.APPROVAL_DECISION_REJECTED).build());
        execute(job());

        assertThat(instance).isEnded();
        assertThat(instance).hasPassedInOrder(
                Elements.APPROVAL_REQUESTED, Elements.LOAD_APPROVAL_REQUEST, Elements.DETERMINE_APPROVAL_STRATEGY, Elements.USER_APPROVE_REQUEST, Elements.REQUEST_REJECTED
        );
    }

    @Test
    void shouldStartAndLoadAndManualAndReturnedAndCancel() {
        ProcessInstance instance = this.processBean.start("1");

        assertThat(instance).isNotNull();
        assertThat(instance).isWaitingAt(Elements.APPROVAL_REQUESTED);

        execute(job());

        assertThat(instance).isWaitingAt(Elements.LOAD_APPROVAL_REQUEST);
        complete(externalTask(),
          CamundaBpmData.builder()
            .set(ApprovalProcessBean.Variables.REQUEST, new ApprovalRequest("id", "subj", "kermit", new BigDecimal("117.81")))
            .build()
        );
        execute(job());

        assertThat(instance).isWaitingAt(Elements.USER_APPROVE_REQUEST);
        this.processBean.complete(task().getId(), CamundaBpmData.builder().set(ApprovalProcessBean.Variables.APPROVAL_DECISION, ApprovalProcessBean.Values.APPROVAL_DECISION_RETURNED).build());
        execute(job());

        assertThat(instance).isWaitingAt(Elements.USER_AMEND_REQUEST);
        this.processBean.complete(task().getId(), CamundaBpmData.builder().set(ApprovalProcessBean.Variables.AMEND_ACTION, ApprovalProcessBean.Values.AMEND_ACTION_CANCELLED).build());
        execute(job());

        assertThat(instance).isEnded();
        assertThat(instance).hasPassedInOrder(
                Elements.APPROVAL_REQUESTED, Elements.LOAD_APPROVAL_REQUEST, Elements.DETERMINE_APPROVAL_STRATEGY, Elements.USER_APPROVE_REQUEST, Elements.REQUEST_CANCELLED
        );
    }

    @Test
    public void shouldStartAndLoadAndManualAndReturnedAndCancelByTimeout() {

        ProcessInstance instance = this.processBean.start("1");

        assertThat(instance).isNotNull();
        assertThat(instance).isWaitingAt(Elements.APPROVAL_REQUESTED);

        execute(job());

        assertThat(instance).isWaitingAt(Elements.LOAD_APPROVAL_REQUEST);
        complete(externalTask(),
          CamundaBpmData.builder()
            .set(ApprovalProcessBean.Variables.REQUEST, new ApprovalRequest("id", "subj", "kermit", new BigDecimal("117.81")))
            .build()
        );
        execute(job());

        assertThat(instance).isWaitingAt(Elements.USER_APPROVE_REQUEST);
        this.processBean.complete(task().getId(), CamundaBpmData.builder().set(ApprovalProcessBean.Variables.APPROVAL_DECISION, ApprovalProcessBean.Values.APPROVAL_DECISION_RETURNED).build());
        execute(job());

        ClockUtil.offset(Duration.ofMinutes(5).toMillis());

        execute(job());

        assertThat(instance).isEnded();
        assertThat(instance).hasPassedInOrder(
                Elements.APPROVAL_REQUESTED, Elements.LOAD_APPROVAL_REQUEST, Elements.DETERMINE_APPROVAL_STRATEGY, Elements.USER_APPROVE_REQUEST, Elements.REQUEST_CANCELLED
        );
    }

    @Test
    void shouldStartAndLoadAndManualAndReturnedAndResubmit() {
        ProcessInstance instance = this.processBean.start("1");

        assertThat(instance).isNotNull();
        assertThat(instance).isWaitingAt(Elements.APPROVAL_REQUESTED);

        execute(job());

        assertThat(instance).isWaitingAt(Elements.LOAD_APPROVAL_REQUEST);
        complete(externalTask(),
          CamundaBpmData.builder()
            .set(ApprovalProcessBean.Variables.REQUEST, new ApprovalRequest("id", "subj", "kermit", new BigDecimal("117.81")))
            .build()
        );
        execute(job());

        assertThat(instance).isWaitingAt(Elements.USER_APPROVE_REQUEST);
        this.processBean.complete(task().getId(), CamundaBpmData.builder().set(ApprovalProcessBean.Variables.APPROVAL_DECISION, ApprovalProcessBean.Values.APPROVAL_DECISION_RETURNED).build());
        execute(job());

        assertThat(instance).isWaitingAt(Elements.USER_AMEND_REQUEST);
        this.processBean.complete(task().getId(), CamundaBpmData.builder().set(ApprovalProcessBean.Variables.AMEND_ACTION, ApprovalProcessBean.Values.AMEND_ACTION_RESUBMITTED).build());
        execute(job());

        assertThat(instance).isWaitingAt(Elements.LOAD_APPROVAL_REQUEST);
        complete(externalTask(),
          CamundaBpmData.builder()
            .set(ApprovalProcessBean.Variables.REQUEST, new ApprovalRequest("id", "subj", "kermit", new BigDecimal("117.81")))
            .build()
        );
        execute(job());

        assertThat(instance).isWaitingAt(Elements.USER_APPROVE_REQUEST);
    }

    static ProcessEngineExtension createExtension() {
        StandaloneInMemoryTestConfiguration config = new StandaloneInMemoryTestConfiguration();
        config.getProcessEnginePlugins().add(new SpinProcessEnginePlugin());

        return ProcessEngineExtension.builder()
                .useProcessEngine(config.buildProcessEngine())
                .build();
    }

}
