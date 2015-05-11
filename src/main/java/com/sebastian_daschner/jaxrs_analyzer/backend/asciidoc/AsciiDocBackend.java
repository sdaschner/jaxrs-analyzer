package com.sebastian_daschner.jaxrs_analyzer.backend.asciidoc;

import com.sebastian_daschner.jaxrs_analyzer.analysis.utils.StringUtils;
import com.sebastian_daschner.jaxrs_analyzer.backend.Backend;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.MethodParameters;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.ResourceMethod;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.Resources;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.Response;

import javax.json.JsonValue;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * A backend implementation which produces an AsciiDoc representation of the JAX-RS project.
 *
 * @author Sebastian Daschner
 */
public class AsciiDocBackend implements Backend {

    private static final String DOCUMENT_TITLE = "= REST resources\n";
    private static final String TYPE_WILDCARD = "\\*/*";

    private final Lock lock = new ReentrantLock();
    private StringBuilder builder;
    private Resources resources;

    @Override
    public String render(final Resources resources) {
        lock.lock();
        try {
            // initialize fields
            builder = new StringBuilder();
            this.resources = resources;

            return renderInternal();
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
        builder.append(DOCUMENT_TITLE);
        // TODO take correct version
        builder.append("v0.1\n\n");
    }

    private void appendResource(final String resource) {
        resources.getMethods(resource).stream()
                .sorted(Comparator.comparing(ResourceMethod::getMethod))
                .forEach(resourceMethod -> {
                    appendMethod(resources.getBasePath(), resource, resourceMethod);
                    appendRequest(resourceMethod);
                    appendResponse(resourceMethod);
                });
    }

    private void appendMethod(final String baseUri, final String resource, final ResourceMethod resourceMethod) {
        builder.append("== `").append(resourceMethod.getMethod()).append(' ');
        if (!StringUtils.isBlank(baseUri))
            builder.append(baseUri).append('/');
        builder.append(resource).append("`\n\n");
    }

    private void appendRequest(final ResourceMethod resourceMethod) {
        builder.append("=== Request\n");

        if (resourceMethod.getRequestBody() != null) {
            builder.append("*Content-Type*: `");
            builder.append(resourceMethod.getRequestMediaTypes().isEmpty() ? TYPE_WILDCARD : toString(resourceMethod.getRequestMediaTypes()));
            builder.append("` + \n");

            builder.append("*Request Body*: (`").append(resourceMethod.getRequestBody().getType()).append("`) + \n");
            resourceMethod.getRequestBody().getRepresentations().entrySet().stream()
                    .forEach(e -> builder.append('`').append(e.getKey()).append("`: `").append(e.getValue()).append("` + \n"));
        } else {
            builder.append("_No body_ + \n");
        }

        final MethodParameters parameters = resourceMethod.getMethodParameters();

        appendParams("*Path Param*: ", parameters.getPathParams());
        appendParams("*Query Param*: ", parameters.getQueryParams());
        appendParams("*Form Param*: ", parameters.getFormParams());
        appendParams("*Header Param*: ", parameters.getHeaderParams());
        appendParams("*Cookie Param*: ", parameters.getCookieParams());
        appendParams("*Matrix Param*: ", parameters.getMatrixParams());

        builder.append('\n');
    }

    private void appendParams(final String name, final Map<String, String> parameters) {
        for (final Map.Entry<String, String> entry : parameters.entrySet()) {
            builder.append(name);
            builder.append(entry.getKey());
            builder.append(", ");
            builder.append(entry.getValue());
            builder.append(" + \n");
        }
    }

    private void appendResponse(final ResourceMethod resourceMethod) {
        builder.append("=== Response\n");

        builder.append("*Content-Type*: `");
        builder.append(resourceMethod.getResponseMediaTypes().isEmpty() ? TYPE_WILDCARD : toString(resourceMethod.getResponseMediaTypes()));
        builder.append("`\n\n");

        resourceMethod.getResponses().entrySet().stream().forEach(e -> {
            builder.append("==== `").append(e.getKey()).append(' ')
                    .append(javax.ws.rs.core.Response.Status.fromStatusCode(e.getKey()).getReasonPhrase()).append("`\n");
            final Response response = e.getValue();
            response.getHeaders().forEach(h -> builder.append("*Header*: `").append(h).append("` + \n"));

            if (response.getResponseBody() != null) {
                builder.append("*Response Body*: ").append("(`").append(response.getResponseBody().getType()).append("`) + \n");
                // TODO remove JSON filtering
                response.getResponseBody().getRepresentations().entrySet().stream().filter(r -> r.getValue() instanceof JsonValue)
                        .forEach(r -> builder.append('`').append(r.getKey()).append("`: `").append(r.getValue()).append("` + \n"));
            }

            builder.append('\n');
        });
    }

    private static String toString(final Set<?> set) {
        return set.stream().map(Object::toString).collect(Collectors.joining(", "));
    }

}
