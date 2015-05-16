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

package com.sebastian_daschner.jaxrs_analyzer.analysis;

import com.sebastian_daschner.jaxrs_analyzer.builder.ResourceMethodBuilder;
import com.sebastian_daschner.jaxrs_analyzer.builder.ResponseBuilder;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.HttpMethod;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.ResourceMethod;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.Resources;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeRepresentation;
import javassist.NotFoundException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.json.Json;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.assertTrue;

public class ProjectAnalyzerTest {

    private ProjectAnalyzer classUnderTest;
    private Path path;

    @Before
    public void setUp() throws MalformedURLException, NotFoundException {

        final String testClassPath = "src/test/jaxrs-test";

        // invoke compilation for jaxrs-test classes
        final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        final StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
        final List<JavaFileObject> compilationUnits = findClassFiles(testClassPath, fileManager);

        final JavaCompiler.CompilationTask compilationTask = compiler.getTask(null, null, null, null, null, compilationUnits);
        assertTrue("Could not compile test project", compilationTask.call());

        this.path = Paths.get(testClassPath).toAbsolutePath();
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
//        ResourceMethod firstDelete = ResourceMethodBuilder.withMethod(HttpMethod.DELETE)
//                .andAcceptMediaTypes("application/json").andResponseMediaTypes("application/json")
//                .andResponse(204, ResponseBuilder.newBuilder().build()).build();
        addMethods(resources, "test", firstGet, firstPost, firstPut);//, firstDelete);

        // test/{foobar}
        ResourceMethod firstDelete = ResourceMethodBuilder.withMethod(HttpMethod.DELETE).andPathParam("foobar", "java.lang.String")
                .andAcceptMediaTypes("application/json").andResponseMediaTypes("application/json")
                .andResponse(204, ResponseBuilder.newBuilder().build()).build();
        addMethods(resources, "test/{foobar}", firstDelete);

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
                .andResponse(200, ResponseBuilder.withResponseBody(new TypeRepresentation("java.lang.String")).build()).build();
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
        ResourceMethod secondPost = ResourceMethodBuilder.withMethod(HttpMethod.POST).andRequestBodyType("java.lang.String").andQueryParam("query", "java.lang.String")
                .andResponse(202, ResponseBuilder.newBuilder().andHeaders("X-Info").build()).build();
        addMethods(resources, "complex/sub", secondPost);

        // subsub
        addMethods(resources, "subsub", secondPost);

        // complex/sub/{name}
        ResourceMethod seventhGet = ResourceMethodBuilder.withMethod(HttpMethod.GET).andPathParam("name", "java.lang.String").andQueryParam("query", "java.lang.String")
                .andResponse(200, ResponseBuilder.withResponseBody(new TypeRepresentation("java.lang.String")).build()).build();
        addMethods(resources, "complex/sub/{name}", seventhGet);

        // subsub/{name}
        addMethods(resources, "subsub/{name}", seventhGet);

        // complex/anotherSub
        ResourceMethod thirdPost = ResourceMethodBuilder.withMethod(HttpMethod.POST).andRequestBodyType("java.lang.String").andQueryParam("query", "java.lang.String")
                .andResponse(202, ResponseBuilder.newBuilder().andHeaders("X-Info").build()).build();
        addMethods(resources, "complex/anotherSub", thirdPost);

        // complex/anotherSub/{name}
        ResourceMethod tenthGet = ResourceMethodBuilder.withMethod(HttpMethod.GET).andPathParam("name", "java.lang.String").andQueryParam("query", "java.lang.String")
                .andResponse(200, ResponseBuilder.withResponseBody(new TypeRepresentation("java.lang.String")).build()).build();
        addMethods(resources, "complex/anotherSub/{name}", tenthGet);

        // complex/anotherSubres
        ResourceMethod fourthPost = ResourceMethodBuilder.withMethod(HttpMethod.POST).andRequestBodyType("java.lang.String").andQueryParam("query", "java.lang.String")
                .andResponse(202, ResponseBuilder.newBuilder().andHeaders("X-Info").build()).build();
        addMethods(resources, "complex/anotherSubres", fourthPost);

        // complex/anotherSubres/{name}
        ResourceMethod eleventhGet = ResourceMethodBuilder.withMethod(HttpMethod.GET).andPathParam("name", "java.lang.String").andQueryParam("query", "java.lang.String")
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
                .andResponse(500, ResponseBuilder.newBuilder().build())
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

    private static List<JavaFileObject> findClassFiles(final String classPath, final StandardJavaFileManager fileManager, final String... packages) {
        final Iterable<? extends JavaFileObject> fileObjects = fileManager.getJavaFileObjects(Paths.get(classPath, packages).toFile().listFiles((dir, name) -> name.endsWith(".java")));
        List<JavaFileObject> classFiles = new LinkedList<>();
        fileObjects.forEach(classFiles::add);

        Stream.of(Paths.get(classPath, packages).toFile().listFiles(File::isDirectory)).map(File::getName)
                .map(n -> {
                    final String[] packagesNames = Arrays.copyOf(packages, packages.length + 1);
                    packagesNames[packages.length] = n;
                    return packagesNames;
                }).forEach(p -> classFiles.addAll(findClassFiles(classPath, fileManager, p)));

        return classFiles;
    }

}
