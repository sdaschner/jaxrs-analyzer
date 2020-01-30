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

import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.sebastian_daschner.jaxrs_analyzer.LogProvider;
import com.sebastian_daschner.jaxrs_analyzer.builder.ResourceMethodBuilder;
import com.sebastian_daschner.jaxrs_analyzer.builder.ResponseBuilder;
import com.sebastian_daschner.jaxrs_analyzer.model.Types;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.HttpMethod;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.ResourceMethod;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.Resources;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.Response;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeIdentifier;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeRepresentation;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

public class ProjectAnalyzerTest {

    private ProjectAnalyzer classUnderTest;
    private Path path;
    private String ignoredRootResource = "com.sebastian_daschner.jaxrs_test.IgnoredTestResources";

    @Before
    public void setUp() throws MalformedURLException {
        LogProvider.injectDebugLogger(System.out::println);

        final String testClassPath = "src/test/jaxrs-test";

        // invoke compilation for jaxrs-test classes
        final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        final StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
        final List<JavaFileObject> compilationUnits = findClassFiles(testClassPath, fileManager);

        final JavaCompiler.CompilationTask compilationTask = compiler.getTask(null, null, null, singletonList("-g"), null, compilationUnits);
        assertTrue("Could not compile test project", compilationTask.call());

        path = Paths.get(testClassPath).toAbsolutePath();

        final Set<Path> classPaths = Stream.of(System.getProperty("java.class.path").split(File.pathSeparator))
                .map(Paths::get)
                .collect(Collectors.toSet());

        classPaths.add(path);
        classUnderTest = new ProjectAnalyzer(classPaths);
    }

    @Test
    public void test() {
        final long startTime = System.currentTimeMillis();
        final Resources actualResources = classUnderTest.analyze(singleton(path), singleton(path), singleton(ignoredRootResource));
        System.out.println("Project analysis took " + (System.currentTimeMillis() - startTime) + " ms");
        final Resources expectedResources = getResources();

        assertEquals(expectedResources.getBasePath(), actualResources.getBasePath());

        assertFalse(actualResources.getResources().contains("ignored"));
        assertEquals(expectedResources.getResources(), actualResources.getResources());
        assertResourceEquals(expectedResources, actualResources);
        assertEquals(expectedResources.getTypeRepresentations().size(), actualResources.getTypeRepresentations().size());
    }

    private static void assertResourceEquals(final Resources expectedResources, final Resources actualResources) {
        actualResources.getResources().forEach(r -> {
            final Set<ResourceMethod> expectedMethods = expectedResources.getMethods(r);
            final Set<ResourceMethod> actualMethods = actualResources.getMethods(r);
            final String resourceText = "Compared resource " + r;

            actualMethods.forEach(am -> {
                final String methodText = resourceText + ", method " + am.getMethod();
                final ResourceMethod em = expectedMethods.stream().filter(m -> m.getMethod() == am.getMethod()).findAny()
                        .orElseThrow(() -> new AssertionError(am.getMethod() + " method not found for resource " + r));
                assertEquals(methodText, em.getMethodParameters(), am.getMethodParameters());
                assertEquals(methodText, em.getRequestMediaTypes(), am.getRequestMediaTypes());
                assertEquals(methodText, em.getResponseMediaTypes(), am.getResponseMediaTypes());
                assertTypeIdentifierEquals(methodText, em.getRequestBody(), am.getRequestBody(), expectedResources.getTypeRepresentations(), actualResources.getTypeRepresentations());
                assertEquals(methodText, em.getResponses().keySet(), am.getResponses().keySet());
                assertEquals(methodText, em.getDescription(), am.getDescription());
                assertEquals(methodText, em.getRequestBodyDescription(), am.getRequestBodyDescription());
                am.getResponses().entrySet().forEach(ae -> {
                    final Response ar = ae.getValue();
                    final Response er = em.getResponses().get(ae.getKey());
                    final String responseText = methodText + ", response " + ae.getKey();
                    assertEquals(responseText, er.getHeaders(), ar.getHeaders());
                    assertTypeIdentifierEquals(responseText, er.getResponseBody(), ar.getResponseBody(), expectedResources.getTypeRepresentations(), actualResources.getTypeRepresentations());
                });
            });

        });
    }

    private static void assertTypeIdentifierEquals(final String message, final TypeIdentifier expectedIdentifier, final TypeIdentifier actualIdentifier,
                                                   final Map<TypeIdentifier, TypeRepresentation> expectedResources, final Map<TypeIdentifier, TypeRepresentation> actualResources) {
        if (expectedIdentifier == null || actualIdentifier == null) {
            assertNull(message, expectedIdentifier);
            assertNull(message, actualIdentifier);
            return;
        }

        if (!expectedIdentifier.getName().startsWith("$")) {
            assertEquals(message, expectedIdentifier, actualIdentifier);
        }
        assertRepresentationEquals(message, expectedResources.get(expectedIdentifier), actualResources.get(actualIdentifier), expectedResources, actualResources);
    }

    private static void assertRepresentationEquals(final String message, final TypeRepresentation expectedRepresentation, final TypeRepresentation actualRepresentation,
                                                   final Map<TypeIdentifier, TypeRepresentation> expectedResources, final Map<TypeIdentifier, TypeRepresentation> actualResources) {
        if (expectedRepresentation == null || actualRepresentation == null) {
            assertNull(message, expectedRepresentation);
            assertNull(message, actualRepresentation);
            return;
        }

        assertEquals(expectedRepresentation.getClass(), actualRepresentation.getClass());
        if (expectedRepresentation instanceof TypeRepresentation.CollectionTypeRepresentation) {
            assertRepresentationEquals(message, ((TypeRepresentation.CollectionTypeRepresentation) expectedRepresentation).getRepresentation(),
                    ((TypeRepresentation.CollectionTypeRepresentation) actualRepresentation).getRepresentation(), expectedResources, actualResources);
        } else {
            final Map<String, TypeIdentifier> expectedProperties = ((TypeRepresentation.ConcreteTypeRepresentation) expectedRepresentation).getProperties();
            final Map<String, TypeIdentifier> actualProperties = ((TypeRepresentation.ConcreteTypeRepresentation) actualRepresentation).getProperties();
            assertEquals(message, expectedProperties.keySet(), actualProperties.keySet());
            actualProperties.forEach((k, v) -> assertTypeIdentifierEquals(message, expectedProperties.get(k), v, expectedResources, actualResources));
        }
    }

    private static Resources getResources() {
        final Resources resources = new Resources();
        Map<String, TypeIdentifier> properties;

        final TypeIdentifier stringIdentifier = TypeIdentifier.ofType(Types.STRING);

        properties = new HashMap<>();
        properties.put("id", TypeIdentifier.ofType(Types.PRIMITIVE_LONG));
        properties.put("name", stringIdentifier);
        final TypeIdentifier modelIdentifier = TypeIdentifier.ofType("Lcom/sebastian_daschner/jaxrs_test/Model;");
        final TypeRepresentation modelRepresentation = TypeRepresentation.ofConcreteBuilder().identifier(modelIdentifier).properties(properties).build();
        resources.getTypeRepresentations().put(modelIdentifier, modelRepresentation);

        final TypeIdentifier modelListIdentifier = TypeIdentifier.ofType("Ljava/util/List<+Lcom/sebastian_daschner/jaxrs_test/Model;>;");
        resources.getTypeRepresentations().put(modelListIdentifier, TypeRepresentation.ofCollection(modelListIdentifier, modelRepresentation));
        final TypeIdentifier stringArrayListIdentifier = TypeIdentifier.ofType("Ljava/util/ArrayList<Ljava/lang/String;>;");
        resources.getTypeRepresentations().put(stringArrayListIdentifier,
            TypeRepresentation.ofCollection(stringArrayListIdentifier, TypeRepresentation.ofConcreteBuilder().identifier(stringIdentifier).build()));

        resources.setBasePath("rest");

        // test
        ResourceMethod firstGet = ResourceMethodBuilder.withMethod(HttpMethod.GET).andAcceptMediaTypes("application/json")
                .andResponseMediaTypes("application/json").andResponse(200, ResponseBuilder
                        .withResponseBody(modelListIdentifier).build()).build();
        ResourceMethod firstPost = ResourceMethodBuilder.withMethod(HttpMethod.POST).andRequestBodyType(Types.STRING)
                .andAcceptMediaTypes("application/json").andResponseMediaTypes("application/json")
                .andResponse(201, ResponseBuilder.newBuilder().andHeaders("Location").build()).build();
        ResourceMethod firstPut = ResourceMethodBuilder.withMethod(HttpMethod.PUT).andRequestBodyType(modelIdentifier)
                .andAcceptMediaTypes("application/json").andResponseMediaTypes("application/json")
                .andResponse(202, ResponseBuilder.newBuilder().build()).build();
        addMethods(resources, "test", firstGet, firstPost, firstPut);//, firstDelete);

        // test/{foobar}
        ResourceMethod firstDelete = ResourceMethodBuilder.withMethod(HttpMethod.DELETE, "Deletes a test.").andPathParam("foobar", Types.STRING, null, "The foo query")
                .andAcceptMediaTypes("application/json").andResponseMediaTypes("application/json")
                .andResponse(204, ResponseBuilder.newBuilder().build()).build();
        addMethods(resources, "test/{foobar}", firstDelete);

        // test/{id}
        ResourceMethod secondGet = ResourceMethodBuilder.withMethod(HttpMethod.GET)
                .andResponse(200, ResponseBuilder.withResponseBody(modelIdentifier).build())
                .andAcceptMediaTypes("application/json").andResponseMediaTypes("application/json")
                .andPathParam("id", Types.STRING).build();
        ResourceMethod secondDelete = ResourceMethodBuilder.withMethod(HttpMethod.DELETE)
                .andAcceptMediaTypes("application/json").andResponseMediaTypes("application/json")
                .andPathParam("id", "Ljava/lang/String;")
                .andResponse(204, ResponseBuilder.newBuilder().build())
                .andResponse(404, ResponseBuilder.newBuilder().andHeaders("X-Message").build())
                .andResponse(500, ResponseBuilder.newBuilder().build()).build();
        addMethods(resources, "test/{id}", secondGet, secondDelete);

        // test/{id}/test
        ResourceMethod thirdDelete = ResourceMethodBuilder.withMethod(HttpMethod.DELETE, "Deletes another test.")
                .andAcceptMediaTypes("application/json").andResponseMediaTypes("application/json")
                .andPathParam("id", Types.STRING, null, "The ID").andQueryParam("query", Types.PRIMITIVE_INT, null, "The deletion query")
                .andResponse(204, ResponseBuilder.newBuilder().build()).build();
        addMethods(resources, "test/{id}/test", thirdDelete);

        // test/test
        ResourceMethod fourthGet = ResourceMethodBuilder.withMethod(HttpMethod.GET, "Returns a test string with plain text.").andAcceptMediaTypes("application/json")
                .andResponseMediaTypes("text/plain", "application/json").andResponse(200, ResponseBuilder.withResponseBody(stringIdentifier).build()).build();
        addMethods(resources, "test/test", fourthGet);
        
        // resourceWithoutClassLevelJavadoc/test
        ResourceMethod getResourceWithoutJavadoc = ResourceMethodBuilder.withMethod(HttpMethod.GET, "Returns a test string in json.").andAcceptMediaTypes("application/json")
                .andResponseMediaTypes("application/json").andResponse(200, ResponseBuilder.withResponseBody(stringIdentifier).build()).build();
        addMethods(resources, "resourceWithoutJavadoc/test", getResourceWithoutJavadoc);

        // complex
        ResourceMethod eighthGet = ResourceMethodBuilder.withMethod(HttpMethod.GET).andResponseMediaTypes("application/json")
                .andResponse(200, ResponseBuilder.withResponseBody(stringArrayListIdentifier).build()).build();
        ResourceMethod secondPut = ResourceMethodBuilder.withMethod(HttpMethod.PUT)
                .andResponse(204, ResponseBuilder.newBuilder().build()).build();
        addMethods(resources, "complex", eighthGet, secondPut);

        // complex/string
        ResourceMethod ninthGet = ResourceMethodBuilder.withMethod(HttpMethod.GET).andResponseMediaTypes("application/json")
                .andResponse(200, ResponseBuilder.withResponseBody(stringIdentifier).build()).build();
        addMethods(resources, "complex/string", ninthGet);

        // complex/status
        ResourceMethod fifthGet = ResourceMethodBuilder.withMethod(HttpMethod.GET)
                .andResponse(200, ResponseBuilder.withResponseBody(stringIdentifier).build()).build();
        addMethods(resources, "complex/status", fifthGet);

        // complex/{info}
        ResourceMethod sixthGet = ResourceMethodBuilder.withMethod(HttpMethod.GET)
                .andPathParam("info", Types.STRING).andResponse(200, ResponseBuilder.newBuilder().andHeaders("X-Info").build()).build();
        addMethods(resources, "complex/{info}", sixthGet);

        // complex/sub
        ResourceMethod secondPost = ResourceMethodBuilder.withMethod(HttpMethod.POST, "Creates a sub resource.").andRequestBodyType(Types.STRING, "The entity")
                .andQueryParam("query", Types.STRING, null, "The query param.")
                .andResponse(202, ResponseBuilder.newBuilder().andHeaders("X-Info").build()).build();
        addMethods(resources, "complex/sub", secondPost);

        // subsub
        addMethods(resources, "subsub", secondPost);

        // complex/sub/{name}
        ResourceMethod seventhGet = ResourceMethodBuilder.withMethod(HttpMethod.GET, "Gets a sub resource.").andPathParam("name", Types.STRING, null, "The name")
                .andQueryParam("query", Types.STRING, null, "The query param.")
                .andResponse(200, ResponseBuilder.withResponseBody(stringIdentifier).build()).build();
        addMethods(resources, "complex/sub/{name}", seventhGet);

        // subsub/{name}
        addMethods(resources, "subsub/{name}", seventhGet);

        // complex/anotherSub
        ResourceMethod thirdPost = ResourceMethodBuilder.withMethod(HttpMethod.POST, "Creates a sub resource.").andRequestBodyType(Types.STRING, "The entity")
                .andQueryParam("query", Types.STRING, null, "The query param.")
                .andResponse(202, ResponseBuilder.newBuilder().andHeaders("X-Info").build()).build();
        addMethods(resources, "complex/anotherSub", thirdPost);

        // complex/anotherSub/{name}
        ResourceMethod tenthGet = ResourceMethodBuilder.withMethod(HttpMethod.GET, "Gets a sub resource.").andPathParam("name", Types.STRING, null, "The name")
                .andQueryParam("query", Types.STRING, null, "The query param.")
                .andResponse(200, ResponseBuilder.withResponseBody(stringIdentifier).build()).build();
        addMethods(resources, "complex/anotherSub/{name}", tenthGet);

        // complex/anotherSubres
        ResourceMethod fourthPost = ResourceMethodBuilder.withMethod(HttpMethod.POST, "Creates a sub resource.").andRequestBodyType(Types.STRING, "The entity")
                .andQueryParam("query", Types.STRING, null, "The query param.")
                .andResponse(202, ResponseBuilder.newBuilder().andHeaders("X-Info").build()).build();
        addMethods(resources, "complex/anotherSubres", fourthPost);

        // complex/anotherSubres/{name}
        ResourceMethod eleventhGet = ResourceMethodBuilder.withMethod(HttpMethod.GET, "Gets a sub resource.").andPathParam("name", Types.STRING, null, "The name")
                .andQueryParam("query", Types.STRING, null, "The query param.")
                .andResponse(200, ResponseBuilder.withResponseBody(stringIdentifier).build()).build();
        addMethods(resources, "complex/anotherSubres/{name}", eleventhGet);

        // complex/auth
        ResourceMethod authGet = ResourceMethodBuilder.withMethod(HttpMethod.GET, "Creates an authorization endpoint.")
                .andHeaderParam("Authorization", Types.STRING)
                .andResponse(200, ResponseBuilder.withResponseBody(stringIdentifier).build()).build();
        addMethods(resources, "complex/auth", authGet);

        // json_tests
        final TypeIdentifier firstIdentifier = TypeIdentifier.ofDynamic();
        properties = new HashMap<>();
        properties.put("key", stringIdentifier);
        // All numbers are treat as double (JSON type number)
        properties.put("duke", TypeIdentifier.ofType(Types.DOUBLE));
        resources.getTypeRepresentations().put(firstIdentifier, TypeRepresentation.ofConcreteBuilder().identifier(firstIdentifier).properties(properties).build());
        ResourceMethod twelfthGet = ResourceMethodBuilder.withMethod(HttpMethod.GET).andResponse(200, ResponseBuilder.withResponseBody(firstIdentifier).build()).build();

        final TypeIdentifier secondIdentifier = TypeIdentifier.ofDynamic();
        properties = new HashMap<>();
        properties.put("key", stringIdentifier);
        resources.getTypeRepresentations().put(secondIdentifier, TypeRepresentation.ofConcreteBuilder().identifier(secondIdentifier).properties(properties).build());

        // TODO type should be Object because JsonArray is interpreted as collection type
        final TypeIdentifier thirdIdentifier = TypeIdentifier.ofDynamic();
        resources.getTypeRepresentations().put(thirdIdentifier, TypeRepresentation.ofCollection(thirdIdentifier, TypeRepresentation.ofConcreteBuilder().identifier(stringIdentifier).build()));
        ResourceMethod fifthPost = ResourceMethodBuilder.withMethod(HttpMethod.POST)
                .andResponse(202, ResponseBuilder.withResponseBody(secondIdentifier).build())
                .andResponse(500, ResponseBuilder.newBuilder().build())
                .andResponse(200, ResponseBuilder.withResponseBody(thirdIdentifier).build()).build();

        addMethods(resources, "json_tests", twelfthGet, fifthPost);

        // json_tests/info
        final TypeIdentifier fourthIdentifier = TypeIdentifier.ofDynamic();
        properties = new HashMap<>();
        properties.put("key", stringIdentifier);
        properties.put("duke", stringIdentifier);
        properties.put("hello", stringIdentifier);
        resources.getTypeRepresentations().put(fourthIdentifier, TypeRepresentation.ofConcreteBuilder().identifier(fourthIdentifier).properties(properties).build());
        ResourceMethod thirteenthGet = ResourceMethodBuilder.withMethod(HttpMethod.GET).andResponse(200, ResponseBuilder.withResponseBody(fourthIdentifier).build())
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
