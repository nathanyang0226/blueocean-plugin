package io.jenkins.blueocean.rest.impl.pipeline.steps;

import hudson.Extension;
import org.jenkinsci.plugins.workflow.steps.AbstractStepDescriptorImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractStepImpl;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;

public class ExplodingStep extends AbstractStepImpl {
    private final String message;

    @DataBoundConstructor
    public ExplodingStep(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Extension
    public static class DescriptorImpl extends AbstractStepDescriptorImpl {

        public DescriptorImpl() {
            super(ExplodingStepExecution.class);
        }

        @Override
        public String getFunctionName() {
            return "explode";
        }

        @Nonnull
        @Override
        public String getDisplayName() {
            return "Explode";
        }
    }
}
