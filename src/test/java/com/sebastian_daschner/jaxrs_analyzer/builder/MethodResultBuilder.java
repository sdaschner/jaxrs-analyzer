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

package com.sebastian_daschner.jaxrs_analyzer.builder;

import com.sebastian_daschner.jaxrs_analyzer.model.elements.HttpResponse;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.HttpMethod;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.MethodParameter;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.ParameterType;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeIdentifier;
import com.sebastian_daschner.jaxrs_analyzer.model.results.MethodResult;

import java.util.Arrays;
import java.util.stream.Stream;

public class MethodResultBuilder {

    private final MethodResult methodResult = new MethodResult();

    private MethodResultBuilder() {
        // prevent other instances
    }

    public static MethodResultBuilder newBuilder() {
        return new MethodResultBuilder();
    }

    public static MethodResultBuilder withResponses(final HttpResponse... responses) {
        final MethodResultBuilder builder = new MethodResultBuilder();
        Stream.of(responses).forEach(builder.methodResult.getResponses()::add);
        return builder;
    }

    public MethodResultBuilder andPath(final String path) {
        methodResult.setPath(path);
        return this;
    }

    public MethodResultBuilder andMethod(final HttpMethod httpMethod) {
        methodResult.setHttpMethod(httpMethod);
        return this;
    }

    public MethodResultBuilder andMethodName(final String name) {
        methodResult.setMethodName(name);
        return this;
    }

    public MethodResultBuilder andRequestMediaTypes(final String... mediaTypes) {
        methodResult.getRequestMediaTypes().addAll(Arrays.asList(mediaTypes));
        return this;
    }

    public MethodResultBuilder andResponseMediaTypes(final String... mediaTypes) {
        methodResult.getResponseMediaTypes().addAll(Arrays.asList(mediaTypes));
        return this;
    }

    public MethodResultBuilder andRequestBodyType(final String type) {
        methodResult.setRequestBodyType(type);
        return this;
    }

    public MethodResultBuilder andMatrixParam(final String name, final String type) {
        andParam(ParameterType.MATRIX, name, type, null);
        return this;
    }

    public MethodResultBuilder andMatrixParam(final String name, final String type, final String defaultValue) {
        andParam(ParameterType.MATRIX, name, type, defaultValue);
        return this;
    }

    public MethodResultBuilder andQueryParam(final String name, final String type) {
        andParam(ParameterType.QUERY, name, type, null);
        return this;
    }

    public MethodResultBuilder andQueryParam(final String name, final String type, final String defaultValue) {
        andParam(ParameterType.QUERY, name, type, defaultValue);
        return this;
    }

    public MethodResultBuilder andPathParam(final String name, final String type) {
        andParam(ParameterType.PATH, name, type, null);
        return this;
    }

    public MethodResultBuilder andPathParam(final String name, final String type, final String defaultValue) {
        andParam(ParameterType.PATH, name, type, defaultValue);
        return this;
    }

    public MethodResultBuilder andCookieParam(final String name, final String type) {
        andParam(ParameterType.COOKIE, name, type, null);
        return this;
    }

    public MethodResultBuilder andCookieParam(final String name, final String type, final String defaultValue) {
        andParam(ParameterType.COOKIE, name, type, defaultValue);
        return this;
    }

    public MethodResultBuilder andHeaderParam(final String name, final String type) {
        andParam(ParameterType.HEADER, name, type, null);
        return this;
    }

    public MethodResultBuilder andHeaderParam(final String name, final String type, final String defaultValue) {
        andParam(ParameterType.HEADER, name, type, defaultValue);
        return this;
    }

    public MethodResultBuilder andFormParam(final String name, final String type) {
        andParam(ParameterType.FORM, name, type, null);
        return this;
    }

    public MethodResultBuilder andFormParam(final String name, final String type, final String defaultValue) {
        andParam(ParameterType.FORM, name, type, defaultValue);
        return this;
    }

    private MethodResultBuilder andParam(final ParameterType parameterType, final String name, final String type, final String defaultValue) {
        final MethodParameter methodParameter = new MethodParameter(TypeIdentifier.ofType(type), parameterType);
        methodParameter.setName(name);
        methodParameter.setDefaultValue(defaultValue);
        methodResult.getMethodParameters().add(methodParameter);
        return this;
    }

    public MethodResult build() {
        return methodResult;
    }

}
