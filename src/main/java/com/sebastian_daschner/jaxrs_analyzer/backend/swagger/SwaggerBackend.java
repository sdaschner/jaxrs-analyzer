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
import com.sebastian_daschner.jaxrs_analyzer.model.rest.MethodParameter;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.ParameterType;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.Project;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.ResourceMethod;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.Resources;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;
import javax.json.stream.JsonGenerator;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.StringWriter;
import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

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

    public static final String SWAGGER_SCHEMES = "swaggerSchemes";
    public static final String RENDER_SWAGGER_TAGS = "renderSwaggerTags";
    public static final String SWAGGER_TAGS_PATH_OFFSET = "swaggerTagsPathOffset";
    public static final String DOMAIN = "domain";

    private static final String NAME = "Swagger";
    private static final String SWAGGER_VERSION = "2.0";

    private final Lock lock = new ReentrantLock();
    private final SwaggerOptions options;
    private Resources resources;
    private JsonObjectBuilder builder;
    private SchemaBuilder schemaBuilder;
    private String projectName;
    private String projectVersion;

    public SwaggerBackend() {
        this(new SwaggerOptions());
    }

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

        try (final StringWriter writer = new StringWriter()) {
            final Map<String, ?> config = singletonMap(JsonGenerator.PRETTY_PRINTING, true);
            final JsonWriter jsonWriter = Json.createWriterFactory(config).createWriter(writer);
            jsonWriter.write(builder.build());
            jsonWriter.close();

            return writer.toString();
        } catch (IOException e) {
            throw new RuntimeException("Could not write Swagger output", e);
        }
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
        final Set<MethodParameter> parameters = method.getMethodParameters();
        final JsonArrayBuilder parameterBuilder = Json.createArrayBuilder();

        buildParameters(parameters, ParameterType.PATH, parameterBuilder);
        buildParameters(parameters, ParameterType.HEADER, parameterBuilder);
        buildParameters(parameters, ParameterType.QUERY, parameterBuilder);
        buildParameters(parameters, ParameterType.FORM, parameterBuilder);

        if (method.getRequestBody() != null) {
            parameterBuilder.add(Json.createObjectBuilder().add("name", "body").add("in", "body").add("required", true)
                    .add("schema", schemaBuilder.build(method.getRequestBody())));
        }
        return parameterBuilder;
    }

    private void buildParameters(final Set<MethodParameter> parameters, final ParameterType parameterType, final JsonArrayBuilder builder) {
        parameters.stream().filter(p -> p.getParameterType() == parameterType)
                .sorted(parameterComparator())
                .forEach(e -> {
                    final String swaggerParameterType = getSwaggerParameterType(parameterType);
                    if (swaggerParameterType != null)
                        builder.add(schemaBuilder.build(e.getType())
                                .add("name", e.getName())
                                .add("in", swaggerParameterType)
                                .add("required", e.getDefaultValue() == null));
                });
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



    private SwaggerScheme extractSwaggerScheme(final String scheme) {
        switch (scheme.toLowerCase()) {
            case "http":
                return SwaggerScheme.HTTP;
            case "https":
                return SwaggerScheme.HTTPS;
            case "ws":
                return SwaggerScheme.WS;
            case "wss":
                return SwaggerScheme.WSS;
            default:
                throw new IllegalArgumentException("Unknown swagger scheme " + scheme);
        }
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void configure(Map<String, String> config) {
        if (config.containsKey(SWAGGER_TAGS_PATH_OFFSET)) {
            Integer swaggerTagsPathOffset = Integer.parseInt(config.get(SWAGGER_TAGS_PATH_OFFSET));

            if (swaggerTagsPathOffset < 0) {
                System.err.println("Please provide positive integer number for option --swaggerTagsPathOffset\n");
                throw new IllegalArgumentException("Please provide positive integer number for option --swaggerTagsPathOffset");
            }

            this.options.setTagsPathOffset(swaggerTagsPathOffset);
        }

        if(config.containsKey(DOMAIN)) {
            this.options.setDomain(config.get(DOMAIN));
        }

        if (config.containsKey(SWAGGER_SCHEMES)) {
            this.options.setSchemes(extractSwaggerSchemes(config.get(SWAGGER_SCHEMES)));
        }

        if (config.containsKey(RENDER_SWAGGER_TAGS)) {
            this.options.setRenderTags(Boolean.parseBoolean(config.get(RENDER_SWAGGER_TAGS)));
        }
    }


    private Set<SwaggerScheme> extractSwaggerSchemes(final String schemes) {
        return Stream.of(schemes.split(","))
                .map(this::extractSwaggerScheme)
                .collect(() -> EnumSet.noneOf(SwaggerScheme.class), Set::add, Set::addAll);
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

}
