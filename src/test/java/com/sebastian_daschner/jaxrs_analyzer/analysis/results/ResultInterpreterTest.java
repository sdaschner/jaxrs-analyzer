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
import com.sebastian_daschner.jaxrs_analyzer.model.Types;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.*;
import com.sebastian_daschner.jaxrs_analyzer.model.results.ClassResult;
import com.sebastian_daschner.jaxrs_analyzer.model.results.MethodResult;
import org.junit.Before;
import org.junit.Test;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static com.sebastian_daschner.jaxrs_analyzer.analysis.results.TypeUtils.STRING_IDENTIFIER;
import static org.junit.Assert.assertEquals;

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
        final ResourceMethod resourceMethod = ResourceMethodBuilder.withMethod("resourceMethod", HttpMethod.GET)
                .andResponse(200, ResponseBuilder.withResponseBody(STRING_IDENTIFIER).build())
                .build();
        expectedResult.addMethod("test", resourceMethod);

        final ClassResult appPathResult = ClassResultBuilder.withApplicationPath("path/").build();
        final MethodResult method = MethodResultBuilder.withResponses(HttpResponseBuilder.withStatues(200).andEntityTypes(Types.STRING).build())
                .andMethodName("resourceMethod").andMethod(HttpMethod.GET).build();
        final ClassResult resClassResult = ClassResultBuilder.withResourcePath("test").andMethods(method).build();

        final Set<ClassResult> results = new HashSet<>(Arrays.asList(appPathResult, resClassResult));

        final Resources actualResult = classUnderTest.interpret(results);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testSubResource() {
        final Resources expectedResult = new Resources();
        expectedResult.setBasePath("path");
        final ResourceMethod resourceGetMethod = ResourceMethodBuilder.withMethod("resourceGetMethod", HttpMethod.GET)
                .andResponse(200, ResponseBuilder.withResponseBody(STRING_IDENTIFIER).build())
                .build();
        expectedResult.addMethod("test", resourceGetMethod);
        final ResourceMethod resourcePostMethod = ResourceMethodBuilder.withMethod("resourcePostMethod", HttpMethod.POST)
                .andResponse(204, ResponseBuilder.newBuilder().build()).build();
        expectedResult.addMethod("test/sub", resourcePostMethod);

        final ClassResult appPathResult = ClassResultBuilder.withApplicationPath("/path").build();
        final MethodResult method = MethodResultBuilder.withResponses(HttpResponseBuilder.withStatues(200).andEntityTypes(Types.STRING).build())
                .andMethodName("resourceGetMethod").andMethod(HttpMethod.GET).build();
        final MethodResult subResourceLocator = MethodResultBuilder.newBuilder().andPath("sub").build();
        final MethodResult subResourceMethod = MethodResultBuilder.withResponses(HttpResponseBuilder.withStatues(204).build())
                .andMethodName("resourcePostMethod").andMethod(HttpMethod.POST).build();
        subResourceLocator.setSubResource(ClassResultBuilder.withResourcePath(null).andMethods(subResourceMethod).build());
        final ClassResult resClassResult = ClassResultBuilder.withResourcePath("test").andMethods(method, subResourceLocator).build();

        final Set<ClassResult> results = new HashSet<>(Arrays.asList(appPathResult, resClassResult));

        final Resources actualResult = classUnderTest.interpret(results);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testNormalizeGenericEntityNoCollection() {
        final Resources expectedResult = new Resources();
        expectedResult.setBasePath("path");

        final ResourceMethod resourceGetMethod = ResourceMethodBuilder.withMethod("resourceGetMethod", HttpMethod.GET)
                .andResponse(200, ResponseBuilder.withResponseBody(STRING_IDENTIFIER).build())
                .build();
        expectedResult.addMethod("test", resourceGetMethod);

        final ClassResult appPathResult = ClassResultBuilder.withApplicationPath("path").build();
        final MethodResult method = MethodResultBuilder
                .withResponses(HttpResponseBuilder.withStatues(200).andEntityTypes("Ljavax/ws/rs/core/GenericEntity<Ljava/lang/String;>;").build())
                .andMethodName("resourceGetMethod").andMethod(HttpMethod.GET).build();
        final ClassResult resClassResult = ClassResultBuilder.withResourcePath("test").andMethods(method).build();

        final Set<ClassResult> results = new HashSet<>(Arrays.asList(appPathResult, resClassResult));

        final Resources actualResult = classUnderTest.interpret(results);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testContentTypes() {
        final Resources expectedResult = new Resources();
        expectedResult.setBasePath("path");
        final ResourceMethod resourceGetMethod = ResourceMethodBuilder.withMethod("resourceGetMethod", HttpMethod.GET)
                .andResponse(200, ResponseBuilder.withResponseBody(STRING_IDENTIFIER).build())
                .andResponseMediaTypes("application/xml").build();
        expectedResult.addMethod("test", resourceGetMethod);

        final ClassResult appPathResult = ClassResultBuilder.withApplicationPath("path").build();
        final MethodResult method = MethodResultBuilder.withResponses(HttpResponseBuilder.withStatues(200).andEntityTypes(Types.STRING).build())
                .andMethodName("resourceGetMethod").andMethod(HttpMethod.GET).andResponseMediaTypes("application/xml").build();
        final ClassResult resClassResult = ClassResultBuilder.withResourcePath("test").andMethods(method).build();

        final Set<ClassResult> results = new HashSet<>(Arrays.asList(appPathResult, resClassResult));

        final Resources actualResult = classUnderTest.interpret(results);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testOverrideAnnotationContentType() {
        final Resources expectedResult = new Resources();
        expectedResult.setBasePath("path");
        final ResourceMethod resourceGetMethod = ResourceMethodBuilder.withMethod("resourceGetMethod", HttpMethod.GET)
                .andResponse(200, ResponseBuilder.withResponseBody(STRING_IDENTIFIER).build())
                .andResponseMediaTypes("application/json").build();
        expectedResult.addMethod("test", resourceGetMethod);

        final ClassResult appPathResult = ClassResultBuilder.withApplicationPath("path").build();
        final MethodResult method = MethodResultBuilder.withResponses(HttpResponseBuilder.withStatues(200).andContentTypes("application/json")
                .andEntityTypes(Types.STRING).build())
                .andMethodName("resourceGetMethod").andMethod(HttpMethod.GET).andResponseMediaTypes("application/xml", "application/json").build();
        final ClassResult resClassResult = ClassResultBuilder.withResourcePath("test").andMethods(method).build();

        final Set<ClassResult> results = new HashSet<>(Arrays.asList(appPathResult, resClassResult));

        final Resources actualResult = classUnderTest.interpret(results);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testNestedBasePath() {
        final Resources expectedResult = new Resources();
        expectedResult.setBasePath("path/nested");
        final ResourceMethod resourceGetMethod = ResourceMethodBuilder.withMethod("resourceGetMethod", HttpMethod.GET)
                .andResponse(200, ResponseBuilder.withResponseBody(STRING_IDENTIFIER).build())
                .andResponseMediaTypes("application/json").build();
        expectedResult.addMethod("test", resourceGetMethod);

        final ClassResult appPathResult = ClassResultBuilder.withApplicationPath("path/nested").build();
        final MethodResult method = MethodResultBuilder.withResponses(HttpResponseBuilder.withStatues(200).andEntityTypes(Types.STRING).build())
                .andMethodName("resourceGetMethod").andMethod(HttpMethod.GET).andResponseMediaTypes("application/json").build();
        final ClassResult resClassResult = ClassResultBuilder.withResourcePath("test").andMethods(method).build();

        final Set<ClassResult> results = new HashSet<>(Arrays.asList(appPathResult, resClassResult));

        final Resources actualResult = classUnderTest.interpret(results);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testDefaultStatusCodes() {
        final Resources expectedResult = new Resources();
        expectedResult.setBasePath("path");
        final ResourceMethod resourceGetMethod = ResourceMethodBuilder.withMethod("resourceGetMethod", HttpMethod.GET)
                .andResponse(200, ResponseBuilder.withResponseBody(STRING_IDENTIFIER).build())
                .andResponseMediaTypes("application/json").build();
        final ResourceMethod resourcePostMethod = ResourceMethodBuilder.withMethod("resourcePostMethod", HttpMethod.POST).andResponse(204, ResponseBuilder.newBuilder().build())
                .andRequestBodyType(Types.STRING).build();
        expectedResult.addMethod("test", resourceGetMethod);
        expectedResult.addMethod("test", resourcePostMethod);

        final ClassResult appPathResult = ClassResultBuilder.withApplicationPath("path").build();
        final MethodResult getMethod = MethodResultBuilder.withResponses(HttpResponseBuilder.newBuilder().andEntityTypes(Types.STRING).build())
                .andMethodName("resourceGetMethod").andMethod(HttpMethod.GET).andResponseMediaTypes("application/json").build();
        final MethodResult postMethod = MethodResultBuilder.newBuilder()
                .andMethodName("resourcePostMethod").andMethod(HttpMethod.POST).andRequestBodyType(Types.STRING).build();
        final ClassResult resClassResult = ClassResultBuilder.withResourcePath("test").andMethods(getMethod, postMethod).build();

        final Set<ClassResult> results = new HashSet<>(Arrays.asList(appPathResult, resClassResult));

        final Resources actualResult = classUnderTest.interpret(results);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testNormalize() {
        final Resources expectedResult = new Resources();
        expectedResult.setBasePath("path");

        final TypeIdentifier stringListIdentifier = TypeIdentifier.ofType("Ljava/util/List<Ljava/lang/String;>;");
        final TypeRepresentation stringList = TypeRepresentation.ofCollection(stringListIdentifier, TypeRepresentation.ofConcrete(STRING_IDENTIFIER));
        expectedResult.getTypeRepresentations().put(stringListIdentifier, stringList);

        final ResourceMethod resourceGetMethod = ResourceMethodBuilder.withMethod("resourceGetMethod", HttpMethod.GET)
                .andResponse(200, ResponseBuilder.withResponseBody(stringListIdentifier).build())
                .build();
        expectedResult.addMethod("test", resourceGetMethod);

        final ClassResult appPathResult = ClassResultBuilder.withApplicationPath("path").build();
        final MethodResult method = MethodResultBuilder
                .withResponses(HttpResponseBuilder.withStatues(200).andEntityTypes("Ljavax/ws/rs/core/GenericEntity<Ljava/util/List<Ljava/lang/String;>;>;").build())
                .andMethodName("resourceGetMethod").andMethod(HttpMethod.GET).build();
        final ClassResult resClassResult = ClassResultBuilder.withResourcePath("test").andMethods(method).build();

        final Set<ClassResult> results = new HashSet<>(Arrays.asList(appPathResult, resClassResult));

        final Resources actualResult = classUnderTest.interpret(results);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testAmbiguousEntityTypes() {
        final String configurationType = "Lcom/sebastian_daschner/jaxrs_analyzer/analysis/results/ResultInterpreterTest$ConfigurationManager$Configuration;";

        final Resources expectedResult = new Resources();
        expectedResult.setBasePath("path");
        final TypeIdentifier identifier = TypeIdentifier.ofType(configurationType);
        expectedResult.getTypeRepresentations().put(identifier, TypeRepresentation.ofConcrete(identifier,
                Collections.singletonMap("name", STRING_IDENTIFIER)));

        final ResourceMethod resourceGetMethod = ResourceMethodBuilder.withMethod("resourceGetMethod", HttpMethod.GET)
                .andResponse(200, ResponseBuilder.withResponseBody(identifier).build())
                .andResponse(204, ResponseBuilder.newBuilder().build())
                .build();
        expectedResult.addMethod("test", resourceGetMethod);

        final ClassResult appPathResult = ClassResultBuilder.withApplicationPath("path").build();
        final MethodResult method = MethodResultBuilder
                .withResponses(HttpResponseBuilder.withStatues(200).andEntityTypes(Types.OBJECT, configurationType).build(),
                        HttpResponseBuilder.withStatues(204).build())
                .andMethodName("resourceGetMethod").andMethod(HttpMethod.GET).build();
        final ClassResult resClassResult = ClassResultBuilder.withResourcePath("test").andMethods(method).build();

        final Set<ClassResult> results = new HashSet<>(Arrays.asList(appPathResult, resClassResult));

        final Resources actualResult = classUnderTest.interpret(results);

        assertEquals(expectedResult, actualResult);
    }

    private interface ConfigurationManager {
        @XmlAccessorType(XmlAccessType.FIELD)
        class Configuration {
            private String name;
        }
    }

}
