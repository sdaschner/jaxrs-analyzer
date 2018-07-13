package com.sebastian_daschner.jaxrs_analyzer.backend.markdown;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.sebastian_daschner.jaxrs_analyzer.backend.Backend;
import com.sebastian_daschner.jaxrs_analyzer.builder.ResourceMethodBuilder;
import com.sebastian_daschner.jaxrs_analyzer.builder.ResourcesBuilder;
import com.sebastian_daschner.jaxrs_analyzer.builder.ResponseBuilder;
import com.sebastian_daschner.jaxrs_analyzer.model.Types;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.HttpMethod;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.Project;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.Resources;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeIdentifier;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeRepresentation;

import static java.util.Collections.singletonMap;

import static org.junit.Assert.assertEquals;

import static com.sebastian_daschner.jaxrs_analyzer.analysis.results.TypeUtils.MODEL_IDENTIFIER;
import static com.sebastian_daschner.jaxrs_analyzer.backend.StringBackend.INLINE_PRETTIFY;


@RunWith(Parameterized.class)
public class MarkdownBackendTest {

    private final Backend cut;
    private final Resources resources;
    private final String expectedOutput;
    private final boolean inlinePrettify;

    public MarkdownBackendTest(Resources resources, String expectedOutput, boolean inlinePrettify) {
        cut = new MarkdownBackend();
        this.resources = resources;
        this.expectedOutput = expectedOutput;
        this.inlinePrettify = inlinePrettify;
    }

    @Test
    public void test() {
        final Project project = new Project("project name", "1.0", resources);
        cut.configure(singletonMap(INLINE_PRETTIFY, String.valueOf(inlinePrettify)));
        final String actualOutput = new String(cut.render(project));

        assertEquals(expectedOutput, actualOutput);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        final Collection<Object[]> data = new LinkedList<>();
        final TypeIdentifier stringIdentifier = TypeIdentifier.ofType(Types.STRING);
        final TypeIdentifier intIdentifier = TypeIdentifier.ofType(Types.PRIMITIVE_INT);

        TypeIdentifier identifier;
        Map<String, TypeIdentifier> properties = new HashMap<>();

        final Resources getRestRes1String = ResourcesBuilder.withBase("rest")
                .andResource("res1", ResourceMethodBuilder.withMethod(HttpMethod.GET, "Lorem Ipsum")
                        .andResponse(200,
                                ResponseBuilder.withResponseBody(
                                        TypeIdentifier.ofType(
                                                Types.STRING))
                                        .andHeaders(
                                                "Location")
                                        .build())
                        .build())
                .build();
        add(data, getRestRes1String,
                "# REST resources of project name\n\n" +
                        "1.0\n" +
                        "\n" +
                        "## `GET rest/res1`\n" +
                        "\n" +
                        "### Description: Lorem Ipsum\n" +
                        "\n" +
                        "### Request\n" +
                        "_No body_ + \n" +
                        "\n" +
                        "### Response\n" +
                        "*Content-Type*: `\\*/*`\n" +
                        "\n" +
                        "#### `200 OK`\n" +
                        "*Header*: `Location` + \n" +
                        "*Response Body*: (`java.lang.String`)\n\n", false);

        add(data, getRestRes1String,
                "# REST resources of project name\n\n" +
                        "1.0\n" +
                        "\n" +
                        "## `GET rest/res1`\n" +
                        "\n" +
                        "### Description: Lorem Ipsum\n" +
                        "\n" +
                        "### Request\n" +
                        "_No body_ + \n" +
                        "\n" +
                        "### Response\n" +
                        "*Content-Type*: `\\*/*`\n" +
                        "\n" +
                        "#### `200 OK`\n" +
                        "*Header*: `Location` + \n" +
                        "*Response Body*: (`java.lang.String`)\n\n", true);

        identifier = TypeIdentifier.ofDynamic();
        properties.put("key", stringIdentifier);
        properties.put("another", intIdentifier);
        final Resources getRestRes1Json = ResourcesBuilder.withBase("rest")
                .andTypeRepresentation(identifier,
                        TypeRepresentation.ofConcrete(identifier, properties))
                .andResource("res1", ResourceMethodBuilder.withMethod(HttpMethod.GET)
                        .andResponse(200,
                                ResponseBuilder.withResponseBody(
                                        identifier)
                                        .build())
                        .build())
                .build();
        add(data, getRestRes1Json,
                "# REST resources of project name\n\n" +
                        "1.0\n" +
                        "\n" +
                        "## `GET rest/res1`\n" +
                        "\n" +
                        "### Request\n" +
                        "_No body_ + \n" +
                        "\n" +
                        "### Response\n" +
                        "*Content-Type*: `\\*/*`\n" +
                        "\n" +
                        "#### `200 OK`\n" + "*Response Body*: (`javax.json.Json`)\n\n```javascript\n" + "{\"another\":0,\"key\":\"string\"}\n" + "```\n\n\n\n", false);
        add(data, getRestRes1Json,
                "# REST resources of project name\n\n" +
                        "1.0\n" +
                        "\n" +
                        "## `GET rest/res1`\n" +
                        "\n" +
                        "### Request\n" +
                        "_No body_ + \n" +
                        "\n" +
                        "### Response\n" +
                        "*Content-Type*: `\\*/*`\n" +
                        "\n" +
                        "#### `200 OK`\n" + "*Response Body*: (`javax.json.Json`)\n\n```javascript\n" + "{\n" + "    \"another\": 0,\n" + "    \"key\": \"string\"\n" + "}\n" + "```\n\n\n\n", true);

        identifier = TypeIdentifier.ofDynamic();
        properties = new HashMap<>();
        properties.put("key", stringIdentifier);
        properties.put("another", intIdentifier);
        add(data, ResourcesBuilder.withBase("rest")
                        .andTypeRepresentation(identifier, TypeRepresentation.ofCollection(identifier, TypeRepresentation.ofConcrete(TypeIdentifier.ofDynamic(), properties)))
                        .andResource("res1", ResourceMethodBuilder.withMethod(HttpMethod.GET)
                                .andResponse(200, ResponseBuilder.withResponseBody(identifier).build()).build()).build(),
                "# REST resources of project name\n\n" +
                        "1.0\n" +
                        "\n" +
                        "## `GET rest/res1`\n" +
                        "\n" +
                        "### Request\n" +
                        "_No body_ + \n" +
                        "\n" +
                        "### Response\n" +
                        "*Content-Type*: `\\*/*`\n" +
                        "\n" +
                        "#### `200 OK`\n" + "*Response Body*: (`javax.json.Json`)\n\n```javascript\n" + "[{\"another\":0,\"key\":\"string\"}]\n" + "```\n\n\n\n", false);

        identifier = TypeIdentifier.ofDynamic();
        add(data, ResourcesBuilder.withBase("rest")
                        .andTypeRepresentation(identifier, TypeRepresentation.ofCollection(identifier, TypeRepresentation.ofConcrete(stringIdentifier)))
                        .andResource("res1", ResourceMethodBuilder.withMethod(HttpMethod.GET)
                                .andResponse(200, ResponseBuilder.withResponseBody(identifier).build()).build()).build(),
                "# REST resources of project name\n\n" +
                        "1.0\n" +
                        "\n" +
                        "## `GET rest/res1`\n" +
                        "\n" +
                        "### Request\n" +
                        "_No body_ + \n" +
                        "\n" +
                        "### Response\n" +
                        "*Content-Type*: `\\*/*`\n" +
                        "\n" +
                        "#### `200 OK`\n" +
                        "*Response Body*: (`javax.json.Json`)\n\n" +
                        "```javascript\n" + "[\"string\"]\n" + "```\n\n\n\n", false);

        identifier = TypeIdentifier.ofDynamic();
        properties = new HashMap<>();
        properties.put("key", stringIdentifier);
        add(data, ResourcesBuilder.withBase("rest")
                        .andTypeRepresentation(identifier, TypeRepresentation.ofCollection(identifier, TypeRepresentation.ofConcrete(TypeIdentifier.ofDynamic(), properties)))
                        .andResource("res1", ResourceMethodBuilder.withMethod(HttpMethod.GET)
                                .andResponse(200, ResponseBuilder.withResponseBody(identifier).build()).build()).build(),
                "# REST resources of project name\n\n" +
                        "1.0\n" +
                        "\n" +
                        "## `GET rest/res1`\n" +
                        "\n" +
                        "### Request\n" +
                        "_No body_ + \n" +
                        "\n" +
                        "### Response\n" +
                        "*Content-Type*: `\\*/*`\n" +
                        "\n" +
                        "#### `200 OK`\n" +
                        "*Response Body*: (`javax.json.Json`)\n\n" +
                        "```javascript\n" + "[{\"key\":\"string\"}]\n" + "```\n\n\n\n", false);

        properties = new HashMap<>();
        properties.put("name", stringIdentifier);
        properties.put("value", intIdentifier);
        add(data, ResourcesBuilder.withBase("rest")
                        .andTypeRepresentation(MODEL_IDENTIFIER, TypeRepresentation.ofConcrete(MODEL_IDENTIFIER, properties))
                        .andResource("res1", ResourceMethodBuilder.withMethod(HttpMethod.GET)
                                .andResponse(200, ResponseBuilder.withResponseBody(MODEL_IDENTIFIER).build()).build()).build(),
                "# REST resources of project name\n\n" +
                        "1.0\n" +
                        "\n" +
                        "## `GET rest/res1`\n" +
                        "\n" +
                        "### Request\n" +
                        "_No body_ + \n" +
                        "\n" +
                        "### Response\n" +
                        "*Content-Type*: `\\*/*`\n" +
                        "\n" +
                        "#### `200 OK`\n" +
                        "*Response Body*: (`com.sebastian_daschner.test.Model`)\n\n" +
                        "```javascript\n" + "{\"name\":\"string\",\"value\":0}\n" + "```\n\n\n\n", false);

        identifier = TypeIdentifier.ofType("Ljava/util/List<Lcom/sebastian_daschner/test/Model;>;");
        properties = new HashMap<>();
        properties.put("name", stringIdentifier);
        properties.put("value", intIdentifier);
        add(data, ResourcesBuilder.withBase("rest")
                        .andTypeRepresentation(identifier, TypeRepresentation.ofCollection(identifier, TypeRepresentation.ofConcrete(MODEL_IDENTIFIER, properties)))
                        .andResource("res1", ResourceMethodBuilder.withMethod(HttpMethod.GET)
                                .andResponse(200, ResponseBuilder.withResponseBody(identifier).build()).build()).build(),
                "# REST resources of project name\n\n" +
                        "1.0\n" +
                        "\n" +
                        "## `GET rest/res1`\n" +
                        "\n" +
                        "### Request\n" +
                        "_No body_ + \n" +
                        "\n" +
                        "### Response\n" +
                        "*Content-Type*: `\\*/*`\n" +
                        "\n" +
                        "#### `200 OK`\n" +
                        "*Response Body*: (Collection of `com.sebastian_daschner.test.Model`)\n\n" +
                        "```javascript\n" + "[{\"name\":\"string\",\"value\":0}]\n" + "```\n\n\n\n", false);

        add(data, ResourcesBuilder.withBase("rest")
                        .andTypeRepresentation(identifier, TypeRepresentation.ofCollection(identifier, TypeRepresentation.ofConcrete(MODEL_IDENTIFIER, properties)))
                        .andResource("res1", ResourceMethodBuilder.withMethod(HttpMethod.POST).andRequestBodyType(identifier).andFormParam("form", MODEL_IDENTIFIER.getType())
                                .andAcceptMediaTypes("application/json").andResponse(201, ResponseBuilder.newBuilder().andHeaders("Location").build()).build()).build(),
                "# REST resources of project name\n\n" +
                        "1.0\n" +
                        "\n" +
                        "## `POST rest/res1`\n" +
                        "\n" +
                        "### Request\n" +
                        "*Content-Type*: `application/json` + \n" +
                        "*Request Body*: (Collection of `com.sebastian_daschner.test.Model`)\n\n" +
                        "```javascript\n" + "[{\"name\":\"string\",\"value\":0}]\n" + "```\n\n\n" +
                        "*Form Param*: `form`, `com.sebastian_daschner.test.Model` + \n" +
                        "\n" +
                        "### Response\n" +
                        "*Content-Type*: `\\*/*`\n" +
                        "\n" +
                        "#### `201 Created`\n" +
                        "*Header*: `Location` + \n\n", false);

        add(data, ResourcesBuilder.withBase("rest")
                        .andTypeRepresentation(identifier, TypeRepresentation.ofCollection(identifier, TypeRepresentation.ofConcrete(MODEL_IDENTIFIER, properties)))
                        .andResource("res1", ResourceMethodBuilder.withMethod(HttpMethod.POST).andRequestBodyType(identifier).andQueryParam("query", Types.PRIMITIVE_INT)
                                .andAcceptMediaTypes("application/json").andResponse(201, ResponseBuilder.newBuilder().andHeaders("Location").build()).build())
                        .andResource("res2", ResourceMethodBuilder.withMethod(HttpMethod.GET).andResponse(200, ResponseBuilder.newBuilder().build()).build()).build(),
                "# REST resources of project name\n\n" +
                        "1.0\n" +
                        "\n" +
                        "## `POST rest/res1`\n" +
                        "\n" +
                        "### Request\n" +
                        "*Content-Type*: `application/json` + \n" +
                        "*Request Body*: (Collection of `com.sebastian_daschner.test.Model`)\n\n" +
                        "```javascript\n" +
                        "[{\"name\":\"string\",\"value\":0}]\n" +
                        "```\n\n\n" +
                        "*Query Param*: `query`, `int` + \n" +
                        "\n" +
                        "### Response\n" +
                        "*Content-Type*: `\\*/*`\n" +
                        "\n" +
                        "#### `201 Created`\n" +
                        "*Header*: `Location` + \n\n" +
                        "## `GET rest/res2`\n" +
                        "\n" +
                        "### Request\n" +
                        "_No body_ + \n" +
                        "\n" +
                        "### Response\n" +
                        "*Content-Type*: `\\*/*`\n" +
                        "\n" +
                        "#### `200 OK`\n\n", false);
        // deprecated method test
        add(data, ResourcesBuilder.withBase("rest")
                        .andResource("res19", ResourceMethodBuilder.withMethod(HttpMethod.GET).andDeprecated(true)
                                .andResponse(200, ResponseBuilder.withResponseBody(TypeIdentifier.ofType(Types.STRING)).andHeaders("Location").build()).build()).build(),
                "# REST resources of project name\n\n" +
                        "1.0\n" +
                        "\n" +
                        "## `GET rest/res19`\n" +
                        "\n" +
                        "CAUTION: deprecated\n" +
                        "\n" +
                        "### Request\n" +
                        "_No body_ + \n" +
                        "\n" +
                        "### Response\n" +
                        "*Content-Type*: `\\*/*`\n" +
                        "\n" +
                        "#### `200 OK`\n" +
                        "*Header*: `Location` + \n" +
                        "*Response Body*: (`java.lang.String`)\n\n", false);
        return data;
    }

    public static void add(final Collection<Object[]> data, final Resources resources, final String output,
                           final boolean pretty) {
        final Object[] objects = new Object[3];
        objects[0] = resources;
        objects[1] = output;
        objects[2] = pretty;
        data.add(objects);
    }

}
