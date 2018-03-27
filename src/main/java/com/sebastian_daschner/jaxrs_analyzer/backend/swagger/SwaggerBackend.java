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
import com.sebastian_daschner.jaxrs_analyzer.model.rest.*;
import com.sebastian_daschner.jaxrs_analyzer.utils.StringUtils;

import javax.json.*;
import javax.json.stream.JsonGenerator;
import javax.ws.rs.core.Response;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import static com.sebastian_daschner.jaxrs_analyzer.backend.ComparatorUtils.mapKeyComparator;
import static com.sebastian_daschner.jaxrs_analyzer.backend.ComparatorUtils.parameterComparator;
import static java.util.Collections.singletonMap;
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
    private final SwaggerOptions options = new SwaggerOptions();

    private Resources resources;
    private JsonObjectBuilder builder;
    private SchemaBuilder schemaBuilder;
    private String projectName;
    private String projectVersion;

    @Override
    public void configure(final Map<String, String> config) {
        options.configure(config);
    }

    @Override
    public byte[] render(final Project project) {
        lock.lock();
        try {
            // initialize fields
            builder = Json.createObjectBuilder();
            resources = project.getResources();
            projectName = project.getName();
            projectVersion = project.getVersion();
            schemaBuilder = new SchemaBuilder(resources.getTypeRepresentations());

            final JsonObject output = modifyJson(renderInternal());

            return serialize(output);
        } finally {
            lock.unlock();
        }
    }

    private JsonObject modifyJson(final JsonObject json) {
        if (options.getJsonPatch() == null)
            return json;
        return options.getJsonPatch().apply(json);
    }

    private JsonObject renderInternal() {
        appendHeader();
        appendPaths();
        appendDefinitions();

        return builder.build();
    }

    private void appendHeader() {
        renderHeader();
        renderTags();
    }

    private void renderHeader() {
        builder.add("swagger", SWAGGER_VERSION).add("info", Json.createObjectBuilder()
                .add("version", projectVersion).add("title", projectName))
                .add("host", options.getDomain() == null ? "" : options.getDomain()).add("basePath", (options.getDomain() != null && !"".equals(options.getDomain().trim()) ? '/' : '/' + projectName + '/') + resources.getBasePath())
                .add("schemes", options.getSchemes().stream().map(Enum::name).map(String::toLowerCase).sorted()
                        .collect(Json::createArrayBuilder, JsonArrayBuilder::add, JsonArrayBuilder::add).build());
    }

    private void renderTags() {
        if (options.isRenderTags()) {
            final JsonArrayBuilder tags = Json.createArrayBuilder();
            resources.getResources().stream()
                    .map(this::extractTag).filter(Objects::nonNull)
                    .distinct().sorted()
                    .map(tag -> Json.createObjectBuilder().add("name", tag))
                    .forEach(tags::add);
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
        consolidateMultipleMethodsForSamePath(s)
                .values().stream()
                .sorted(comparing(ResourceMethod::getMethod))
                .forEach(m ->
                        methods.add(m.getMethod().toString().toLowerCase(), buildForMethod(m, s)));
        return methods;
    }

    private Map<String, ResourceMethod> consolidateMultipleMethodsForSamePath(String s) {
        return resources.getMethods(s).stream().collect(
                Collectors.groupingBy(m->m.getMethod().toString().toLowerCase(),
                        Collectors.reducing(new ResourceMethod(), ResourceMethod::combine))
        );
    }

    private JsonObjectBuilder buildForMethod(final ResourceMethod method, final String s) {
        final JsonArrayBuilder consumes = Json.createArrayBuilder();
        method.getRequestMediaTypes().stream().sorted().forEach(consumes::add);

        final JsonArrayBuilder produces = Json.createArrayBuilder();
        method.getResponseMediaTypes().stream().sorted().forEach(produces::add);

        final JsonObjectBuilder builder = Json.createObjectBuilder();

        if (method.getDescription() != null)
            builder.add("description", method.getDescription());

        builder.add("consumes", consumes).add("produces", produces)
                .add("parameters", buildParameters(method)).add("responses", buildResponses(method));

        if (method.isDeprecated())
            builder.add("deprecated", true);

        if (options.isRenderTags())
            Optional.ofNullable(extractTag(s)).ifPresent(t -> builder.add("tags", Json.createArrayBuilder().add(t)));

        return builder;
    }

    private JsonArrayBuilder buildParameters(final ResourceMethod method) {
        final Set<MethodParameter> parameters = method.getMethodParameters();
        final JsonArrayBuilder parameterBuilder = Json.createArrayBuilder();

        buildParameters(parameters, ParameterType.PATH, parameterBuilder);
        buildParameters(parameters, ParameterType.HEADER, parameterBuilder);
        buildParameters(parameters, ParameterType.QUERY, parameterBuilder);
        buildParameters(parameters, ParameterType.FORM, parameterBuilder);

        if (method.getRequestBody() != null) {
            final JsonObjectBuilder requestBuilder = Json.createObjectBuilder()
                    .add("name", "body")
                    .add("in", "body")
                    .add("required", true)
                    .add("schema", schemaBuilder.build(method.getRequestBody()));
            if (!StringUtils.isBlank(method.getRequestBodyDescription()))
                requestBuilder.add("description", method.getRequestBodyDescription());
            parameterBuilder.add(requestBuilder);
        }
        return parameterBuilder;
    }

    private void buildParameters(final Set<MethodParameter> parameters, final ParameterType parameterType, final JsonArrayBuilder builder) {
        parameters.stream().filter(p -> p.getParameterType() == parameterType)
                .sorted(parameterComparator())
                .forEach(e -> {
                    final String swaggerParameterType = getSwaggerParameterType(parameterType);
                    if (swaggerParameterType != null) {
                        final JsonObjectBuilder paramBuilder = schemaBuilder.build(e.getType())
                                .add("name", e.getName())
                                .add("in", swaggerParameterType)
                                .add("required", e.getDefaultValue() == null);
                        if (!StringUtils.isBlank(e.getDescription())) {
                            paramBuilder.add("description", e.getDescription());
                        }
                        if (!StringUtils.isBlank(e.getDefaultValue())) {
                            paramBuilder.add("default", e.getDefaultValue());
                        }
                        builder.add(paramBuilder);
                    }
                });
    }

    private JsonObjectBuilder buildResponses(final ResourceMethod method) {
        final JsonObjectBuilder responses = Json.createObjectBuilder();

        method.getResponses().entrySet().stream().sorted(mapKeyComparator()).forEach(e -> {
            final JsonObjectBuilder headers = Json.createObjectBuilder();
            e.getValue().getHeaders().stream().sorted().forEach(h -> headers.add(h, Json.createObjectBuilder().add("type", "string")));

            final JsonObjectBuilder response = Json.createObjectBuilder()
                    .add("description", e.getValue().getDescription() != null ? e.getValue().getDescription() : Optional.ofNullable(Response.Status.fromStatusCode(e.getKey())).map(Response.Status::getReasonPhrase).orElse(""))
                    .add("headers", headers);

            if (e.getValue().getResponseBody() != null) {
                final JsonObject schema = schemaBuilder.build(e.getValue().getResponseBody()).build();
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

    private static String getSwaggerParameterType(final ParameterType parameterType) {
        switch (parameterType) {
            case QUERY:
                return "query";
            case PATH:
                return "path";
            case HEADER:
                return "header";
            case FORM:
                return "formData";
            default:
                // TODO handle others (possible w/ Swagger?)
                return null;
        }
    }

    private static byte[] serialize(final JsonObject jsonObject) {
        try (final ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            final Map<String, ?> config = singletonMap(JsonGenerator.PRETTY_PRINTING, true);
            final JsonWriter jsonWriter = Json.createWriterFactory(config).createWriter(output);
            jsonWriter.write(jsonObject);
            jsonWriter.close();

            return output.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Could not write Swagger output", e);
        }
    }

}
