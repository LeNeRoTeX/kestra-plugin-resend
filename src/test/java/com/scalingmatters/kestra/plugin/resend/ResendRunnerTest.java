package com.scalingmatters.kestra.plugin.resend;

import io.kestra.core.junit.annotations.ExecuteFlow;
import io.kestra.core.junit.annotations.KestraTest;
import org.junit.jupiter.api.Test;
import io.kestra.core.models.executions.Execution;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * This test will start Kestra, load the flow located in `src/test/resources/flows/example.yaml`, and execute it.
 * The Kestra configuration file is in `src/test/resources/application.yml`, it configures the in-memory backend for tests.
 */
@KestraTest(startRunner = true) // This annotation starts an embedded Kestra for tests
class ResendRunnerTest {

    @Test
    @ExecuteFlow("flows/resend.yaml")
    void resend(Execution execution) {
        assertThat(execution.getTaskRunList(), hasSize(2));
        assertThat(((Map<String, Object>)execution.getTaskRunList().get(1).getOutputs().get("result")).get("id"), notNullValue());
    }
}
