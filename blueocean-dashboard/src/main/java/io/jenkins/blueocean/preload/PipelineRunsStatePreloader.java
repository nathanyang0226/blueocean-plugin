/*
 * The MIT License
 *
 * Copyright (c) 2016, CloudBees, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package io.jenkins.blueocean.preload;

import hudson.Extension;
import hudson.model.Item;
import io.jenkins.blueocean.commons.BlueUrlTokenizer;
import io.jenkins.blueocean.commons.RESTFetchPreloader;
import io.jenkins.blueocean.commons.stapler.ModelObjectSerializer;
import io.jenkins.blueocean.rest.model.BluePipeline;
import io.jenkins.blueocean.rest.model.BlueRun;
import io.jenkins.blueocean.rest.model.BlueRunContainer;
import io.jenkins.blueocean.service.embedded.rest.BluePipelineFactory;
import jenkins.model.Jenkins;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Preload pipeline runs onto the page if the requested page is a pipeline runs page.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
@Extension
public class PipelineRunsStatePreloader extends RESTFetchPreloader {

    private static final Logger LOGGER = Logger.getLogger(PipelineRunsStatePreloader.class.getName());

    private static final int DEFAULT_LIMIT = 26;

    @Override
    protected FetchData getFetchData(@Nonnull BlueUrlTokenizer blueUrl) {
        BluePipeline pipeline = getPipeline(blueUrl);

        if (pipeline != null) {
            // It's a pipeline page. Let's prefetch the pipeline runs and add them to the page,
            // saving the frontend the overhead of requesting them.

            BlueRunContainer runsContainer = pipeline.getRuns();
            Iterator<BlueRun> runsIterator = runsContainer.iterator(0, DEFAULT_LIMIT);
            JSONArray runs = new JSONArray();

            while(runsIterator.hasNext()) {
                BlueRun blueRun = runsIterator.next();
                try {
                    runs.add(JSONObject.fromObject(ModelObjectSerializer.toJson(blueRun)));
                } catch (IOException e) {
                    LOGGER.log(Level.FINE, String.format("Unable to preload runs for Job '%s'. Run serialization error.", pipeline.getFullName()), e);
                    return null;
                }
            }

            return new FetchData(
                pipeline.getActivities().getLink().getHref() + "?start=0&limit=" + DEFAULT_LIMIT,
                runs.toString());
        }

        // Don't preload any data on the page.
        return null;
    }

    private BluePipeline getPipeline(BlueUrlTokenizer blueUrl) {
        if (addPipelineRuns(blueUrl)) {
            Jenkins jenkins = Jenkins.getInstance();
            String pipelineFullName = blueUrl.getPart(BlueUrlTokenizer.UrlPart.PIPELINE);

            try {
                Item pipelineJob = jenkins.getItemByFullName(pipelineFullName);
                return (BluePipeline) BluePipelineFactory.resolve(pipelineJob);
            } catch (Exception e) {
                LOGGER.log(Level.FINE, String.format("Unable to find Job named '%s'.", pipelineFullName), e);
                return null;
            }
        }

        return null;
    }

    private boolean addPipelineRuns(@Nonnull BlueUrlTokenizer blueUrl) {
        if (blueUrl.lastPartIs(BlueUrlTokenizer.UrlPart.PIPELINE)) {
            // e.g. /blue/organizations/jenkins/f1%2Ff3%20with%20spaces%2Ff3%20pipeline/
            return true;
        } else if (blueUrl.lastPartIs(BlueUrlTokenizer.UrlPart.PIPELINE_TAB, "activity")) {
            // e.g. /blue/organizations/jenkins/f1%2Ff3%20with%20spaces%2Ff3%20pipeline/activity/
            return true;
        }

        return false;
    }
}
