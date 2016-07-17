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

import com.sebastian_daschner.jaxrs_analyzer.model.rest.*;

import java.util.Arrays;

public class ResourceMethodBuilder {

    private final ResourceMethod method;

    private ResourceMethodBuilder(final String name, final HttpMethod method) {
        this.method = new ResourceMethod(name, method);
    }

    public static ResourceMethodBuilder withMethod(final String name, final HttpMethod method) {
        return new ResourceMethodBuilder(name, method);
    }

    public ResourceMethodBuilder andRequestBodyType(final String type) {
        method.setRequestBody(TypeIdentifier.ofType(type));
        return this;
    }

    public ResourceMethodBuilder andRequestBodyType(final TypeIdentifier identifier) {
        method.setRequestBody(identifier);
        return this;
    }

    public ResourceMethodBuilder andResponse(final int status, final Response response) {
        method.getResponses().put(status, response);
        return this;
    }

    public ResourceMethodBuilder andAcceptMediaTypes(final String... mediaTypes) {
        method.getRequestMediaTypes().addAll(Arrays.asList(mediaTypes));
        return this;
    }

    public ResourceMethodBuilder andResponseMediaTypes(final String... mediaTypes) {
        method.getResponseMediaTypes().addAll(Arrays.asList(mediaTypes));
        return this;
    }

    public ResourceMethodBuilder andMatrixParam(final String name, final String type) {
        andParam(ParameterType.MATRIX, name, type, null);
        return this;
    }

    public ResourceMethodBuilder andMatrixParam(final String name, final String type, final String defaultValue) {
        andParam(ParameterType.MATRIX, name, type, defaultValue);
        return this;
    }

    public ResourceMethodBuilder andQueryParam(final String name, final String type) {
        andParam(ParameterType.QUERY, name, type, null);
        return this;
    }

    public ResourceMethodBuilder andQueryParam(final String name, final String type, final String defaultValue) {
        andParam(ParameterType.QUERY, name, type, defaultValue);
        return this;
    }

    public ResourceMethodBuilder andPathParam(final String name, final String type) {
        andParam(ParameterType.PATH, name, type, null);
        return this;
    }

    public ResourceMethodBuilder andPathParam(final String name, final String type, final String defaultValue) {
        andParam(ParameterType.PATH, name, type, defaultValue);
        return this;
    }

    public ResourceMethodBuilder andCookieParam(final String name, final String type) {
        andParam(ParameterType.COOKIE, name, type, null);
        return this;
    }

    public ResourceMethodBuilder andCookieParam(final String name, final String type, final String defaultValue) {
        andParam(ParameterType.COOKIE, name, type, defaultValue);
        return this;
    }

    public ResourceMethodBuilder andHeaderParam(final String name, final String type) {
        andParam(ParameterType.HEADER, name, type, null);
        return this;
    }

    public ResourceMethodBuilder andHeaderParam(final String name, final String type, final String defaultValue) {
        andParam(ParameterType.HEADER, name, type, defaultValue);
        return this;
    }

    public ResourceMethodBuilder andFormParam(final String name, final String type) {
        andParam(ParameterType.FORM, name, type, null);
        return this;
    }

    public ResourceMethodBuilder andFormParam(final String name, final String type, final String defaultValue) {
        andParam(ParameterType.FORM, name, type, defaultValue);
        return this;
    }

    public ResourceMethodBuilder andParam(final ParameterType parameterType, final String name, final String type, final String defaultValue) {
        final MethodParameter methodParameter = new MethodParameter(TypeIdentifier.ofType(type), parameterType);
        methodParameter.setName(name);
        methodParameter.setDefaultValue(defaultValue);
        method.getMethodParameters().add(methodParameter);
        return this;
    }

    public ResourceMethod build() {
        return method;
    }

}
