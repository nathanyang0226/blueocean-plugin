package io.jenkins.blueocean;

import hudson.Extension;
import hudson.model.RootAction;
import org.kohsuke.stapler.StaplerProxy;

import javax.inject.Inject;

/**
 * @author Kohsuke Kawaguchi
 */
@Extension
public class BlueOceanRootAction implements RootAction, StaplerProxy {
    @Inject
    BlueOceanUI app;

    @Override
    public String getIconFileName() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return null;
    }

    /**
     * This would map to /jenkins/bo/
     */
    @Override
    public String getUrlName() {
        return "bo";
    }

    @Override
    public Object getTarget() {
        return app;
    }
}
