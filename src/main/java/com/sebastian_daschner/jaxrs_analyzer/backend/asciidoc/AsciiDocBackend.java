package com.sebastian_daschner.jaxrs_analyzer.backend.asciidoc;

import com.sebastian_daschner.jaxrs_analyzer.backend.Backend;
import com.sebastian_daschner.jaxrs_analyzer.backend.JsonRepresentationAppender;
import com.sebastian_daschner.jaxrs_analyzer.model.Types;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.*;
import com.sebastian_daschner.jaxrs_analyzer.utils.StringUtils;

import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import static com.sebastian_daschner.jaxrs_analyzer.backend.ComparatorUtils.mapKeyComparator;
import static com.sebastian_daschner.jaxrs_analyzer.backend.ComparatorUtils.parameterComparator;
import static com.sebastian_daschner.jaxrs_analyzer.model.JavaUtils.toReadableType;

/**
 * A backend implementation which produces an AsciiDoc representation of the JAX-RS project.
 *
 * @author Sebastian Daschner
 */
public class AsciiDocBackend implements Backend {

    private static final String NAME = "AsciiDoc";
    private static final String DOCUMENT_TITLE = "= REST resources of ";
    private static final String TYPE_WILDCARD = "\\*/*";

    private final Lock lock = new ReentrantLock();
    private StringBuilder builder;
    private Resources resources;
    private String projectName;
    private String projectVersion;
    private TypeRepresentationVisitor visitor;

    public AsciiDocBackend() {
        super();
    }

    @Override
    public String render(final Project project) {
        lock.lock();
        try {
            // initialize fields
            builder = new StringBuilder();
            resources = project.getResources();
            projectName = project.getName();
            projectVersion = project.getVersion();
            visitor = new JsonRepresentationAppender(builder, resources.getTypeRepresentations());

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
        builder.append(DOCUMENT_TITLE).append(projectName).append('\n')
                .append(projectVersion).append("\n\n");
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

            builder.append("*Request Body*: (").append(toTypeOrCollection(resourceMethod.getRequestBody())).append(") + \n");
            Optional.ofNullable(resources.getTypeRepresentations().get(resourceMethod.getRequestBody())).ifPresent(r -> {
                builder.append('`');
                r.accept(visitor);
                builder.append("` + \n");
            });
        } else {
            builder.append("_No body_ + \n");
        }

        final Set<MethodParameter> parameters = resourceMethod.getMethodParameters();

        appendParams("Path Param", parameters, ParameterType.PATH);
        appendParams("Query Param", parameters, ParameterType.QUERY);
        appendParams("Form Param", parameters, ParameterType.FORM);
        appendParams("Header Param", parameters, ParameterType.HEADER);
        appendParams("Cookie Param", parameters, ParameterType.COOKIE);
        appendParams("Matrix Param", parameters, ParameterType.MATRIX);

        builder.append('\n');
    }

    private void appendParams(final String name, final Set<MethodParameter> parameters, final ParameterType parameterType) {
        parameters.stream().filter(p -> p.getParameterType() == parameterType)
                .sorted(parameterComparator()).forEach(p -> builder
                .append('*')
                .append(name)
                .append("*: `")
                .append(p.getName())
                .append("`, `")
                .append(toReadableType(p.getType().getType()))
                // TODO add default value
                .append("` + \n"));
    }

    private void appendResponse(final ResourceMethod resourceMethod) {
        builder.append("=== Response\n");

        builder.append("*Content-Type*: `");
        builder.append(resourceMethod.getResponseMediaTypes().isEmpty() ? TYPE_WILDCARD : toString(resourceMethod.getResponseMediaTypes()));
        builder.append("`\n\n");

        resourceMethod.getResponses().entrySet().stream().sorted(mapKeyComparator()).forEach(e -> {
            builder.append("==== `").append(e.getKey()).append(' ')
                    .append(javax.ws.rs.core.Response.Status.fromStatusCode(e.getKey()).getReasonPhrase()).append("`\n");
            final Response response = e.getValue();
            response.getHeaders().forEach(h -> builder.append("*Header*: `").append(h).append("` + \n"));

            if (response.getResponseBody() != null) {
                builder.append("*Response Body*: ").append('(').append(toTypeOrCollection(response.getResponseBody())).append(") + \n");
                Optional.ofNullable(resources.getTypeRepresentations().get(response.getResponseBody())).ifPresent(r -> {
                    builder.append('`');
                    r.accept(visitor);
                    builder.append("` + \n");
                });
            }

            builder.append('\n');
        });
    }

    private String toTypeOrCollection(final TypeIdentifier type) {
        final TypeRepresentation representation = resources.getTypeRepresentations().get(type);
        if (representation != null && !representation.getComponentType().equals(type) && !type.getType().equals(Types.JSON)) {
            return "Collection of `" + toReadableType(representation.getComponentType().getType()) + '`';
        }
        return '`' + toReadableType(type.getType()) + '`';
    }

    private static String toString(final Set<String> set) {
        return set.stream().sorted().map(Object::toString).collect(Collectors.joining(", "));
    }

    @Override
    public String getName() {
        return NAME;
    }

}
