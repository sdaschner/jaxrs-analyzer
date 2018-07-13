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

import com.sebastian_daschner.jaxrs_analyzer.backend.StringBackend;
import com.sebastian_daschner.jaxrs_analyzer.model.Types;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.*;
import com.sebastian_daschner.jaxrs_analyzer.utils.StringUtils;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.sebastian_daschner.jaxrs_analyzer.backend.ComparatorUtils.mapKeyComparator;
import static com.sebastian_daschner.jaxrs_analyzer.backend.ComparatorUtils.parameterComparator;
import static com.sebastian_daschner.jaxrs_analyzer.model.JavaUtils.toReadableType;

/**
 * A thread-safe backend which produces a plain text representation of the JAX-RS analysis.
 *
 * @author Sebastian Daschner
 */
public class PlainTextBackend extends StringBackend {

    private static final String NAME = "PlainText";
    private static final String REST_HEADER = "REST resources of ";
    private static final String TYPE_WILDCARD = "*/*";

    @Override
    protected void appendMethod(final String baseUri, final String resource, final ResourceMethod resourceMethod) {
        builder.append(resourceMethod.getMethod()).append(' ');
        if (!StringUtils.isBlank(baseUri))
            builder.append(baseUri).append('/');
        builder.append(resource).append(":\n");
        if (!StringUtils.isBlank(resourceMethod.getDescription()))
            builder.append("Description: ").append(resourceMethod.getDescription()).append("\n");
        if (resourceMethod.isDeprecated())
            builder.append(" Deprecated\n");
    }

    @Override
    protected void appendRequest(final ResourceMethod resourceMethod) {
        builder.append(" Request:\n");

        if (resourceMethod.getRequestBody() != null) {
            builder.append("  Content-Type: ");
            builder.append(resourceMethod.getRequestMediaTypes().isEmpty() ? TYPE_WILDCARD : toString(resourceMethod.getRequestMediaTypes()));
            builder.append('\n');

            builder.append("  Request Body: ").append(toTypeOrCollection(resourceMethod.getRequestBody())).append('\n');
            Optional.ofNullable(resources.getTypeRepresentations().get(resourceMethod.getRequestBody())).ifPresent(r -> {
                if (!prettify)
                    builder.append("   ");
                builder.append(doVisit(r));
                builder.append('\n');
            });
        } else {
            builder.append("  No body\n");
        }

        final Set<MethodParameter> parameters = resourceMethod.getMethodParameters();

        appendParams("  Path Param: ", parameters, ParameterType.PATH);
        appendParams("  Query Param: ", parameters, ParameterType.QUERY);
        appendParams("  Form Param: ", parameters, ParameterType.FORM);
        appendParams("  Header Param: ", parameters, ParameterType.HEADER);
        appendParams("  Cookie Param: ", parameters, ParameterType.COOKIE);
        appendParams("  Matrix Param: ", parameters, ParameterType.MATRIX);

        builder.append('\n');
    }

    private void appendParams(final String name, final Set<MethodParameter> parameters, final ParameterType parameterType) {
        parameters.stream().filter(p -> p.getParameterType() == parameterType)
                .sorted(parameterComparator()).forEach(p -> builder
                .append(name)
                .append(p.getName())
                .append(", ")
                .append(toReadableType(p.getType().getType()))
                // TODO add default value
                .append('\n'));
    }

    @Override
    protected void appendResponse(final ResourceMethod resourceMethod) {
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
                    if (!prettify)
                        builder.append("    ");
                    builder.append(doVisit(r));
                    builder.append('\n');
                });
            }

            builder.append('\n');
        });
    }

    @Override
    protected void appendResourceEnd() {
        builder.append("\n");
    }

    private String toTypeOrCollection(final TypeIdentifier type) {
        final TypeRepresentation representation = resources.getTypeRepresentations().get(type);
        if (representation != null && !representation.getComponentType().equals(type) && !type.getType().equals(Types.JSON)) {
            return "Collection of " + toReadableComponentType(representation.getComponentType());
        }
        return toReadableType(type.getType());
    }

    private static String toString(final Set<String> set) {
        return set.stream().sorted().map(Object::toString).collect(Collectors.joining(", "));
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected void appendFirstLine() {
        builder.append(REST_HEADER).append(projectName).append(":\n");
    }

}
