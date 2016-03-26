package com.sebastian_daschner.jaxrs_analyzer.backend.plaintext;

import com.sebastian_daschner.jaxrs_analyzer.model.Types;
import com.sebastian_daschner.jaxrs_analyzer.backend.Backend;
import com.sebastian_daschner.jaxrs_analyzer.builder.ResourceMethodBuilder;
import com.sebastian_daschner.jaxrs_analyzer.builder.ResourcesBuilder;
import com.sebastian_daschner.jaxrs_analyzer.builder.ResponseBuilder;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class PlainTextBackendTest {

    private final Backend cut;
    private final Resources resources;
    private final String expectedOutput;

    public PlainTextBackendTest(final Resources resources, final String expectedOutput) {
        cut = new PlainTextBackend();
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
        TypeIdentifier nestedIdentifier;
        Map<String, TypeIdentifier> properties = new HashMap<>();

        add(data, ResourcesBuilder.withBase("rest").andResource("res1", ResourceMethodBuilder.withMethod(HttpMethod.GET)
                        .andResponse(200, ResponseBuilder.withResponseBody(TypeIdentifier.ofType(Types.STRING)).andHeaders("Location").build()).build()).build(),
                "REST resources of project name:\n" +
                        "1.0\n" +
                        "\n" +
                        "GET rest/res1:\n" +
                        " Request:\n" +
                        "  No body\n" +
                        "\n" +
                        " Response:\n" +
                        "  Content-Type: */*\n" +
                        "  Status Codes: 200\n" +
                        "   Header: Location\n" +
                        "   Response Body: java.lang.String\n\n\n");

        identifier = TypeIdentifier.ofDynamic();
        properties.put("key", stringIdentifier);
        properties.put("another", intIdentifier);
        add(data, ResourcesBuilder.withBase("rest")
                        .andTypeRepresentation(identifier, TypeRepresentation.ofConcrete(identifier, properties))
                        .andResource("res2", ResourceMethodBuilder.withMethod(HttpMethod.GET)
                                .andResponse(200, ResponseBuilder.withResponseBody(identifier).build()).build()).build(),
                "REST resources of project name:\n" +
                        "1.0\n" +
                        "\n" +
                        "GET rest/res2:\n" +
                        " Request:\n" +
                        "  No body\n" +
                        "\n" +
                        " Response:\n" +
                        "  Content-Type: */*\n" +
                        "  Status Codes: 200\n" +
                        "   Response Body: javax.json.Json\n" +
                        "    {\"another\":0,\"key\":\"string\"}\n\n\n");

        identifier = TypeIdentifier.ofDynamic();
        properties = new HashMap<>();
        properties.put("key", stringIdentifier);
        properties.put("another", intIdentifier);
        add(data, ResourcesBuilder.withBase("rest")
                        .andTypeRepresentation(identifier, TypeRepresentation.ofCollection(identifier, TypeRepresentation.ofConcrete(TypeIdentifier.ofDynamic(), properties)))
                        .andResource("res3", ResourceMethodBuilder.withMethod(HttpMethod.GET)
                                .andResponse(200, ResponseBuilder.withResponseBody(identifier).build()).build()).build(),
                "REST resources of project name:\n" +
                        "1.0\n" +
                        "\n" +
                        "GET rest/res3:\n" +
                        " Request:\n" +
                        "  No body\n" +
                        "\n" +
                        " Response:\n" +
                        "  Content-Type: */*\n" +
                        "  Status Codes: 200\n" +
                        "   Response Body: javax.json.Json\n" +
                        "    [{\"another\":0,\"key\":\"string\"}]\n\n\n");

        identifier = TypeIdentifier.ofDynamic();
        add(data, ResourcesBuilder.withBase("rest")
                        .andTypeRepresentation(identifier, TypeRepresentation.ofCollection(identifier, TypeRepresentation.ofConcrete(stringIdentifier)))
                        .andResource("res4", ResourceMethodBuilder.withMethod(HttpMethod.GET)
                                .andResponse(200, ResponseBuilder.withResponseBody(identifier).build()).build()).build(),
                "REST resources of project name:\n" +
                        "1.0\n" +
                        "\n" +
                        "GET rest/res4:\n" +
                        " Request:\n" +
                        "  No body\n" +
                        "\n" +
                        " Response:\n" +
                        "  Content-Type: */*\n" +
                        "  Status Codes: 200\n" +
                        "   Response Body: javax.json.Json\n" +
                        "    [\"string\"]\n\n\n");

        identifier = TypeIdentifier.ofDynamic();
        properties = new HashMap<>();
        properties.put("key", stringIdentifier);
        add(data, ResourcesBuilder.withBase("rest")
                        .andTypeRepresentation(identifier, TypeRepresentation.ofCollection(identifier, TypeRepresentation.ofConcrete(TypeIdentifier.ofDynamic(), properties)))
                        .andResource("res5", ResourceMethodBuilder.withMethod(HttpMethod.GET)
                                .andResponse(200, ResponseBuilder.withResponseBody(identifier).build()).build()).build(),
                "REST resources of project name:\n" +
                        "1.0\n" +
                        "\n" +
                        "GET rest/res5:\n" +
                        " Request:\n" +
                        "  No body\n" +
                        "\n" +
                        " Response:\n" +
                        "  Content-Type: */*\n" +
                        "  Status Codes: 200\n" +
                        "   Response Body: javax.json.Json\n" +
                        "    [{\"key\":\"string\"}]\n\n\n");

        identifier = TypeIdentifier.ofType("com.sebastian_daschner.test.Model");
        properties = new HashMap<>();
        properties.put("name", stringIdentifier);
        properties.put("value", intIdentifier);
        add(data, ResourcesBuilder.withBase("rest")
                        .andTypeRepresentation(identifier, TypeRepresentation.ofConcrete(identifier, properties))
                        .andResource("res6", ResourceMethodBuilder.withMethod(HttpMethod.GET)
                                .andResponse(200, ResponseBuilder.withResponseBody(identifier).build()).build()).build(),
                "REST resources of project name:\n" +
                        "1.0\n" +
                        "\n" +
                        "GET rest/res6:\n" +
                        " Request:\n" +
                        "  No body\n" +
                        "\n" +
                        " Response:\n" +
                        "  Content-Type: */*\n" +
                        "  Status Codes: 200\n" +
                        "   Response Body: com.sebastian_daschner.test.Model\n" +
                        "    {\"name\":\"string\",\"value\":0}\n\n\n");

        identifier = TypeIdentifier.ofType("java.util.List<com.sebastian_daschner.test.Model>");
        nestedIdentifier = TypeIdentifier.ofType("com.sebastian_daschner.test.Model");
        properties = new HashMap<>();
        properties.put("name", stringIdentifier);
        properties.put("value", intIdentifier);
        add(data, ResourcesBuilder.withBase("rest")
                        .andTypeRepresentation(identifier, TypeRepresentation.ofCollection(identifier, TypeRepresentation.ofConcrete(nestedIdentifier, properties)))
                        .andResource("res7", ResourceMethodBuilder.withMethod(HttpMethod.GET)
                                .andResponse(200, ResponseBuilder.withResponseBody(identifier).build()).build()).build(),
                "REST resources of project name:\n" +
                        "1.0\n" +
                        "\n" +
                        "GET rest/res7:\n" +
                        " Request:\n" +
                        "  No body\n" +
                        "\n" +
                        " Response:\n" +
                        "  Content-Type: */*\n" +
                        "  Status Codes: 200\n" +
                        "   Response Body: Collection of com.sebastian_daschner.test.Model\n" +
                        "    [{\"name\":\"string\",\"value\":0}]\n\n\n");

        add(data, ResourcesBuilder.withBase("rest")
                        .andTypeRepresentation(identifier, TypeRepresentation.ofCollection(identifier, TypeRepresentation.ofConcrete(nestedIdentifier, properties)))
                        .andResource("res8", ResourceMethodBuilder.withMethod(HttpMethod.POST).andRequestBodyType(identifier).andAcceptMediaTypes("application/json")
                                .andResponse(201, ResponseBuilder.newBuilder().andHeaders("Location").build()).build()).build(),
                "REST resources of project name:\n" +
                        "1.0\n" +
                        "\n" +
                        "POST rest/res8:\n" +
                        " Request:\n" +
                        "  Content-Type: application/json\n" +
                        "  Request Body: Collection of com.sebastian_daschner.test.Model\n" +
                        "   [{\"name\":\"string\",\"value\":0}]\n" +
                        "\n" +
                        " Response:\n" +
                        "  Content-Type: */*\n" +
                        "  Status Codes: 201\n" +
                        "   Header: Location\n\n\n");

        add(data, ResourcesBuilder.withBase("rest")
                        .andTypeRepresentation(identifier, TypeRepresentation.ofCollection(identifier, TypeRepresentation.ofConcrete(nestedIdentifier, properties)))
                        .andResource("res9", ResourceMethodBuilder.withMethod(HttpMethod.POST).andRequestBodyType(identifier).andAcceptMediaTypes("application/json")
                                .andResponse(201, ResponseBuilder.newBuilder().andHeaders("Location").build()).build())
                        .andResource("res10", ResourceMethodBuilder.withMethod(HttpMethod.GET).andResponse(200, ResponseBuilder.newBuilder().build()).build()).build(),
                "REST resources of project name:\n" +
                        "1.0\n" +
                        "\n" +
                        "GET rest/res10:\n" +
                        " Request:\n" +
                        "  No body\n" +
                        "\n" +
                        " Response:\n" +
                        "  Content-Type: */*\n" +
                        "  Status Codes: 200\n\n\n" +
                        "POST rest/res9:\n" +
                        " Request:\n" +
                        "  Content-Type: application/json\n" +
                        "  Request Body: Collection of com.sebastian_daschner.test.Model\n" +
                        "   [{\"name\":\"string\",\"value\":0}]\n" +
                        "\n" +
                        " Response:\n" +
                        "  Content-Type: */*\n" +
                        "  Status Codes: 201\n" +
                        "   Header: Location\n\n\n");
        return data;
    }

    public static void add(final Collection<Object[]> data, final Resources resources, final String output) {
        final Object[] objects = new Object[2];
        objects[0] = resources;
        objects[1] = output;
        data.add(objects);
    }

}