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

package com.sebastian_daschner.jaxrs_analyzer.analysis.results;

import com.sebastian_daschner.jaxrs_analyzer.builder.*;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.HttpMethod;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.ResourceMethod;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.Resources;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeRepresentation;
import com.sebastian_daschner.jaxrs_analyzer.model.results.ClassResult;
import com.sebastian_daschner.jaxrs_analyzer.model.results.MethodResult;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.json.Json;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ResultInterpreterTest {

    private ResultInterpreter classUnderTest;

    @Before
    public void setUp() {
        classUnderTest = new ResultInterpreter();
    }

    @Test
    public void testStandard() {
        final Resources expectedResult = new Resources();
        expectedResult.setBasePath("path");
        final ResourceMethod resourceMethod = ResourceMethodBuilder.withMethod(HttpMethod.GET)
                .andResponse(200, ResponseBuilder.withResponseBody(new TypeRepresentation("java.lang.String")).build())
                .build();
        expectedResult.addMethod("test", resourceMethod);

        final ClassResult appPathResult = ClassResultBuilder.withApplicationPath("path/").build();
        final MethodResult method = MethodResultBuilder.withResponses(HttpResponseBuilder.withStatues(200).andEntityTypes("java.lang.String").build())
                .andMethod(HttpMethod.GET).build();
        final ClassResult resClassResult = ClassResultBuilder.withResourcePath("test").andMethods(method).build();

        final Set<ClassResult> results = new HashSet<>(Arrays.asList(appPathResult, resClassResult));

        final Resources actualResult = classUnderTest.interpret(results);

        Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testSubResource() {
        final Resources expectedResult = new Resources();
        expectedResult.setBasePath("path");
        final ResourceMethod resourceGetMethod = ResourceMethodBuilder.withMethod(HttpMethod.GET)
                .andResponse(200, ResponseBuilder.withResponseBody(new TypeRepresentation("java.lang.String")).build())
                .build();
        expectedResult.addMethod("test", resourceGetMethod);
        final ResourceMethod resourcePostMethod = ResourceMethodBuilder.withMethod(HttpMethod.POST)
                .andResponse(204, ResponseBuilder.newBuilder().build()).build();
        expectedResult.addMethod("test/sub", resourcePostMethod);

        final ClassResult appPathResult = ClassResultBuilder.withApplicationPath("/path").build();
        final MethodResult method = MethodResultBuilder.withResponses(HttpResponseBuilder.withStatues(200).andEntityTypes("java.lang.String").build())
                .andMethod(HttpMethod.GET).build();
        final MethodResult subResourceLocator = MethodResultBuilder.newBuilder().andPath("sub").build();
        final MethodResult subResourceMethod = MethodResultBuilder.withResponses(HttpResponseBuilder.withStatues(204).build()).andMethod(HttpMethod.POST).build();
        subResourceLocator.setSubResource(ClassResultBuilder.withResourcePath(null).andMethods(subResourceMethod).build());
        final ClassResult resClassResult = ClassResultBuilder.withResourcePath("test").andMethods(method, subResourceLocator).build();

        final Set<ClassResult> results = new HashSet<>(Arrays.asList(appPathResult, resClassResult));

        final Resources actualResult = classUnderTest.interpret(results);

        Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testNormalizeGenericEntity() {
        final Resources expectedResult = new Resources();
        expectedResult.setBasePath("path");
        final TypeRepresentation representation = new TypeRepresentation("java.lang.String");
        representation.getRepresentations().put("application/json", Json.createArrayBuilder().add("string").build());

        final ResourceMethod resourceGetMethod = ResourceMethodBuilder.withMethod(HttpMethod.GET)
                .andResponse(200, ResponseBuilder.withResponseBody(representation).build())
                .build();
        expectedResult.addMethod("test", resourceGetMethod);

        final ClassResult appPathResult = ClassResultBuilder.withApplicationPath("path").build();
        final MethodResult method = MethodResultBuilder
                .withResponses(HttpResponseBuilder.withStatues(200).andEntityTypes("javax.ws.rs.core.GenericEntity<java.util.List<java.lang.String>>").build())
                .andMethod(HttpMethod.GET).build();
        final ClassResult resClassResult = ClassResultBuilder.withResourcePath("test").andMethods(method).build();

        final Set<ClassResult> results = new HashSet<>(Arrays.asList(appPathResult, resClassResult));

        final Resources actualResult = classUnderTest.interpret(results);

        Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testNormalizeGenericEntityNoCollection() {
        final Resources expectedResult = new Resources();
        expectedResult.setBasePath("path");
        final TypeRepresentation representation = new TypeRepresentation("java.lang.String");

        final ResourceMethod resourceGetMethod = ResourceMethodBuilder.withMethod(HttpMethod.GET)
                .andResponse(200, ResponseBuilder.withResponseBody(representation).build())
                .build();
        expectedResult.addMethod("test", resourceGetMethod);

        final ClassResult appPathResult = ClassResultBuilder.withApplicationPath("path").build();
        final MethodResult method = MethodResultBuilder
                .withResponses(HttpResponseBuilder.withStatues(200).andEntityTypes("javax.ws.rs.core.GenericEntity<java.lang.String>").build())
                .andMethod(HttpMethod.GET).build();
        final ClassResult resClassResult = ClassResultBuilder.withResourcePath("test").andMethods(method).build();

        final Set<ClassResult> results = new HashSet<>(Arrays.asList(appPathResult, resClassResult));

        final Resources actualResult = classUnderTest.interpret(results);

        Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testContentTypes() {
        final Resources expectedResult = new Resources();
        expectedResult.setBasePath("path");
        final ResourceMethod resourceGetMethod = ResourceMethodBuilder.withMethod(HttpMethod.GET)
                .andResponse(200, ResponseBuilder.withResponseBody(new TypeRepresentation("java.lang.String")).build())
                .andResponseMediaTypes("application/xml").build();
        expectedResult.addMethod("test", resourceGetMethod);

        final ClassResult appPathResult = ClassResultBuilder.withApplicationPath("path").build();
        final MethodResult method = MethodResultBuilder.withResponses(HttpResponseBuilder.withStatues(200).andEntityTypes("java.lang.String").build())
                .andMethod(HttpMethod.GET).andResponseMediaTypes("application/xml").build();
        final ClassResult resClassResult = ClassResultBuilder.withResourcePath("test").andMethods(method).build();

        final Set<ClassResult> results = new HashSet<>(Arrays.asList(appPathResult, resClassResult));

        final Resources actualResult = classUnderTest.interpret(results);

        Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testOverrideAnnotationContentType() {
        final Resources expectedResult = new Resources();
        expectedResult.setBasePath("path");
        final ResourceMethod resourceGetMethod = ResourceMethodBuilder.withMethod(HttpMethod.GET)
                .andResponse(200, ResponseBuilder.withResponseBody(new TypeRepresentation("java.lang.String")).build())
                .andResponseMediaTypes("application/json").build();
        expectedResult.addMethod("test", resourceGetMethod);

        final ClassResult appPathResult = ClassResultBuilder.withApplicationPath("path").build();
        final MethodResult method = MethodResultBuilder.withResponses(HttpResponseBuilder.withStatues(200).andContentTypes("application/json")
                .andEntityTypes("java.lang.String").build())
                .andMethod(HttpMethod.GET).andResponseMediaTypes("application/xml", "application/json").build();
        final ClassResult resClassResult = ClassResultBuilder.withResourcePath("test").andMethods(method).build();

        final Set<ClassResult> results = new HashSet<>(Arrays.asList(appPathResult, resClassResult));

        final Resources actualResult = classUnderTest.interpret(results);

        Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testNestedBasePath() {
        final Resources expectedResult = new Resources();
        expectedResult.setBasePath("path/nested");
        final ResourceMethod resourceGetMethod = ResourceMethodBuilder.withMethod(HttpMethod.GET)
                .andResponse(200, ResponseBuilder.withResponseBody(new TypeRepresentation("java.lang.String")).build())
                .andResponseMediaTypes("application/json").build();
        expectedResult.addMethod("test", resourceGetMethod);

        final ClassResult appPathResult = ClassResultBuilder.withApplicationPath("path/nested").build();
        final MethodResult method = MethodResultBuilder.withResponses(HttpResponseBuilder.withStatues(200).andEntityTypes("java.lang.String").build())
                .andMethod(HttpMethod.GET).andResponseMediaTypes("application/json").build();
        final ClassResult resClassResult = ClassResultBuilder.withResourcePath("test").andMethods(method).build();

        final Set<ClassResult> results = new HashSet<>(Arrays.asList(appPathResult, resClassResult));

        final Resources actualResult = classUnderTest.interpret(results);

        Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testDefaultStatusCodes() {
        final Resources expectedResult = new Resources();
        expectedResult.setBasePath("path");
        final ResourceMethod resourceGetMethod = ResourceMethodBuilder.withMethod(HttpMethod.GET)
                .andResponse(200, ResponseBuilder.withResponseBody(new TypeRepresentation("java.lang.String")).build())
                .andResponseMediaTypes("application/json").build();
        final ResourceMethod resourcePostMethod = ResourceMethodBuilder.withMethod(HttpMethod.POST).andResponse(204, ResponseBuilder.newBuilder().build())
                .andRequestBodyType("java.lang.String").build();
        expectedResult.addMethod("test", resourceGetMethod);
        expectedResult.addMethod("test", resourcePostMethod);

        final ClassResult appPathResult = ClassResultBuilder.withApplicationPath("path").build();
        final MethodResult getMethod = MethodResultBuilder.withResponses(HttpResponseBuilder.newBuilder().andEntityTypes("java.lang.String").build())
                .andMethod(HttpMethod.GET).andResponseMediaTypes("application/json").build();
        final MethodResult postMethod = MethodResultBuilder.newBuilder().andMethod(HttpMethod.POST).andRequestBodyType("java.lang.String").build();
        final ClassResult resClassResult = ClassResultBuilder.withResourcePath("test").andMethods(getMethod, postMethod).build();

        final Set<ClassResult> results = new HashSet<>(Arrays.asList(appPathResult, resClassResult));

        final Resources actualResult = classUnderTest.interpret(results);

        Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testNormalizeList() {
        final Resources expectedResult = new Resources();
        expectedResult.setBasePath("path");
        final TypeRepresentation representation = new TypeRepresentation("java.lang.String");
        representation.getRepresentations().put("application/json", Json.createArrayBuilder().add("string").build());

        final ResourceMethod resourceGetMethod = ResourceMethodBuilder.withMethod(HttpMethod.GET)
                .andResponse(200, ResponseBuilder.withResponseBody(representation).build())
                .build();
        expectedResult.addMethod("test", resourceGetMethod);

        final ClassResult appPathResult = ClassResultBuilder.withApplicationPath("path").build();
        final MethodResult method = MethodResultBuilder
                .withResponses(HttpResponseBuilder.withStatues(200).andEntityTypes("javax.ws.rs.core.GenericEntity<java.util.List<java.lang.String>>").build())
                .andMethod(HttpMethod.GET).build();
        final ClassResult resClassResult = ClassResultBuilder.withResourcePath("test").andMethods(method).build();

        final Set<ClassResult> results = new HashSet<>(Arrays.asList(appPathResult, resClassResult));

        final Resources actualResult = classUnderTest.interpret(results);

        Assert.assertEquals(expectedResult, actualResult);
    }

}
