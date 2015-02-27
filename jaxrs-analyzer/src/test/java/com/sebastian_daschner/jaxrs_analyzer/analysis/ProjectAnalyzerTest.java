package com.sebastian_daschner.jaxrs_analyzer.analysis;

import com.sebastian_daschner.jaxrs_analyzer.model.rest.HttpMethod;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeRepresentation;
import com.sebastian_daschner.jaxrs_analyzer.builder.ResourceMethodBuilder;
import com.sebastian_daschner.jaxrs_analyzer.builder.ResponseBuilder;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.ResourceMethod;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.Resources;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.json.Json;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class ProjectAnalyzerTest {

    private ProjectAnalyzer classUnderTest;
    private Path path;

    @Before
    public void setUp() throws MalformedURLException {
        // workaround to find correct path in both the IDE test and maven test
        final String prefix = Stream.of(Paths.get(".").toFile().list()).anyMatch("jaxrs-test"::equals) ? "" : "../";
        final String binPath = prefix + "jaxrs-test/target/classes";

        this.path = Paths.get(binPath).toAbsolutePath();
        this.classUnderTest = new ProjectAnalyzer();
    }

    @Test
    public void test() {
        final long startTime = System.currentTimeMillis();
        final Resources actualResources = classUnderTest.analyze(path);
        System.out.println("Project analysis took " + (System.currentTimeMillis() - startTime) + " ms");
        final Resources expectedResources = getResources();

        // for debugging purposes
        if (!expectedResources.getResources().equals(actualResources.getResources()))
            System.out.println("no match: " + expectedResources.getResources() + " <-> " + actualResources.getResources());

        actualResources.getResources().stream().forEach(r -> {
            if (!actualResources.getMethods(r).equals(expectedResources.getMethods(r)))
                System.out.println("no match: " + r + ": " + expectedResources.getMethods(r) + " <-> " + actualResources.getMethods(r));
        });

        Assert.assertEquals("failed for project", expectedResources, actualResources);
    }

    public static Resources getResources() {
        final Resources resources = new Resources();

        final TypeRepresentation modelRepresentation = new TypeRepresentation("com.sebastian_daschner.jaxrs_test.Model");
        modelRepresentation.getRepresentations().put("application/json", Json.createObjectBuilder().add("id", 0).add("name", "string").build());
        final TypeRepresentation modelCollectionRepresentation = new TypeRepresentation("com.sebastian_daschner.jaxrs_test.Model");
        modelCollectionRepresentation.getRepresentations().put("application/json",
                Json.createArrayBuilder().add(Json.createObjectBuilder().add("id", 0).add("name", "string")).build());
        final TypeRepresentation stringCollectionRepresentation = new TypeRepresentation("java.lang.String");
        stringCollectionRepresentation.getRepresentations().put("application/json", Json.createArrayBuilder().add("string").build());

        resources.setBasePath("rest");

        // test
        ResourceMethod firstGet = ResourceMethodBuilder.withMethod(HttpMethod.GET).andAcceptMediaTypes("application/json")
                .andResponseMediaTypes("application/json").andResponse(200, ResponseBuilder
                        .withResponseBody(modelCollectionRepresentation).build()).build();
        ResourceMethod firstPost = ResourceMethodBuilder.withMethod(HttpMethod.POST).andRequestBodyType("java.lang.String")
                .andAcceptMediaTypes("application/json").andResponseMediaTypes("application/json")
                .andResponse(201, ResponseBuilder.newBuilder().andHeaders("Location").build()).build();
        ResourceMethod firstPut = ResourceMethodBuilder.withMethod(HttpMethod.PUT).andRequestBodyType(modelRepresentation)
                .andAcceptMediaTypes("application/json").andResponseMediaTypes("application/json")
                .andResponse(202, ResponseBuilder.newBuilder().build()).build();
        addMethods(resources, "test", firstGet, firstPost, firstPut);

        // test/{id}
        ResourceMethod secondGet = ResourceMethodBuilder.withMethod(HttpMethod.GET)
                .andResponse(200, ResponseBuilder.withResponseBody(modelRepresentation).build())
                .andAcceptMediaTypes("application/json").andResponseMediaTypes("application/json")
                .andPathParam("id", "java.lang.String").build();
        ResourceMethod secondDelete = ResourceMethodBuilder.withMethod(HttpMethod.DELETE)
                .andAcceptMediaTypes("application/json").andResponseMediaTypes("application/json").andPathParam("id", "java.lang.String")
                .andResponse(204, ResponseBuilder.newBuilder().build())
                .andResponse(404, ResponseBuilder.newBuilder().andHeaders("X-Message").build()).build();
        addMethods(resources, "test/{id}", secondGet, secondDelete);

        // test/{id}/test
        ResourceMethod thirdDelete = ResourceMethodBuilder.withMethod(HttpMethod.DELETE)
                .andAcceptMediaTypes("application/json").andResponseMediaTypes("application/json").andPathParam("id", "java.lang.String")
                .andResponse(204, ResponseBuilder.newBuilder().build()).build();
        addMethods(resources, "test/{id}/test", thirdDelete);

        // test/test
        ResourceMethod fourthGet = ResourceMethodBuilder.withMethod(HttpMethod.GET).andAcceptMediaTypes("application/json")
                .andResponseMediaTypes("text/plain").andResponse(200, ResponseBuilder.withResponseBody(new TypeRepresentation("java.lang.String")).build()).build();
        addMethods(resources, "test/test", fourthGet);

        // complex
        ResourceMethod eighthGet = ResourceMethodBuilder.withMethod(HttpMethod.GET).andResponseMediaTypes("application/json")
                .andResponse(200, ResponseBuilder.withResponseBody(stringCollectionRepresentation).build()).build();
        ResourceMethod secondPut = ResourceMethodBuilder.withMethod(HttpMethod.PUT)
                .andResponse(204, ResponseBuilder.newBuilder().build()).build();
        addMethods(resources, "complex", eighthGet, secondPut);

        // complex/string
        ResourceMethod ninthGet = ResourceMethodBuilder.withMethod(HttpMethod.GET).andResponseMediaTypes("application/json")
                // TODO change representation to String
                .andResponse(200, ResponseBuilder.withResponseBody(new TypeRepresentation("java.lang.Object")).build()).build();
        addMethods(resources, "complex/string", ninthGet);

        // complex/status
        ResourceMethod fifthGet = ResourceMethodBuilder.withMethod(HttpMethod.GET)
                .andResponse(200, ResponseBuilder.withResponseBody(new TypeRepresentation("java.lang.String")).build()).build();
        addMethods(resources, "complex/status", fifthGet);

        // complex/{info}
        ResourceMethod sixthGet = ResourceMethodBuilder.withMethod(HttpMethod.GET)
                .andPathParam("info", "java.lang.String").andResponse(200, ResponseBuilder.newBuilder().andHeaders("X-Info").build()).build();
        addMethods(resources, "complex/{info}", sixthGet);

        // complex/sub
        ResourceMethod secondPost = ResourceMethodBuilder.withMethod(HttpMethod.POST).andRequestBodyType("java.lang.String")
                .andResponse(202, ResponseBuilder.newBuilder().andHeaders("X-Info").build()).build();
        addMethods(resources, "complex/sub", secondPost);

        // subsub
        addMethods(resources, "subsub", secondPost);

        // complex/sub/{name}
        ResourceMethod seventhGet = ResourceMethodBuilder.withMethod(HttpMethod.GET).andPathParam("name", "java.lang.String")
                .andResponse(200, ResponseBuilder.withResponseBody(new TypeRepresentation("java.lang.String")).build()).build();
        addMethods(resources, "complex/sub/{name}", seventhGet);

        // subsub/{name}
        addMethods(resources, "subsub/{name}", seventhGet);

        // complex/anotherSub
        ResourceMethod thirdPost = ResourceMethodBuilder.withMethod(HttpMethod.POST).andRequestBodyType("java.lang.String")
                .andResponse(202, ResponseBuilder.newBuilder().andHeaders("X-Info").build()).build();
        addMethods(resources, "complex/anotherSub", thirdPost);

        // complex/anotherSub/{name}
        ResourceMethod tenthGet = ResourceMethodBuilder.withMethod(HttpMethod.GET).andPathParam("name", "java.lang.String")
                .andResponse(200, ResponseBuilder.withResponseBody(new TypeRepresentation("java.lang.String")).build()).build();
        addMethods(resources, "complex/anotherSub/{name}", tenthGet);

        // complex/anotherSubres
        ResourceMethod fourthPost = ResourceMethodBuilder.withMethod(HttpMethod.POST).andRequestBodyType("java.lang.String")
                .andResponse(202, ResponseBuilder.newBuilder().andHeaders("X-Info").build()).build();
        addMethods(resources, "complex/anotherSubres", fourthPost);

        // complex/anotherSubres/{name}
        ResourceMethod eleventhGet = ResourceMethodBuilder.withMethod(HttpMethod.GET).andPathParam("name", "java.lang.String")
                .andResponse(200, ResponseBuilder.withResponseBody(new TypeRepresentation("java.lang.String")).build()).build();
        addMethods(resources, "complex/anotherSubres/{name}", eleventhGet);

        // json_tests
        final TypeRepresentation firstResponseBody = new TypeRepresentation("javax.json.JsonObject");
        firstResponseBody.getRepresentations().put("application/json", Json.createObjectBuilder().add("key", "string").add("duke", 0).build());
        ResourceMethod twelfthGet = ResourceMethodBuilder.withMethod(HttpMethod.GET)
                .andResponse(200, ResponseBuilder.withResponseBody(firstResponseBody).build()).build();

        final TypeRepresentation secondResponseBody = new TypeRepresentation("javax.json.JsonObject");
        secondResponseBody.getRepresentations().put("application/json", Json.createObjectBuilder().add("key", "string").build());
        final TypeRepresentation thirdResponseBody = new TypeRepresentation("javax.json.JsonArray");
        thirdResponseBody.getRepresentations().put("application/json", Json.createArrayBuilder().add("string").add(0).build());
        ResourceMethod fifthPost = ResourceMethodBuilder.withMethod(HttpMethod.POST)
                .andResponse(202, ResponseBuilder.withResponseBody(secondResponseBody).build())
                .andResponse(200, ResponseBuilder.withResponseBody(thirdResponseBody).build()).build();

        addMethods(resources, "json_tests", twelfthGet, fifthPost);

        // json_tests/info
        final TypeRepresentation fourthResponseBody = new TypeRepresentation("javax.json.JsonObject");
        fourthResponseBody.getRepresentations().put("application/json",
                Json.createObjectBuilder().add("key", "string").add("duke", "string").add("hello", "string").build());
        ResourceMethod thirteenthGet = ResourceMethodBuilder.withMethod(HttpMethod.GET).andResponse(200, ResponseBuilder.withResponseBody(fourthResponseBody).build())
                .build();
        addMethods(resources, "json_tests/info", thirteenthGet);

        return resources;
    }

    private static void addMethods(final Resources resources, final String path, final ResourceMethod... methods) {
        Stream.of(methods).forEach(m -> resources.addMethod(path, m));
    }

}
