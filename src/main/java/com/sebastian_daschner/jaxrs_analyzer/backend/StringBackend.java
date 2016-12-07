package com.sebastian_daschner.jaxrs_analyzer.backend;

import com.sebastian_daschner.jaxrs_analyzer.model.rest.Project;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.ResourceMethod;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.Resources;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeRepresentationVisitor;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.Comparator.comparing;

/**
 * A backend that is backed by Strings (plain text).
 *
 * @author Sebastian Daschner
 */
public abstract class StringBackend implements Backend {

    protected final Lock lock = new ReentrantLock();
    protected StringBuilder builder;
    protected Resources resources;
    protected String projectName;
    protected String projectVersion;
    protected TypeRepresentationVisitor visitor;

    private void initRender(final Project project) {
        // initialize fields
        builder = new StringBuilder();
        resources = project.getResources();
        projectName = project.getName();
        projectVersion = project.getVersion();
        visitor = new JsonRepresentationAppender(builder, resources.getTypeRepresentations());
    }

    @Override
    public byte[] render(final Project project) {
        lock.lock();
        try {
            initRender(project);

            final String output = renderInternal();

            return serialize(output);
        } finally {
            lock.unlock();
        }
    }

    private String renderInternal() {
        appendHeader();

        resources.getResources().stream().sorted().forEach(this::appendResource);

        return builder.toString();
    }

    private void appendHeader() {
        appendFirstLine();
        builder.append(projectVersion).append("\n\n");
    }

    private void appendResource(final String resource) {
        resources.getMethods(resource).stream()
                .sorted(comparing(ResourceMethod::getMethod))
                .forEach(resourceMethod -> {
                    appendMethod(resources.getBasePath(), resource, resourceMethod);
                    appendRequest(resourceMethod);
                    appendResponse(resourceMethod);
                    appendResourceEnd();
                });
    }

    protected abstract void appendFirstLine();

    protected abstract void appendMethod(String baseUri, String resource, ResourceMethod resourceMethod);

    protected abstract void appendRequest(ResourceMethod resourceMethod);

    protected abstract void appendResponse(ResourceMethod resourceMethod);

    protected void appendResourceEnd() {
    }

    private static byte[] serialize(final String output) {
        return output.getBytes();
    }

}
