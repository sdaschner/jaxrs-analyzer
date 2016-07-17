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
import com.sebastian_daschner.jaxrs_analyzer.model.Types;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.*;

import static com.sebastian_daschner.jaxrs_analyzer.analysis.results.TypeUtils.MODEL_IDENTIFIER;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class SwaggerBackendTest {

    private final Backend cut;
    private final Resources resources;
    private final String expectedOutput;

    public SwaggerBackendTest(final Resources resources, final String expectedOutput, final SwaggerOptions options) {
        cut = new SwaggerBackend(options);
        this.resources = resources;
        this.expectedOutput = expectedOutput;
    }

    @Test
    public void test() {
        final Project project = new Project("project name", "1.0", resources);
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
                        .andResource("res1", ResourceMethodBuilder.withMethod("res1Get", HttpMethod.GET)
                                .andResponse(200, ResponseBuilder.withResponseBody(TypeIdentifier.ofType(Types.STRING)).andHeaders("Location").build()).build()).build(),
                "{\"swagger\":\"2.0\",\"info\":{\"version\":\"1.0\",\"title\":\"project name\"},\"host\":\"example.com\",\"basePath\":\"/rest\",\"schemes\":[\"http\"],\"paths\":{\"/res1\":{\"get\":{\"consumes\":[],\"produces\":[],\"operationId\":\"res1Get\",\"parameters\":[],\"responses\":{\"200\":{\"description\":\"OK\",\"headers\":{\"Location\":{\"type\":\"string\"}},\"schema\":{\"type\":\"string\"}}}}}},\"definitions\":{}}");

        SwaggerOptions options = new SwaggerOptions();
        options.setSchemes(EnumSet.of(SwaggerScheme.HTTPS, SwaggerScheme.WSS));
        add(data, ResourcesBuilder.withBase("rest")
                        .andResource("res1", ResourceMethodBuilder.withMethod("res1Get", HttpMethod.GET)
                                .andResponse(200, ResponseBuilder.withResponseBody(TypeIdentifier.ofType(Types.STRING)).andHeaders("Location").build()).build()).build(),
                "{\"swagger\":\"2.0\",\"info\":{\"version\":\"1.0\",\"title\":\"project name\"},\"host\":\"example.com\",\"basePath\":\"/rest\",\"schemes\":[\"https\",\"wss\"],\"paths\":{\"/res1\":{\"get\":{\"consumes\":[],\"produces\":[],\"operationId\":\"res1Get\",\"parameters\":[],\"responses\":{\"200\":{\"description\":\"OK\",\"headers\":{\"Location\":{\"type\":\"string\"}},\"schema\":{\"type\":\"string\"}}}}}},\"definitions\":{}}",
                options);

        identifier = TypeIdentifier.ofDynamic();
        properties.put("key", stringIdentifier);
        properties.put("another", intIdentifier);
        add(data, ResourcesBuilder.withBase("rest").andTypeRepresentation(identifier, TypeRepresentation.ofConcrete(identifier, properties))
                        .andResource("res2", ResourceMethodBuilder.withMethod("res2Get", HttpMethod.GET)
                                .andResponse(200, ResponseBuilder.withResponseBody(identifier).build()).build()).build(),
                "{\"swagger\":\"2.0\",\"info\":{\"version\":\"1.0\",\"title\":\"project name\"},\"host\":\"example.com\",\"basePath\":\"/rest\",\"schemes\":[\"http\"],\"paths\":{\"/res2\":{\"get\":{\"consumes\":[],\"produces\":[],\"operationId\":\"res2Get\",\"parameters\":[],\"responses\":{\"200\":{\"description\":\"OK\",\"headers\":{},\"schema\":{\"$ref\":\"#/definitions/JsonObject\"}}}}}},\"definitions\":{\"JsonObject\":{\"properties\":{\"another\":{\"type\":\"integer\"},\"key\":{\"type\":\"string\"}}}}}");

        identifier = TypeIdentifier.ofDynamic();
        properties = new HashMap<>();
        properties.put("key", stringIdentifier);
        properties.put("another", intIdentifier);
        add(data, ResourcesBuilder.withBase("rest")
                        .andTypeRepresentation(identifier, TypeRepresentation.ofCollection(identifier, TypeRepresentation.ofConcrete(TypeIdentifier.ofDynamic(), properties)))
                        .andResource("res3", ResourceMethodBuilder.withMethod("res3Get", HttpMethod.GET)
                                .andResponse(200, ResponseBuilder.withResponseBody(identifier).build()).build()).build(),
                "{\"swagger\":\"2.0\",\"info\":{\"version\":\"1.0\",\"title\":\"project name\"},\"host\":\"example.com\",\"basePath\":\"/rest\",\"schemes\":[\"http\"],\"paths\":{\"/res3\":{\"get\":{\"consumes\":[],\"produces\":[],\"operationId\":\"res3Get\",\"parameters\":[],\"responses\":{\"200\":{\"description\":\"OK\",\"headers\":{},\"schema\":{\"type\":\"array\",\"items\":{\"$ref\":\"#/definitions/JsonObject\"}}}}}}},\"definitions\":{\"JsonObject\":{\"properties\":{\"another\":{\"type\":\"integer\"},\"key\":{\"type\":\"string\"}}}}}");

        identifier = TypeIdentifier.ofDynamic();
        add(data, ResourcesBuilder.withBase("rest")
                        .andTypeRepresentation(identifier, TypeRepresentation.ofCollection(identifier, TypeRepresentation.ofConcrete(stringIdentifier)))
                        .andResource("res4", ResourceMethodBuilder.withMethod("res4Get", HttpMethod.GET)
                                .andResponse(200, ResponseBuilder.withResponseBody(identifier).build()).build()).build(),
                "{\"swagger\":\"2.0\",\"info\":{\"version\":\"1.0\",\"title\":\"project name\"},\"host\":\"example.com\",\"basePath\":\"/rest\",\"schemes\":[\"http\"],\"paths\":{\"/res4\":{\"get\":{\"consumes\":[],\"produces\":[],\"operationId\":\"res4Get\",\"parameters\":[],\"responses\":{\"200\":{\"description\":\"OK\",\"headers\":{},\"schema\":{\"type\":\"array\",\"items\":{\"type\":\"string\"}}}}}}},\"definitions\":{}}");

        identifier = TypeIdentifier.ofDynamic();
        properties = new HashMap<>();
        properties.put("key", stringIdentifier);
        add(data, ResourcesBuilder.withBase("rest")
                        .andTypeRepresentation(identifier, TypeRepresentation.ofCollection(identifier, TypeRepresentation.ofConcrete(identifier, properties)))
                        .andResource("res5", ResourceMethodBuilder.withMethod("res5Get", HttpMethod.GET)
                                .andResponse(200, ResponseBuilder.withResponseBody(identifier).build()).build()).build(),
                "{\"swagger\":\"2.0\",\"info\":{\"version\":\"1.0\",\"title\":\"project name\"},\"host\":\"example.com\",\"basePath\":\"/rest\",\"schemes\":[\"http\"],\"paths\":{\"/res5\":{\"get\":{\"consumes\":[],\"produces\":[],\"operationId\":\"res5Get\",\"parameters\":[],\"responses\":{\"200\":{\"description\":\"OK\",\"headers\":{},\"schema\":{\"type\":\"array\",\"items\":{\"$ref\":\"#/definitions/JsonObject\"}}}}}}},\"definitions\":{\"JsonObject\":{\"properties\":{\"key\":{\"type\":\"string\"}}}}}");

        properties = new HashMap<>();
        properties.put("name", stringIdentifier);
        properties.put("value", intIdentifier);
        add(data, ResourcesBuilder.withBase("rest")
                        .andTypeRepresentation(MODEL_IDENTIFIER, TypeRepresentation.ofConcrete(MODEL_IDENTIFIER, properties))
                        .andResource("res6", ResourceMethodBuilder.withMethod("res6Get", HttpMethod.GET)
                                .andResponse(200, ResponseBuilder.withResponseBody(MODEL_IDENTIFIER).build()).build()).build(),
                "{\"swagger\":\"2.0\",\"info\":{\"version\":\"1.0\",\"title\":\"project name\"},\"host\":\"example.com\",\"basePath\":\"/rest\",\"schemes\":[\"http\"],\"paths\":{\"/res6\":{\"get\":{\"consumes\":[],\"produces\":[],\"operationId\":\"res6Get\",\"parameters\":[],\"responses\":{\"200\":{\"description\":\"OK\",\"headers\":{},\"schema\":{\"$ref\":\"#/definitions/Model\"}}}}}},\"definitions\":{\"Model\":{\"properties\":{\"name\":{\"type\":\"string\"},\"value\":{\"type\":\"integer\"}}}}}");

        identifier = TypeIdentifier.ofType("Ljavax/ws/rs/core/StreamingOutput;");
        add(data, ResourcesBuilder.withBase("rest")
                        .andTypeRepresentation(identifier, TypeRepresentation.ofConcrete(identifier))
                        .andResource("res7", ResourceMethodBuilder.withMethod("res7Get", HttpMethod.GET)
                                .andResponse(200, ResponseBuilder.withResponseBody(identifier).build()).build()).build(),
                "{\"swagger\":\"2.0\",\"info\":{\"version\":\"1.0\",\"title\":\"project name\"},\"host\":\"example.com\",\"basePath\":\"/rest\",\"schemes\":[\"http\"],\"paths\":{\"/res7\":{\"get\":{\"consumes\":[],\"produces\":[],\"operationId\":\"res7Get\",\"parameters\":[],\"responses\":{\"200\":{\"description\":\"OK\",\"headers\":{},\"schema\":{\"$ref\":\"#/definitions/StreamingOutput\"}}}}}},\"definitions\":{\"StreamingOutput\":{\"properties\":{}}}}");

        final TypeIdentifier dynamicIdentifier = TypeIdentifier.ofDynamic();
        properties = new HashMap<>();
        properties.put("name", stringIdentifier);
        properties.put("value", intIdentifier);
        add(data, ResourcesBuilder.withBase("rest")
                        .andTypeRepresentation(dynamicIdentifier, TypeRepresentation.ofCollection(MODEL_IDENTIFIER, TypeRepresentation.ofConcrete(MODEL_IDENTIFIER, properties)))
                        .andResource("res8", ResourceMethodBuilder.withMethod("res8Post", HttpMethod.POST).andRequestBodyType(dynamicIdentifier).andAcceptMediaTypes("application/json")
                                .andResponse(201, ResponseBuilder.newBuilder().andHeaders("Location").build()).build()).build(),
                "{\"swagger\":\"2.0\",\"info\":{\"version\":\"1.0\",\"title\":\"project name\"},\"host\":\"example.com\",\"basePath\":\"/rest\",\"schemes\":[\"http\"],\"paths\":{\"/res8\":{\"post\":{\"consumes\":[\"application/json\"],\"produces\":[],\"operationId\":\"res8Post\",\"parameters\":[{\"name\":\"body\",\"in\":\"body\",\"required\":true,\"schema\":{\"type\":\"array\",\"items\":{\"$ref\":\"#/definitions/Model\"}}}],\"responses\":{\"201\":{\"description\":\"Created\",\"headers\":{\"Location\":{\"type\":\"string\"}}}}}}},\"definitions\":{\"Model\":{\"properties\":{\"name\":{\"type\":\"string\"},\"value\":{\"type\":\"integer\"}}}}}");

        options = new SwaggerOptions();
        options.setDomain("domain.tld");
        options.setSchemes(EnumSet.of(SwaggerScheme.HTTP, SwaggerScheme.HTTPS));
        options.setRenderTags(true);
        options.setTagsPathOffset(0);
        add(data, ResourcesBuilder.withBase("rest")
                        .andResource("res09", ResourceMethodBuilder.withMethod("res09Get", HttpMethod.GET)
                                .andResponse(200, ResponseBuilder.withResponseBody(TypeIdentifier.ofType(Types.STRING)).andHeaders("Location").build()).build())
                        .andResource("res10", ResourceMethodBuilder.withMethod("res10Get", HttpMethod.GET)
                                .andResponse(200, ResponseBuilder.withResponseBody(TypeIdentifier.ofType(Types.STRING)).andHeaders("Location").build()).build()).build(),
                "{\"swagger\":\"2.0\",\"info\":{\"version\":\"1.0\",\"title\":\"project name\"},\"host\":\"domain.tld\",\"basePath\":\"/rest\",\"schemes\":[\"http\",\"https\"],\"tags\":[\"res09\",\"res10\"],\"paths\":{"
                        + "\"/res09\":{\"get\":{\"consumes\":[],\"produces\":[],\"operationId\":\"res09Get\",\"parameters\":[],\"responses\":{\"200\":{\"description\":\"OK\",\"headers\":{\"Location\":{\"type\":\"string\"}},\"schema\":{\"type\":\"string\"}}},\"tags\":[\"res09\"]}},"
                        + "\"/res10\":{\"get\":{\"consumes\":[],\"produces\":[],\"operationId\":\"res10Get\",\"parameters\":[],\"responses\":{\"200\":{\"description\":\"OK\",\"headers\":{\"Location\":{\"type\":\"string\"}},\"schema\":{\"type\":\"string\"}}},\"tags\":[\"res10\"]}}"
                        + "},\"definitions\":{}}",
                options);

        options = new SwaggerOptions();
        options.setRenderTags(true);
        options.setTagsPathOffset(1);
        add(data, ResourcesBuilder.withBase("rest")
                        .andResource("v2/res11", ResourceMethodBuilder.withMethod("v2res11Get", HttpMethod.GET)
                                .andResponse(200, ResponseBuilder.withResponseBody(TypeIdentifier.ofType(Types.STRING)).andHeaders("Location").build()).build())
                        .andResource("v2/res12", ResourceMethodBuilder.withMethod("v2res12Get", HttpMethod.GET)
                                .andResponse(200, ResponseBuilder.withResponseBody(TypeIdentifier.ofType(Types.STRING)).andHeaders("Location").build()).build()).build(),
                "{\"swagger\":\"2.0\",\"info\":{\"version\":\"1.0\",\"title\":\"project name\"},\"host\":\"example.com\",\"basePath\":\"/rest\",\"schemes\":[\"http\"],\"tags\":[\"res11\",\"res12\"],\"paths\":{"
                        + "\"/v2/res11\":{\"get\":{\"consumes\":[],\"produces\":[],\"operationId\":\"v2res11Get\",\"parameters\":[],\"responses\":{\"200\":{\"description\":\"OK\",\"headers\":{\"Location\":{\"type\":\"string\"}},\"schema\":{\"type\":\"string\"}}},\"tags\":[\"res11\"]}},"
                        + "\"/v2/res12\":{\"get\":{\"consumes\":[],\"produces\":[],\"operationId\":\"v2res12Get\",\"parameters\":[],\"responses\":{\"200\":{\"description\":\"OK\",\"headers\":{\"Location\":{\"type\":\"string\"}},\"schema\":{\"type\":\"string\"}}},\"tags\":[\"res12\"]}}"
                        + "},\"definitions\":{}}",
                options);

        options = new SwaggerOptions();
        options.setDomain("domain.tld");
        options.setSchemes(EnumSet.of(SwaggerScheme.HTTP, SwaggerScheme.HTTPS));
        options.setRenderTags(true);
        options.setTagsPathOffset(42);
        add(data, ResourcesBuilder.withBase("rest")
                        .andResource("v2/res13", ResourceMethodBuilder.withMethod("v2res13Get", HttpMethod.GET)
                                .andResponse(200, ResponseBuilder.withResponseBody(TypeIdentifier.ofType(Types.STRING)).andHeaders("Location").build()).build())
                        .andResource("v2/res14", ResourceMethodBuilder.withMethod("v2res14Get", HttpMethod.GET)
                                .andResponse(200, ResponseBuilder.withResponseBody(TypeIdentifier.ofType(Types.STRING)).andHeaders("Location").build()).build()).build(),
                "{\"swagger\":\"2.0\",\"info\":{\"version\":\"1.0\",\"title\":\"project name\"},\"host\":\"domain.tld\",\"basePath\":\"/rest\",\"schemes\":[\"http\",\"https\"],\"tags\":[],\"paths\":{"
                        + "\"/v2/res13\":{\"get\":{\"consumes\":[],\"produces\":[],\"operationId\":\"v2res13Get\",\"parameters\":[],\"responses\":{\"200\":{\"description\":\"OK\",\"headers\":{\"Location\":{\"type\":\"string\"}},\"schema\":{\"type\":\"string\"}}}}},"
                        + "\"/v2/res14\":{\"get\":{\"consumes\":[],\"produces\":[],\"operationId\":\"v2res14Get\",\"parameters\":[],\"responses\":{\"200\":{\"description\":\"OK\",\"headers\":{\"Location\":{\"type\":\"string\"}},\"schema\":{\"type\":\"string\"}}}}}"
                        + "},\"definitions\":{}}",
                options);

        // query parameter tests
        add(data, ResourcesBuilder.withBase("rest")
                        .andTypeRepresentation(dynamicIdentifier, TypeRepresentation.ofCollection(identifier, TypeRepresentation.ofConcrete(identifier, properties)))
                        .andResource("res15", ResourceMethodBuilder.withMethod("res15Get", HttpMethod.GET).andQueryParam("value", "Ljava/lang/Integer;").andAcceptMediaTypes("application/json")
                                .andResponse(200, ResponseBuilder.withResponseBody(TypeIdentifier.ofType(Types.STRING)).andHeaders("Location").build()).build()).build(),
                "{\"swagger\":\"2.0\",\"info\":{\"version\":\"1.0\",\"title\":\"project name\"},\"host\":\"example.com\",\"basePath\":\"/rest\",\"schemes\":[\"http\"],\"paths\":{\"/res15\":{\"get\":{\"consumes\":[\"application/json\"],\"produces\":[],\"operationId\":\"res15Get\",\"parameters\":[{\"type\":\"integer\",\"name\":\"value\",\"in\":\"query\",\"required\":true}],\"responses\":{\"200\":{\"description\":\"OK\",\"headers\":{\"Location\":{\"type\":\"string\"}},\"schema\":{\"type\":\"string\"}}}}}},\"definitions\":{}}");

        add(data, ResourcesBuilder.withBase("rest")
                        .andTypeRepresentation(dynamicIdentifier, TypeRepresentation.ofCollection(identifier, TypeRepresentation.ofConcrete(identifier, properties)))
                        .andResource("res16", ResourceMethodBuilder.withMethod("res16Get", HttpMethod.GET).andQueryParam("value", "Ljava/lang/Integer;", "test").andAcceptMediaTypes("application/json")
                                .andResponse(200, ResponseBuilder.withResponseBody(TypeIdentifier.ofType(Types.STRING)).andHeaders("Location").build()).build()).build(),
                "{\"swagger\":\"2.0\",\"info\":{\"version\":\"1.0\",\"title\":\"project name\"},\"host\":\"example.com\",\"basePath\":\"/rest\",\"schemes\":[\"http\"],\"paths\":{\"/res16\":{\"get\":{\"consumes\":[\"application/json\"],\"produces\":[],\"operationId\":\"res16Get\",\"parameters\":[{\"type\":\"integer\",\"name\":\"value\",\"in\":\"query\",\"required\":false}],\"responses\":{\"200\":{\"description\":\"OK\",\"headers\":{\"Location\":{\"type\":\"string\"}},\"schema\":{\"type\":\"string\"}}}}}},\"definitions\":{}}");

        add(data, ResourcesBuilder.withBase("rest")
                        .andTypeRepresentation(dynamicIdentifier, TypeRepresentation.ofCollection(identifier, TypeRepresentation.ofConcrete(identifier, properties)))
                        .andResource("res17", ResourceMethodBuilder.withMethod("res17Get", HttpMethod.GET).andQueryParam("value", "Ljava/lang/Integer;").andQueryParam("name", "Ljava/lang/String;", "foobar").andAcceptMediaTypes("application/json")
                                .andResponse(200, ResponseBuilder.withResponseBody(TypeIdentifier.ofType(Types.STRING)).andHeaders("Location").build()).build()).build(),
                "{\"swagger\":\"2.0\",\"info\":{\"version\":\"1.0\",\"title\":\"project name\"},\"host\":\"example.com\",\"basePath\":\"/rest\",\"schemes\":[\"http\"],\"paths\":{\"/res17\":{\"get\":{\"consumes\":[\"application/json\"],\"produces\":[],\"operationId\":\"res17Get\",\"parameters\":[{\"type\":\"string\",\"name\":\"name\",\"in\":\"query\",\"required\":false},{\"type\":\"integer\",\"name\":\"value\",\"in\":\"query\",\"required\":true}],\"responses\":{\"200\":{\"description\":\"OK\",\"headers\":{\"Location\":{\"type\":\"string\"}},\"schema\":{\"type\":\"string\"}}}}}},\"definitions\":{}}");

        // Enum type tests
        identifier = TypeIdentifier.ofType("Lcom/sebastian_daschner/test/FirstEnum;");
        final TypeIdentifier secondIdentifier = TypeIdentifier.ofType("Lcom/sebastian_daschner/test/SecondEnum;");
        add(data, ResourcesBuilder.withBase("rest")
                        .andResource("res18", ResourceMethodBuilder
                                .withMethod("res18Get", HttpMethod.GET)
                                .andQueryParam("q1", identifier.getType())
                                .andQueryParam("q2", secondIdentifier.getType())
                                .build()
                        ).andTypeRepresentation(identifier, TypeRepresentation.ofEnum(identifier, "APPLE", "BANANA"))
                        .andTypeRepresentation(secondIdentifier, TypeRepresentation.ofEnum(secondIdentifier, "APPLE", "BANANA"))
                        .build(),
                "{\"swagger\":\"2.0\",\"info\":{\"version\":\"1.0\",\"title\":\"project name\"},\"host\":\"example.com\",\"basePath\":\"/rest\",\"schemes\":[\"http\"],\"paths\":{\"/res18\":{\"get\":{\"consumes\":[],\"produces\":[],\"operationId\":\"res18Get\",\"parameters\":[{\"type\":\"string\",\"enum\":[\"APPLE\",\"BANANA\"],\"name\":\"q1\",\"in\":\"query\",\"required\":true},{\"type\":\"string\",\"enum\":[\"APPLE\",\"BANANA\"],\"name\":\"q2\",\"in\":\"query\",\"required\":true}],\"responses\":{}}}},\"definitions\":{}}"
        );

        return data;
    }

    public static void add(final Collection<Object[]> data, final Resources resources, final String output) {
        add(data, resources, output, new SwaggerOptions());
    }

    public static void add(final Collection<Object[]> data, final Resources resources, final String output, final SwaggerOptions options) {
        final Object[] objects = new Object[3];
        objects[0] = resources;
        objects[1] = output;
        objects[2] = options;
        data.add(objects);
    }

}
