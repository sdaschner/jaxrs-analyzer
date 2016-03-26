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

package com.sebastian_daschner.jaxrs_analyzer.backend.swagger;

import com.sebastian_daschner.jaxrs_analyzer.backend.Backend;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.MethodParameters;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.Project;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.ResourceMethod;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.Resources;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.core.Response;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.sebastian_daschner.jaxrs_analyzer.backend.ComparatorUtils.mapKeyComparator;
import static java.util.Comparator.comparing;

/**
 * A backend which produces a Swagger JSON representation of the resources.
 *
 * @author Sebastian Daschner
 */
public class SwaggerBackend implements Backend {

    private static final String NAME = "Swagger";
    private static final String SWAGGER_VERSION = "2.0";

    private final Lock lock = new ReentrantLock();
    private final SwaggerOptions options;
    private Resources resources;
    private JsonObjectBuilder builder;
    private SchemaBuilder schemaBuilder;
    private String projectName;
    private String projectVersion;

    SwaggerBackend(final SwaggerOptions options) {
        this.options = options;
    }

    @Override
    public String render(final Project project) {
        lock.lock();
        try {
            // initialize fields
            builder = Json.createObjectBuilder();
            resources = project.getResources();
            projectName = project.getName();
            projectVersion = project.getVersion();
            schemaBuilder = new SchemaBuilder(resources.getTypeRepresentations());

            return renderInternal();
        } finally {
            lock.unlock();
        }
    }

    private String renderInternal() {
        appendHeader();
        appendPaths();
        appendDefinitions();

        return builder.build().toString();
    }

    private void appendHeader() {
        builder.add("swagger", SWAGGER_VERSION).add("info", Json.createObjectBuilder()
                .add("version", projectVersion).add("title", projectName))
                .add("host", options.getDomain()).add("basePath", '/' + resources.getBasePath())
                .add("schemes", options.getSchemes().stream().map(Enum::name).map(String::toLowerCase).sorted()
                        .collect(Json::createArrayBuilder, JsonArrayBuilder::add, JsonArrayBuilder::add));
        if (options.isRenderTags()) {
            final JsonArrayBuilder tags = Json.createArrayBuilder();
            resources.getResources().stream()
                    .map(this::extractTag).filter(Objects::nonNull)
                    .distinct().sorted().forEach(tags::add);
            builder.add("tags", tags);
        }
    }

    private String extractTag(final String s) {
        final int offset = options.getTagsPathOffset();
        final String[] parts = s.split("/");

        if (parts.length > offset && !parts[offset].contains("{")) {
            return parts[offset];
        }
        return null;
    }

    private void appendPaths() {
        final JsonObjectBuilder paths = Json.createObjectBuilder();
        resources.getResources().stream().sorted().forEach(s -> paths.add('/' + s, buildPathDefinition(s)));
        builder.add("paths", paths);
    }

    private JsonObjectBuilder buildPathDefinition(final String s) {
        final JsonObjectBuilder methods = Json.createObjectBuilder();
        resources.getMethods(s).stream()
                .sorted(comparing(ResourceMethod::getMethod))
                .forEach(m -> methods.add(m.getMethod().toString().toLowerCase(), buildForMethod(m, s)));
        return methods;
    }

    private JsonObjectBuilder buildForMethod(final ResourceMethod method, final String s) {
        final JsonArrayBuilder consumes = Json.createArrayBuilder();
        method.getRequestMediaTypes().stream().sorted().forEach(consumes::add);

        final JsonArrayBuilder produces = Json.createArrayBuilder();
        method.getResponseMediaTypes().stream().sorted().forEach(produces::add);

        final JsonObjectBuilder methodDescription = Json.createObjectBuilder().add("consumes", consumes).add("produces", produces)
                .add("parameters", buildParameters(method)).add("responses", buildResponses(method));

        if (options.isRenderTags())
            Optional.ofNullable(extractTag(s)).ifPresent(t -> methodDescription.add("tags", Json.createArrayBuilder().add(t)));

        return methodDescription;
    }

    private JsonArrayBuilder buildParameters(final ResourceMethod method) {
        final MethodParameters parameters = method.getMethodParameters();
        final JsonArrayBuilder parameterBuilder = Json.createArrayBuilder();

        parameters.getPathParams().entrySet().stream().sorted(mapKeyComparator()).forEach(e -> parameterBuilder.add(buildParameter(e, "path")));
        parameters.getHeaderParams().entrySet().stream().sorted(mapKeyComparator()).forEach(e -> parameterBuilder.add(buildParameter(e, "header")));
        parameters.getQueryParams().entrySet().stream().sorted(mapKeyComparator()).forEach(e -> parameterBuilder.add(buildParameter(e, "query")));
        parameters.getFormParams().entrySet().stream().sorted(mapKeyComparator()).forEach(e -> parameterBuilder.add(buildParameter(e, "formData")));

        if (method.getRequestBody() != null) {
            parameterBuilder.add(Json.createObjectBuilder().add("name", "body").add("in", "body").add("required", true)
                    .add("schema", schemaBuilder.build(method.getRequestBody())));
        }
        return parameterBuilder;
    }

    private JsonObjectBuilder buildParameter(final Map.Entry<String, String> entry, final String context) {
        return Json.createObjectBuilder()
                .add("name", entry.getKey()).add("in", context)
                .add("required", true).add("type", SwaggerUtils.toSwaggerType(entry.getValue()).toString());
    }

    private JsonObjectBuilder buildResponses(final ResourceMethod method) {
        final JsonObjectBuilder responses = Json.createObjectBuilder();

        method.getResponses().entrySet().stream().sorted(mapKeyComparator()).forEach(e -> {
            final JsonObjectBuilder headers = Json.createObjectBuilder();
            e.getValue().getHeaders().stream().sorted().forEach(h -> headers.add(h, Json.createObjectBuilder().add("type", "string")));

            final JsonObjectBuilder response = Json.createObjectBuilder()
                    .add("description", Optional.ofNullable(Response.Status.fromStatusCode(e.getKey())).map(Response.Status::getReasonPhrase).orElse(""))
                    .add("headers", headers);

            if (e.getValue().getResponseBody() != null) {
                final JsonObject schema = schemaBuilder.build(e.getValue().getResponseBody());
                if (!schema.isEmpty())
                    response.add("schema", schema);
            }

            responses.add(e.getKey().toString(), response);
        });

        return responses;
    }

    private void appendDefinitions() {
        builder.add("definitions", schemaBuilder.getDefinitions());
    }

    @Override
    public String getName() {
        return NAME;
    }

}
