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
    private final Set<MethodParameter> methodParameters = new HashSet<>();

    private final HttpMethod method;
    private final String name;
    private TypeIdentifier requestBody;

    public ResourceMethod(final String name, final HttpMethod method) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(method);
        this.name = name;
        this.method = method;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getName() { return name; }

    public Set<MethodParameter> getMethodParameters() {
        return methodParameters;
    }

    public TypeIdentifier getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(final TypeIdentifier requestBody) {
        this.requestBody = requestBody;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ResourceMethod that = (ResourceMethod) o;

        if (requestMediaTypes != null ? !requestMediaTypes.equals(that.requestMediaTypes) : that.requestMediaTypes != null)
            return false;
        if (responseMediaTypes != null ? !responseMediaTypes.equals(that.responseMediaTypes) : that.responseMediaTypes != null)
            return false;
        if (responses != null ? !responses.equals(that.responses) : that.responses != null) return false;
        if (methodParameters != null ? !methodParameters.equals(that.methodParameters) : that.methodParameters != null)
            return false;
        if (method != that.method) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return requestBody != null ? requestBody.equals(that.requestBody) : that.requestBody == null;
    }

    @Override
    public int hashCode() {
        int result = requestMediaTypes != null ? requestMediaTypes.hashCode() : 0;
        result = 31 * result + (responseMediaTypes != null ? responseMediaTypes.hashCode() : 0);
        result = 31 * result + (responses != null ? responses.hashCode() : 0);
        result = 31 * result + (methodParameters != null ? methodParameters.hashCode() : 0);
        result = 31 * result + (method != null ? method.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (requestBody != null ? requestBody.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ResourceMethod{" +
                "name=" + name +
                ", method=" + method +
                ", requestMediaTypes=" + requestMediaTypes +
                ", responseMediaTypes=" + responseMediaTypes +
                ", responses=" + responses +
                ", methodParameters=" + methodParameters +
                ", requestBody=" + requestBody +
                '}';
    }
}
