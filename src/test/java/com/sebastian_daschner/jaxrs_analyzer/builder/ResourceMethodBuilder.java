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

import com.sebastian_daschner.jaxrs_analyzer.model.Types;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.*;

import java.util.Arrays;

public class ResourceMethodBuilder {

    private final ResourceMethod method;
    private int parameter ;

    private ResourceMethodBuilder(final HttpMethod method) {
        this.method = new ResourceMethod(method);
    }

    public static ResourceMethodBuilder withMethod(final HttpMethod method) {
        return new ResourceMethodBuilder(method);
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
        andParam(Types.MATRIX_PARAM, name, type, true);
        return this;
    }

    public ResourceMethodBuilder andMatrixParam(final String name, final String type, final Boolean required) {
        andParam(Types.MATRIX_PARAM, name, type, required);
        return this;
    }

    public ResourceMethodBuilder andQueryParam(final String name, final String type) {
        andParam(Types.QUERY_PARAM, name, type, true);
        return this;
    }

    public ResourceMethodBuilder andQueryParam(final String name, final String type, final Boolean required) {
        andParam(Types.QUERY_PARAM, name, type, required);
        return this;
    }

    public ResourceMethodBuilder andPathParam(final String name, final String type) {
        andParam(Types.PATH_PARAM, name, type, true);
        return this;
    }

    public ResourceMethodBuilder andPathParam(final String name, final String type, final Boolean required) {
        andParam(Types.PATH_PARAM, name, type, required);
        return this;
    }

    public ResourceMethodBuilder andCookieParam(final String name, final String type) {
        andParam(Types.COOKIE_PARAM, name, type, true);
        return this;
    }

    public ResourceMethodBuilder andCookieParam(final String name, final String type, final Boolean required) {
        andParam(Types.COOKIE_PARAM, name, type, required);
        return this;
    }

    public ResourceMethodBuilder andHeaderParam(final String name, final String type) {
        andParam(Types.HEADER_PARAM, name, type, true);
        return this;
    }

    public ResourceMethodBuilder andHeaderParam(final String name, final String type, final Boolean required) {
        andParam(Types.HEADER_PARAM, name, type, required);
        return this;
    }

    public ResourceMethodBuilder andFormParam(final String name, final String type) {
        andParam(Types.FORM_PARAM, name, type, true);
        return this;
    }

    public ResourceMethodBuilder andFormParam(final String name, final String type, final Boolean required) {
        andParam(Types.FORM_PARAM, name, type, required);
        return this;
    }

    public ResourceMethodBuilder andParam(final String annotation, final String name, final String type, final Boolean required) {
        method.getMethodParameters().setParameter(parameter, new MethodParameter(annotation, name, type, required));
        parameter++;
        return this;
    }

    public ResourceMethod build() {
        return method;
    }

}
