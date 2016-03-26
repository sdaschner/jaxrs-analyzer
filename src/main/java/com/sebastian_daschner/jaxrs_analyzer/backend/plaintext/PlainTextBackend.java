/*
 * Copyright (C) 2015 Sebastian Daschner, sebastian-daschner.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sebastian_daschner.jaxrs_analyzer.backend.plaintext;

import com.sebastian_daschner.jaxrs_analyzer.utils.StringUtils;
import com.sebastian_daschner.jaxrs_analyzer.model.Types;
import com.sebastian_daschner.jaxrs_analyzer.backend.Backend;
import com.sebastian_daschner.jaxrs_analyzer.backend.JsonRepresentationAppender;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.*;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import static com.sebastian_daschner.jaxrs_analyzer.backend.ComparatorUtils.mapKeyComparator;
import static java.util.Comparator.comparing;

/**
 * A thread-safe backend which produces a plain text representation of the JAX-RS analysis.
 *
 * @author Sebastian Daschner
 */
public class PlainTextBackend implements Backend {

    private static final String NAME = "Plain text";
    private static final String REST_HEADER = "REST resources of ";
    private static final String TYPE_WILDCARD = "*/*";

    private final Lock lock = new ReentrantLock();
    private StringBuilder builder;
    private Resources resources;
    private String projectName;
    private String projectVersion;
    private TypeRepresentationVisitor visitor;

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
        builder.append(REST_HEADER).append(projectName).append(":\n")
                .append(projectVersion).append("\n\n");
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

    private void appendMethod(final String baseUri, final String resource, final ResourceMethod resourceMethod) {
        builder.append(resourceMethod.getMethod()).append(' ');
        if (!StringUtils.isBlank(baseUri))
            builder.append(baseUri).append('/');
        builder.append(resource).append(":\n");
    }

    private void appendRequest(final ResourceMethod resourceMethod) {
        builder.append(" Request:\n");

        if (resourceMethod.getRequestBody() != null) {
            builder.append("  Content-Type: ");
            builder.append(resourceMethod.getRequestMediaTypes().isEmpty() ? TYPE_WILDCARD : toString(resourceMethod.getRequestMediaTypes()));
            builder.append('\n');

            builder.append("  Request Body: ").append(toTypeOrCollection(resourceMethod.getRequestBody())).append('\n');
            Optional.ofNullable(resources.getTypeRepresentations().get(resourceMethod.getRequestBody())).ifPresent(r -> {
                builder.append("   ");
                r.accept(visitor);
                builder.append('\n');
            });
        } else {
            builder.append("  No body\n");
        }

        final MethodParameters parameters = resourceMethod.getMethodParameters();

        appendParams("  Path Param: ", parameters.getPathParams());
        appendParams("  Query Param: ", parameters.getQueryParams());
        appendParams("  Form Param: ", parameters.getFormParams());
        appendParams("  Header Param: ", parameters.getHeaderParams());
        appendParams("  Cookie Param: ", parameters.getCookieParams());
        appendParams("  Matrix Param: ", parameters.getMatrixParams());

        builder.append('\n');
    }

    private void appendParams(final String name, final Map<String, String> parameters) {
        parameters.entrySet().stream().sorted(mapKeyComparator()).forEach(e -> builder
                .append(name)
                .append(e.getKey())
                .append(", ")
                .append(e.getValue())
                .append('\n'));
    }

    private void appendResponse(final ResourceMethod resourceMethod) {
        builder.append(" Response:\n");

        builder.append("  Content-Type: ");
        builder.append(resourceMethod.getResponseMediaTypes().isEmpty() ? TYPE_WILDCARD : toString(resourceMethod.getResponseMediaTypes()));
        builder.append('\n');

        resourceMethod.getResponses().entrySet().stream().sorted(mapKeyComparator()).forEach(e -> {
            builder.append("  Status Codes: ").append(e.getKey()).append('\n');
            final Response response = e.getValue();
            if (!response.getHeaders().isEmpty()) {
                builder.append("   Header: ").append(response.getHeaders().stream().sorted().collect(Collectors.joining(", ")));
                builder.append('\n');
            }
            if (response.getResponseBody() != null) {
                builder.append("   Response Body: ").append(toTypeOrCollection(response.getResponseBody())).append('\n');
                Optional.ofNullable(resources.getTypeRepresentations().get(response.getResponseBody())).ifPresent(r -> {
                    builder.append("    ");
                    r.accept(visitor);
                    builder.append('\n');
                });
            }

            builder.append('\n');
        });
    }

    private void appendResourceEnd() {
        builder.append("\n");
    }

    private String toTypeOrCollection(final TypeIdentifier type) {
        final TypeRepresentation representation = resources.getTypeRepresentations().get(type);
        if (representation != null && !representation.getComponentType().equals(type) && !type.getType().equals(Types.JSON)) {
            return "Collection of " + representation.getComponentType().getType();
        }
        return type.getType();
    }

    private static String toString(final Set<String> set) {
        return set.stream().sorted().map(Object::toString).collect(Collectors.joining(", "));
    }

    @Override
    public String getName() {
        return NAME;
    }

}
