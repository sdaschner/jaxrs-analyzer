package com.sebastian_daschner.jaxrs_analyzer.backend.asciidoc;

import com.sebastian_daschner.jaxrs_analyzer.builder.ResourceMethodBuilder;
import com.sebastian_daschner.jaxrs_analyzer.builder.ResourcesBuilder;
import com.sebastian_daschner.jaxrs_analyzer.builder.ResponseBuilder;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.HttpMethod;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.Resources;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeRepresentation;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import javax.json.Json;
import java.util.Collection;
import java.util.LinkedList;

@RunWith(Parameterized.class)
public class AsciiDocBackendTest extends TestCase {

    private final AsciiDocBackend cut;
    private final Resources resources;
    private final String expectedOutput;

    public AsciiDocBackendTest(final Resources resources, final String expectedOutput) {
        cut = new AsciiDocBackend();
        this.resources = resources;
        this.expectedOutput = expectedOutput;
    }

    @Test
    public void test() {
        final String actualOutput = cut.render(resources);

        assertEquals(expectedOutput, actualOutput);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        final Collection<Object[]> data = new LinkedList<>();

        TypeRepresentation representation;

        add(data, ResourcesBuilder.withBase("rest").andResource("res1", ResourceMethodBuilder.withMethod(HttpMethod.GET)
                        .andResponse(200, ResponseBuilder.withResponseBody(new TypeRepresentation("java.lang.String")).andHeaders("Location").build()).build()).build(),
                "= REST resources\n" +
                        "v0.1\n" +
                        "\n" +
                        "== `GET rest/res1`\n" +
                        "\n" +
                        "=== Request\n" +
                        "_No body_ + \n" +
                        "\n" +
                        "=== Response\n" +
                        "*Content-Type*: `\\*/*`\n" +
                        "\n" +
                        "==== `200 OK`\n" +
                        "*Header*: `Location` + \n" +
                        "*Response Body*: (`java.lang.String`) + \n\n");

        representation = new TypeRepresentation("javax.json.JsonObject");
        representation.getRepresentations().put("application/json", Json.createObjectBuilder().add("key", "string").add("another", 0).build());
        add(data, ResourcesBuilder.withBase("rest")
                        .andResource("res1", ResourceMethodBuilder.withMethod(HttpMethod.GET)
                                .andResponse(200, ResponseBuilder.withResponseBody(representation).build()).build()).build(),
                "= REST resources\n" +
                        "v0.1\n" +
                        "\n" +
                        "== `GET rest/res1`\n" +
                        "\n" +
                        "=== Request\n" +
                        "_No body_ + \n" +
                        "\n" +
                        "=== Response\n" +
                        "*Content-Type*: `\\*/*`\n" +
                        "\n" +
                        "==== `200 OK`\n" +
                        "*Response Body*: (`javax.json.JsonObject`) + \n" +
                        "`application/json`: `{\"key\":\"string\",\"another\":0}` + \n\n");

        representation = new TypeRepresentation("javax.json.JsonObject");
        representation.getRepresentations().put("application/json", Json.createArrayBuilder().add(Json.createObjectBuilder().add("key", "string").add("another", 0)).build());
        add(data, ResourcesBuilder.withBase("rest")
                        .andResource("res1", ResourceMethodBuilder.withMethod(HttpMethod.GET)
                                .andResponse(200, ResponseBuilder.withResponseBody(representation).build()).build()).build(),
                "= REST resources\n" +
                        "v0.1\n" +
                        "\n" +
                        "== `GET rest/res1`\n" +
                        "\n" +
                        "=== Request\n" +
                        "_No body_ + \n" +
                        "\n" +
                        "=== Response\n" +
                        "*Content-Type*: `\\*/*`\n" +
                        "\n" +
                        "==== `200 OK`\n" +
                        "*Response Body*: (`javax.json.JsonObject`) + \n" +
                        "`application/json`: `[{\"key\":\"string\",\"another\":0}]` + \n\n");

        representation = new TypeRepresentation("javax.json.JsonArray");
        representation.getRepresentations().put("application/json", Json.createArrayBuilder().add("string").add(0).build());
        representation.getRepresentations().put("application/xml", Json.createArrayBuilder().add("string").add(0).build());
        add(data, ResourcesBuilder.withBase("rest")
                        .andResource("res1", ResourceMethodBuilder.withMethod(HttpMethod.GET)
                                .andResponse(200, ResponseBuilder.withResponseBody(representation).build()).build()).build(),
                "= REST resources\n" +
                        "v0.1\n" +
                        "\n" +
                        "== `GET rest/res1`\n" +
                        "\n" +
                        "=== Request\n" +
                        "_No body_ + \n" +
                        "\n" +
                        "=== Response\n" +
                        "*Content-Type*: `\\*/*`\n" +
                        "\n" +
                        "==== `200 OK`\n" +
                        "*Response Body*: (`javax.json.JsonArray`) + \n" +
                        "`application/xml`: `[\"string\",0]` + \n" +
                        "`application/json`: `[\"string\",0]` + \n\n");

        representation = new TypeRepresentation("javax.json.JsonArray");
        representation.getRepresentations().put("application/json", Json.createArrayBuilder().add(Json.createObjectBuilder().add("key", "string")).add(Json.createObjectBuilder().add("key", "string")).build());
        add(data, ResourcesBuilder.withBase("rest")
                        .andResource("res1", ResourceMethodBuilder.withMethod(HttpMethod.GET)
                                .andResponse(200, ResponseBuilder.withResponseBody(representation).build()).build()).build(),
                "= REST resources\n" +
                        "v0.1\n" +
                        "\n" +
                        "== `GET rest/res1`\n" +
                        "\n" +
                        "=== Request\n" +
                        "_No body_ + \n" +
                        "\n" +
                        "=== Response\n" +
                        "*Content-Type*: `\\*/*`\n" +
                        "\n" +
                        "==== `200 OK`\n" +
                        "*Response Body*: (`javax.json.JsonArray`) + \n" +
                        "`application/json`: `[{\"key\":\"string\"},{\"key\":\"string\"}]` + \n\n");

        representation = new TypeRepresentation("de.sebastian_daschner.test.Model");
        representation.getRepresentations().put("application/json", Json.createObjectBuilder().add("name", "string").add("value", 0).build());
        add(data, ResourcesBuilder.withBase("rest")
                        .andResource("res1", ResourceMethodBuilder.withMethod(HttpMethod.GET)
                                .andResponse(200, ResponseBuilder.withResponseBody(representation).build()).build()).build(),
                "= REST resources\n" +
                        "v0.1\n" +
                        "\n" +
                        "== `GET rest/res1`\n" +
                        "\n" +
                        "=== Request\n" +
                        "_No body_ + \n" +
                        "\n" +
                        "=== Response\n" +
                        "*Content-Type*: `\\*/*`\n" +
                        "\n" +
                        "==== `200 OK`\n" +
                        "*Response Body*: (`de.sebastian_daschner.test.Model`) + \n" +
                        "`application/json`: `{\"name\":\"string\",\"value\":0}` + \n\n");

        representation = new TypeRepresentation("de.sebastian_daschner.test.Model");
        representation.getRepresentations().put("application/json", Json.createArrayBuilder().add(Json.createObjectBuilder().add("name", "string").add("value", 0)).build());
        add(data, ResourcesBuilder.withBase("rest")
                        .andResource("res1", ResourceMethodBuilder.withMethod(HttpMethod.POST).andRequestBodyType(representation).andAcceptMediaTypes("application/json")
                                .andResponse(201, ResponseBuilder.newBuilder().andHeaders("Location").build()).build()).build(),
                "= REST resources\n" +
                        "v0.1\n" +
                        "\n" +
                        "== `POST rest/res1`\n" +
                        "\n" +
                        "=== Request\n" +
                        "*Content-Type*: `application/json` + \n" +
                        "*Request Body*: (`de.sebastian_daschner.test.Model`) + \n" +
                        "`application/json`: `[{\"name\":\"string\",\"value\":0}]` + \n" +
                        "\n" +
                        "=== Response\n" +
                        "*Content-Type*: `\\*/*`\n" +
                        "\n" +
                        "==== `201 Created`\n" +
                        "*Header*: `Location` + \n\n");

        add(data, ResourcesBuilder.withBase("rest")
                        .andResource("res1", ResourceMethodBuilder.withMethod(HttpMethod.POST).andRequestBodyType(representation).andAcceptMediaTypes("application/json")
                                .andResponse(201, ResponseBuilder.newBuilder().andHeaders("Location").build()).build())
                        .andResource("res2", ResourceMethodBuilder.withMethod(HttpMethod.GET).andResponse(200, ResponseBuilder.newBuilder().build()).build()).build(),
                "= REST resources\n" +
                        "v0.1\n" +
                        "\n" +
                        "== `POST rest/res1`\n" +
                        "\n" +
                        "=== Request\n" +
                        "*Content-Type*: `application/json` + \n" +
                        "*Request Body*: (`de.sebastian_daschner.test.Model`) + \n" +
                        "`application/json`: `[{\"name\":\"string\",\"value\":0}]` + \n" +
                        "\n" +
                        "=== Response\n" +
                        "*Content-Type*: `\\*/*`\n" +
                        "\n" +
                        "==== `201 Created`\n" +
                        "*Header*: `Location` + \n\n" +
                        "== `GET rest/res2`\n" +
                        "\n" +
                        "=== Request\n" +
                        "_No body_ + \n" +
                        "\n" +
                        "=== Response\n" +
                        "*Content-Type*: `\\*/*`\n" +
                        "\n" +
                        "==== `200 OK`\n\n");
        return data;
    }

    public static void add(final Collection<Object[]> data, final Resources resources, final String output) {
        final Object[] objects = new Object[2];
        objects[0] = resources;
        objects[1] = output;
        data.add(objects);
    }

}