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
import com.sebastian_daschner.jaxrs_analyzer.builder.ResourceMethodBuilder;
import com.sebastian_daschner.jaxrs_analyzer.builder.ResourcesBuilder;
import com.sebastian_daschner.jaxrs_analyzer.builder.ResponseBuilder;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.*;
import com.sebastian_daschner.jaxrs_analyzer.model.types.Type;
import com.sebastian_daschner.jaxrs_analyzer.model.types.Types;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class SwaggerBackendTest {

    private final Backend cut;
    private final Resources resources;
    private final String expectedOutput;
    private final Boolean renderSwaggerTags;
    private final Integer swaggerTagsPathOffset;

    public SwaggerBackendTest(final Resources resources, final String expectedOutput,
            final Boolean renderSwaggerTags, final Integer swaggerTagsPathOffset) {
        cut = new SwaggerBackend();
        this.resources = resources;
        this.expectedOutput = expectedOutput;
        this.renderSwaggerTags = renderSwaggerTags;
        this.swaggerTagsPathOffset = swaggerTagsPathOffset;
    }

    @Test
    public void test() {
        final Project project = new Project("project name", "1.0", "domain.tld", resources, renderSwaggerTags, swaggerTagsPathOffset);
        final String actualOutput = cut.render(project);

        assertEquals(expectedOutput, actualOutput);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        final Collection<Object[]> data = new LinkedList<>();
        final TypeIdentifier stringIdentifier = TypeIdentifier.ofType(Types.STRING);
        final TypeIdentifier intIdentifier = TypeIdentifier.ofType(Types.PRIMITIVE_INT);

        TypeIdentifier identifier;
        Map<String, TypeIdentifier> properties = new HashMap<>();

        add(data, ResourcesBuilder.withBase("rest")
                        .andResource("res1", ResourceMethodBuilder.withMethod(HttpMethod.GET)
                                .andResponse(200, ResponseBuilder.withResponseBody(TypeIdentifier.ofType(Types.STRING)).andHeaders("Location").build()).build()).build(),
                "{\"swagger\":\"2.0\",\"info\":{\"version\":\"1.0\",\"title\":\"project name\"},\"host\":\"domain.tld\",\"basePath\":\"/rest\",\"schemes\":[\"http\"],\"paths\":{\"/res1\":{\"get\":{\"consumes\":[],\"produces\":[],\"parameters\":[],\"responses\":{\"200\":{\"description\":\"OK\",\"headers\":{\"Location\":{\"type\":\"string\"}},\"schema\":{\"type\":\"string\"}}}}}},\"definitions\":{}}");

        identifier = TypeIdentifier.ofDynamic();
        properties.put("key", stringIdentifier);
        properties.put("another", intIdentifier);
        add(data, ResourcesBuilder.withBase("rest").andTypeRepresentation(identifier, TypeRepresentation.ofConcrete(identifier, properties))
                        .andResource("res2", ResourceMethodBuilder.withMethod(HttpMethod.GET)
                                .andResponse(200, ResponseBuilder.withResponseBody(identifier).build()).build()).build(),
                "{\"swagger\":\"2.0\",\"info\":{\"version\":\"1.0\",\"title\":\"project name\"},\"host\":\"domain.tld\",\"basePath\":\"/rest\",\"schemes\":[\"http\"],\"paths\":{\"/res2\":{\"get\":{\"consumes\":[],\"produces\":[],\"parameters\":[],\"responses\":{\"200\":{\"description\":\"OK\",\"headers\":{},\"schema\":{\"$ref\":\"#/definitions/JsonObject\"}}}}}},\"definitions\":{\"JsonObject\":{\"properties\":{\"another\":{\"type\":\"integer\"},\"key\":{\"type\":\"string\"}}}}}");

        identifier = TypeIdentifier.ofDynamic();
        properties = new HashMap<>();
        properties.put("key", stringIdentifier);
        properties.put("another", intIdentifier);
        add(data, ResourcesBuilder.withBase("rest")
                        .andTypeRepresentation(identifier, TypeRepresentation.ofCollection(identifier, TypeRepresentation.ofConcrete(TypeIdentifier.ofDynamic(), properties)))
                        .andResource("res3", ResourceMethodBuilder.withMethod(HttpMethod.GET)
                                .andResponse(200, ResponseBuilder.withResponseBody(identifier).build()).build()).build(),
                "{\"swagger\":\"2.0\",\"info\":{\"version\":\"1.0\",\"title\":\"project name\"},\"host\":\"domain.tld\",\"basePath\":\"/rest\",\"schemes\":[\"http\"],\"paths\":{\"/res3\":{\"get\":{\"consumes\":[],\"produces\":[],\"parameters\":[],\"responses\":{\"200\":{\"description\":\"OK\",\"headers\":{},\"schema\":{\"type\":\"array\",\"items\":{\"$ref\":\"#/definitions/JsonObject\"}}}}}}},\"definitions\":{\"JsonObject\":{\"properties\":{\"another\":{\"type\":\"integer\"},\"key\":{\"type\":\"string\"}}}}}");

        identifier = TypeIdentifier.ofDynamic();
        add(data, ResourcesBuilder.withBase("rest")
                        .andTypeRepresentation(identifier, TypeRepresentation.ofCollection(identifier, TypeRepresentation.ofConcrete(stringIdentifier)))
                        .andResource("res4", ResourceMethodBuilder.withMethod(HttpMethod.GET)
                                .andResponse(200, ResponseBuilder.withResponseBody(identifier).build()).build()).build(),
                "{\"swagger\":\"2.0\",\"info\":{\"version\":\"1.0\",\"title\":\"project name\"},\"host\":\"domain.tld\",\"basePath\":\"/rest\",\"schemes\":[\"http\"],\"paths\":{\"/res4\":{\"get\":{\"consumes\":[],\"produces\":[],\"parameters\":[],\"responses\":{\"200\":{\"description\":\"OK\",\"headers\":{},\"schema\":{\"type\":\"array\",\"items\":{\"type\":\"string\"}}}}}}},\"definitions\":{}}");

        identifier = TypeIdentifier.ofDynamic();
        properties = new HashMap<>();
        properties.put("key", stringIdentifier);
        add(data, ResourcesBuilder.withBase("rest")
                        .andTypeRepresentation(identifier, TypeRepresentation.ofCollection(identifier, TypeRepresentation.ofConcrete(identifier, properties)))
                        .andResource("res5", ResourceMethodBuilder.withMethod(HttpMethod.GET)
                                .andResponse(200, ResponseBuilder.withResponseBody(identifier).build()).build()).build(),
                "{\"swagger\":\"2.0\",\"info\":{\"version\":\"1.0\",\"title\":\"project name\"},\"host\":\"domain.tld\",\"basePath\":\"/rest\",\"schemes\":[\"http\"],\"paths\":{\"/res5\":{\"get\":{\"consumes\":[],\"produces\":[],\"parameters\":[],\"responses\":{\"200\":{\"description\":\"OK\",\"headers\":{},\"schema\":{\"type\":\"array\",\"items\":{\"$ref\":\"#/definitions/JsonObject\"}}}}}}},\"definitions\":{\"JsonObject\":{\"properties\":{\"key\":{\"type\":\"string\"}}}}}");

        identifier = TypeIdentifier.ofType(new Type("com.sebastian_daschner.test.Model"));
        properties = new HashMap<>();
        properties.put("name", stringIdentifier);
        properties.put("value", intIdentifier);
        add(data, ResourcesBuilder.withBase("rest")
                        .andTypeRepresentation(identifier, TypeRepresentation.ofConcrete(identifier, properties))
                        .andResource("res6", ResourceMethodBuilder.withMethod(HttpMethod.GET)
                                .andResponse(200, ResponseBuilder.withResponseBody(identifier).build()).build()).build(),
                "{\"swagger\":\"2.0\",\"info\":{\"version\":\"1.0\",\"title\":\"project name\"},\"host\":\"domain.tld\",\"basePath\":\"/rest\",\"schemes\":[\"http\"],\"paths\":{\"/res6\":{\"get\":{\"consumes\":[],\"produces\":[],\"parameters\":[],\"responses\":{\"200\":{\"description\":\"OK\",\"headers\":{},\"schema\":{\"$ref\":\"#/definitions/Model\"}}}}}},\"definitions\":{\"Model\":{\"properties\":{\"name\":{\"type\":\"string\"},\"value\":{\"type\":\"integer\"}}}}}");

        identifier = TypeIdentifier.ofType(new Type("javax.ws.rs.core.StreamingOutput"));
        add(data, ResourcesBuilder.withBase("rest")
                        .andTypeRepresentation(identifier, TypeRepresentation.ofConcrete(identifier))
                        .andResource("res7", ResourceMethodBuilder.withMethod(HttpMethod.GET)
                                .andResponse(200, ResponseBuilder.withResponseBody(identifier).build()).build()).build(),
                "{\"swagger\":\"2.0\",\"info\":{\"version\":\"1.0\",\"title\":\"project name\"},\"host\":\"domain.tld\",\"basePath\":\"/rest\",\"schemes\":[\"http\"],\"paths\":{\"/res7\":{\"get\":{\"consumes\":[],\"produces\":[],\"parameters\":[],\"responses\":{\"200\":{\"description\":\"OK\",\"headers\":{},\"schema\":{\"$ref\":\"#/definitions/StreamingOutput\"}}}}}},\"definitions\":{\"StreamingOutput\":{\"properties\":{}}}}");

        identifier = TypeIdentifier.ofType(new Type("com.sebastian_daschner.test.Model"));
        final TypeIdentifier dynamicIdentifier = TypeIdentifier.ofDynamic();
        properties = new HashMap<>();
        properties.put("name", stringIdentifier);
        properties.put("value", intIdentifier);
        add(data, ResourcesBuilder.withBase("rest")
                        .andTypeRepresentation(dynamicIdentifier, TypeRepresentation.ofCollection(identifier, TypeRepresentation.ofConcrete(identifier, properties)))
                        .andResource("res8", ResourceMethodBuilder.withMethod(HttpMethod.POST).andRequestBodyType(dynamicIdentifier).andAcceptMediaTypes("application/json")
                                .andResponse(201, ResponseBuilder.newBuilder().andHeaders("Location").build()).build()).build(),
                "{\"swagger\":\"2.0\",\"info\":{\"version\":\"1.0\",\"title\":\"project name\"},\"host\":\"domain.tld\",\"basePath\":\"/rest\",\"schemes\":[\"http\"],\"paths\":{\"/res8\":{\"post\":{\"consumes\":[\"application/json\"],\"produces\":[],\"parameters\":[{\"name\":\"body\",\"in\":\"body\",\"required\":true,\"schema\":{\"type\":\"array\",\"items\":{\"$ref\":\"#/definitions/Model\"}}}],\"responses\":{\"201\":{\"description\":\"Created\",\"headers\":{\"Location\":{\"type\":\"string\"}}}}}}},\"definitions\":{\"Model\":{\"properties\":{\"name\":{\"type\":\"string\"},\"value\":{\"type\":\"integer\"}}}}}");

        add(data, ResourcesBuilder.withBase("rest")
                .andResource("res09", ResourceMethodBuilder.withMethod(HttpMethod.GET)
                        .andResponse(200, ResponseBuilder.withResponseBody(TypeIdentifier.ofType(Types.STRING)).andHeaders("Location").build()).build())
                .andResource("res10", ResourceMethodBuilder.withMethod(HttpMethod.GET)
                        .andResponse(200, ResponseBuilder.withResponseBody(TypeIdentifier.ofType(Types.STRING)).andHeaders("Location").build()).build()).build(),
        "{\"swagger\":\"2.0\",\"info\":{\"version\":\"1.0\",\"title\":\"project name\"},\"host\":\"domain.tld\",\"basePath\":\"/rest\",\"schemes\":[\"http\"],\"tags\":[\"res09\",\"res10\"],\"paths\":{"
                + "\"/res09\":{\"get\":{\"consumes\":[],\"produces\":[],\"parameters\":[],\"responses\":{\"200\":{\"description\":\"OK\",\"headers\":{\"Location\":{\"type\":\"string\"}},\"schema\":{\"type\":\"string\"}}},\"tags\":[\"res09\"]}},"
                + "\"/res10\":{\"get\":{\"consumes\":[],\"produces\":[],\"parameters\":[],\"responses\":{\"200\":{\"description\":\"OK\",\"headers\":{\"Location\":{\"type\":\"string\"}},\"schema\":{\"type\":\"string\"}}},\"tags\":[\"res10\"]}}"
                + "},\"definitions\":{}}",
        true, 0);

        add(data, ResourcesBuilder.withBase("rest")
        .andResource("v2/res11", ResourceMethodBuilder.withMethod(HttpMethod.GET)
                .andResponse(200, ResponseBuilder.withResponseBody(TypeIdentifier.ofType(Types.STRING)).andHeaders("Location").build()).build())
        .andResource("v2/res12", ResourceMethodBuilder.withMethod(HttpMethod.GET)
                .andResponse(200, ResponseBuilder.withResponseBody(TypeIdentifier.ofType(Types.STRING)).andHeaders("Location").build()).build()).build(),
        "{\"swagger\":\"2.0\",\"info\":{\"version\":\"1.0\",\"title\":\"project name\"},\"host\":\"domain.tld\",\"basePath\":\"/rest\",\"schemes\":[\"http\"],\"tags\":[\"res11\",\"res12\"],\"paths\":{"
                + "\"/v2/res11\":{\"get\":{\"consumes\":[],\"produces\":[],\"parameters\":[],\"responses\":{\"200\":{\"description\":\"OK\",\"headers\":{\"Location\":{\"type\":\"string\"}},\"schema\":{\"type\":\"string\"}}},\"tags\":[\"res11\"]}},"
                + "\"/v2/res12\":{\"get\":{\"consumes\":[],\"produces\":[],\"parameters\":[],\"responses\":{\"200\":{\"description\":\"OK\",\"headers\":{\"Location\":{\"type\":\"string\"}},\"schema\":{\"type\":\"string\"}}},\"tags\":[\"res12\"]}}"
                + "},\"definitions\":{}}",
        true, 1);

        add(data, ResourcesBuilder.withBase("rest")
        .andResource("v2/res13", ResourceMethodBuilder.withMethod(HttpMethod.GET)
                .andResponse(200, ResponseBuilder.withResponseBody(TypeIdentifier.ofType(Types.STRING)).andHeaders("Location").build()).build())
        .andResource("v2/res14", ResourceMethodBuilder.withMethod(HttpMethod.GET)
                .andResponse(200, ResponseBuilder.withResponseBody(TypeIdentifier.ofType(Types.STRING)).andHeaders("Location").build()).build()).build(),
        "{\"swagger\":\"2.0\",\"info\":{\"version\":\"1.0\",\"title\":\"project name\"},\"host\":\"domain.tld\",\"basePath\":\"/rest\",\"schemes\":[\"http\"],\"tags\":[],\"paths\":{"
                + "\"/v2/res13\":{\"get\":{\"consumes\":[],\"produces\":[],\"parameters\":[],\"responses\":{\"200\":{\"description\":\"OK\",\"headers\":{\"Location\":{\"type\":\"string\"}},\"schema\":{\"type\":\"string\"}}}}},"
                + "\"/v2/res14\":{\"get\":{\"consumes\":[],\"produces\":[],\"parameters\":[],\"responses\":{\"200\":{\"description\":\"OK\",\"headers\":{\"Location\":{\"type\":\"string\"}},\"schema\":{\"type\":\"string\"}}}}}"
                + "},\"definitions\":{}}",
        true, 42);

        return data;
    }

    public static void add(final Collection<Object[]> data, final Resources resources, final String output) {
        add(data, resources, output, false, 0);
    }

    public static void add(final Collection<Object[]> data, final Resources resources, final String output,
            final Boolean renderSwaggerTags, final Integer swaggerTagsPathOffset) {
        final Object[] objects = new Object[4];
        objects[0] = resources;
        objects[1] = output;
        objects[2] = renderSwaggerTags;
        objects[3] = swaggerTagsPathOffset;
        data.add(objects);
    }

}