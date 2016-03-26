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

package com.sebastian_daschner.jaxrs_analyzer.model.rest;

import java.util.*;

/**
 * Represents a REST resource method.
 *
 * @author Sebastian Daschner
 */
public class ResourceMethod {

    private final Set<String> requestMediaTypes = new HashSet<>();
    private final Set<String> responseMediaTypes = new HashSet<>();
    private final Map<Integer, Response> responses = new HashMap<>();

    private final MethodParameters methodParameters;
    private final HttpMethod method;
    private TypeIdentifier requestBody;

    public ResourceMethod(final HttpMethod method) {
        this(method, new MethodParameters());
    }

    public ResourceMethod(final HttpMethod method, final MethodParameters methodParameters) {
        Objects.requireNonNull(method);
        Objects.requireNonNull(methodParameters);

        this.method = method;
        this.methodParameters = methodParameters;
    }

    public Set<String> getRequestMediaTypes() {
        return requestMediaTypes;
    }

    public Set<String> getResponseMediaTypes() {
        return responseMediaTypes;
    }

    public Map<Integer, Response> getResponses() {
        return responses;
    }

    public MethodParameters getMethodParameters() {
        return methodParameters;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public TypeIdentifier getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(final TypeIdentifier requestBody) {
        this.requestBody = requestBody;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final ResourceMethod that = (ResourceMethod) o;

        if (!requestMediaTypes.equals(that.requestMediaTypes)) return false;
        if (!responseMediaTypes.equals(that.responseMediaTypes)) return false;
        if (!responses.equals(that.responses)) return false;
        if (!methodParameters.equals(that.methodParameters)) return false;
        if (method != that.method) return false;
        return !(requestBody != null ? !requestBody.equals(that.requestBody) : that.requestBody != null);
    }

    @Override
    public int hashCode() {
        int result = requestMediaTypes.hashCode();
        result = 31 * result + responseMediaTypes.hashCode();
        result = 31 * result + responses.hashCode();
        result = 31 * result + methodParameters.hashCode();
        result = 31 * result + method.hashCode();
        result = 31 * result + (requestBody != null ? requestBody.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ResourceMethod{" +
                "requestMediaTypes=" + requestMediaTypes +
                ", responseMediaTypes=" + responseMediaTypes +
                ", responses=" + responses +
                ", methodParameters=" + methodParameters +
                ", method=" + method +
                ", requestBody=" + requestBody +
                '}';
    }

}
