package io.jenkins.blueocean.rest.impl.pipeline.steps;

import com.google.inject.Inject;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.workflow.steps.AbstractStepExecutionImpl;
import org.jenkinsci.plugins.workflow.steps.StepContextParameter;

import javax.annotation.Nonnull;

public class ExplodingStepExecution extends AbstractStepExecutionImpl {

    @StepContextParameter
    private transient TaskListener listener;

    @Inject(optional=true) transient ExplodingStep step;

    @Override
    public boolean start() throws Exception {
        listener.getLogger().println("In general, young salmon eat insects, invertebrates and plankton; adults eat other fish, squid, eels, and shrimp.");
        String msg = step.getMessage();
        throw new RuntimeException(msg);
    }

    @Override
    public void stop(@Nonnull Throwable cause) throws Exception {
    }
}
