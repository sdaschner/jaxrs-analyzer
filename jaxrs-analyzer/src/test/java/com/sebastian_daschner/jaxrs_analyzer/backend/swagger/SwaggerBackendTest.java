package com.sebastian_daschner.jaxrs_analyzer.backend.swagger;

import com.sebastian_daschner.jaxrs_analyzer.builder.ResourceMethodBuilder;
import com.sebastian_daschner.jaxrs_analyzer.builder.ResourcesBuilder;
import com.sebastian_daschner.jaxrs_analyzer.builder.ResponseBuilder;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.HttpMethod;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.Resources;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeRepresentation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import javax.json.Json;
import java.util.Collection;
import java.util.LinkedList;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class SwaggerBackendTest {

    private final SwaggerBackend cut;
    private final Resources resources;
    private final String expectedOutput;

    public SwaggerBackendTest(final Resources resources, final String expectedOutput) {
        cut = new SwaggerBackend();
        this.resources = resources;
        this.expectedOutput = expectedOutput;
    }

    @Test
    public void test() {
        final String actualOutput = cut.render(resources);

        assertEquals(expectedOutput, actualOutput);
//        System.out.println(expectedOutput);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        final Collection<Object[]> data = new LinkedList<>();

        TypeRepresentation representation;

        add(data, ResourcesBuilder.withBase("rest")
                        .andResource("res1", ResourceMethodBuilder.withMethod(HttpMethod.GET)
                                .andResponse(200, ResponseBuilder.withResponseBody(new TypeRepresentation("java.lang.String")).andHeaders("Location").build()).build()).build(),
                "{\"swagger\":\"2.0\",\"info\":{\"version\":\"0.1-SNAPSHOT\",\"title\":\"project\"},\"host\":\"example.com\",\"basePath\":\"/rest\",\"schemas\":[\"http\"],\"paths\":{\"/res1\":{\"get\":{\"consumes\":[],\"produces\":[],\"parameters\":[],\"responses\":{\"200\":{\"description\":\"OK\",\"headers\":{\"Location\":{\"type\":\"string\"}},\"schema\":{\"type\":\"string\"}}}}}},\"definitions\":{}}");

        representation = new TypeRepresentation("javax.json.JsonObject");
        representation.getRepresentations().put("application/json", Json.createObjectBuilder().add("key", "string").add("another", 0).build());
        add(data, ResourcesBuilder.withBase("rest")
                        .andResource("res1", ResourceMethodBuilder.withMethod(HttpMethod.GET)
                                .andResponse(200, ResponseBuilder.withResponseBody(representation).build()).build()).build(),
                "{\"swagger\":\"2.0\",\"info\":{\"version\":\"0.1-SNAPSHOT\",\"title\":\"project\"},\"host\":\"example.com\",\"basePath\":\"/rest\",\"schemas\":[\"http\"],\"paths\":{\"/res1\":{\"get\":{\"consumes\":[],\"produces\":[],\"parameters\":[],\"responses\":{\"200\":{\"description\":\"OK\",\"headers\":{},\"schema\":{\"$ref\":\"definition#1\"}}}}}},\"definitions\":{\"definition#1\":{\"properties\":{\"key\":{\"type\":\"string\"},\"another\":{\"type\":\"number\"}}}}}");

        representation = new TypeRepresentation("javax.json.JsonObject");
        representation.getRepresentations().put("application/json", Json.createArrayBuilder().add(Json.createObjectBuilder().add("key", "string").add("another", 0)).build());
        add(data, ResourcesBuilder.withBase("rest")
                        .andResource("res1", ResourceMethodBuilder.withMethod(HttpMethod.GET)
                                .andResponse(200, ResponseBuilder.withResponseBody(representation).build()).build()).build(),
                "{\"swagger\":\"2.0\",\"info\":{\"version\":\"0.1-SNAPSHOT\",\"title\":\"project\"},\"host\":\"example.com\",\"basePath\":\"/rest\",\"schemas\":[\"http\"],\"paths\":{\"/res1\":{\"get\":{\"consumes\":[],\"produces\":[],\"parameters\":[],\"responses\":{\"200\":{\"description\":\"OK\",\"headers\":{},\"schema\":{\"type\":\"array\",\"items\":{\"$ref\":\"definition#1\"}}}}}}},\"definitions\":{\"definition#1\":{\"properties\":{\"key\":{\"type\":\"string\"},\"another\":{\"type\":\"number\"}}}}}");

        representation = new TypeRepresentation("javax.json.JsonArray");
        representation.getRepresentations().put("application/json", Json.createArrayBuilder().add("string").add(0).build());
        add(data, ResourcesBuilder.withBase("rest")
                        .andResource("res1", ResourceMethodBuilder.withMethod(HttpMethod.GET)
                                .andResponse(200, ResponseBuilder.withResponseBody(representation).build()).build()).build(),
                "{\"swagger\":\"2.0\",\"info\":{\"version\":\"0.1-SNAPSHOT\",\"title\":\"project\"},\"host\":\"example.com\",\"basePath\":\"/rest\",\"schemas\":[\"http\"],\"paths\":{\"/res1\":{\"get\":{\"consumes\":[],\"produces\":[],\"parameters\":[],\"responses\":{\"200\":{\"description\":\"OK\",\"headers\":{},\"schema\":{\"type\":\"array\",\"items\":[{\"type\":\"string\"},{\"type\":\"number\"}]}}}}}},\"definitions\":{}}");

        representation = new TypeRepresentation("javax.json.JsonArray");
        representation.getRepresentations().put("application/json", Json.createArrayBuilder().add(Json.createObjectBuilder().add("key", "string")).add(Json.createObjectBuilder().add("key", "string")).build());
        add(data, ResourcesBuilder.withBase("rest")
                        .andResource("res1", ResourceMethodBuilder.withMethod(HttpMethod.GET)
                                .andResponse(200, ResponseBuilder.withResponseBody(representation).build()).build()).build(),
                "{\"swagger\":\"2.0\",\"info\":{\"version\":\"0.1-SNAPSHOT\",\"title\":\"project\"},\"host\":\"example.com\",\"basePath\":\"/rest\",\"schemas\":[\"http\"],\"paths\":{\"/res1\":{\"get\":{\"consumes\":[],\"produces\":[],\"parameters\":[],\"responses\":{\"200\":{\"description\":\"OK\",\"headers\":{},\"schema\":{\"type\":\"array\",\"items\":{\"$ref\":\"definition#1\"}}}}}}},\"definitions\":{\"definition#1\":{\"properties\":{\"key\":{\"type\":\"string\"}}}}}");

        representation = new TypeRepresentation("de.sebastian_daschner.test.Model");
        representation.getRepresentations().put("application/json", Json.createObjectBuilder().add("name", "string").add("value", 0).build());
        add(data, ResourcesBuilder.withBase("rest")
                        .andResource("res1", ResourceMethodBuilder.withMethod(HttpMethod.GET)
                                .andResponse(200, ResponseBuilder.withResponseBody(representation).build()).build()).build(),
                "{\"swagger\":\"2.0\",\"info\":{\"version\":\"0.1-SNAPSHOT\",\"title\":\"project\"},\"host\":\"example.com\",\"basePath\":\"/rest\",\"schemas\":[\"http\"],\"paths\":{\"/res1\":{\"get\":{\"consumes\":[],\"produces\":[],\"parameters\":[],\"responses\":{\"200\":{\"description\":\"OK\",\"headers\":{},\"schema\":{\"$ref\":\"definition#1\"}}}}}},\"definitions\":{\"definition#1\":{\"properties\":{\"name\":{\"type\":\"string\"},\"value\":{\"type\":\"number\"}}}}}");

        representation = new TypeRepresentation("de.sebastian_daschner.test.Model");
        representation.getRepresentations().put("application/json", Json.createArrayBuilder().add(Json.createObjectBuilder().add("name", "string").add("value", 0)).build());
        add(data, ResourcesBuilder.withBase("rest")
                        .andResource("res1", ResourceMethodBuilder.withMethod(HttpMethod.POST).andRequestBodyType(representation).andAcceptMediaTypes("application/json")
                                .andResponse(201, ResponseBuilder.newBuilder().andHeaders("Location").build()).build()).build(),
                "{\"swagger\":\"2.0\",\"info\":{\"version\":\"0.1-SNAPSHOT\",\"title\":\"project\"},\"host\":\"example.com\",\"basePath\":\"/rest\",\"schemas\":[\"http\"],\"paths\":{\"/res1\":{\"post\":{\"consumes\":[\"application/json\"],\"produces\":[],\"parameters\":[{\"name\":\"body\",\"in\":\"body\",\"required\":true,\"schema\":{\"type\":\"array\",\"items\":{\"$ref\":\"definition#1\"}}}],\"responses\":{\"201\":{\"description\":\"Created\",\"headers\":{\"Location\":{\"type\":\"string\"}}}}}}},\"definitions\":{\"definition#1\":{\"properties\":{\"name\":{\"type\":\"string\"},\"value\":{\"type\":\"number\"}}}}}");

        return data;
    }

    public static void add(final Collection<Object[]> data, final Resources resources, final String output) {
        final Object[] objects = new Object[2];
        objects[0] = resources;
        objects[1] = output;
        data.add(objects);
    }

}