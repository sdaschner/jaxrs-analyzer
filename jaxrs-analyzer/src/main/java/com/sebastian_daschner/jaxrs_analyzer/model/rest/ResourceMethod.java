package com.sebastian_daschner.jaxrs_analyzer.model.rest;

import java.util.*;

/**
 * Represents a REST resource method.
 *
 * @author Sebastian Daschner
 */
public class ResourceMethod {

    private final Set<String> acceptMediaTypes = new HashSet<>();
    private final Set<String> responseMediaTypes = new HashSet<>();
    private final Map<Integer, Response> responses = new HashMap<>();

    private final MethodParameters methodParameters;
    private final HttpMethod method;
    private TypeRepresentation requestBody;


    public ResourceMethod(final HttpMethod method) {
        this(method, new MethodParameters());
    }

    public ResourceMethod(final HttpMethod method, final MethodParameters methodParameters) {
        Objects.requireNonNull(method);
        Objects.requireNonNull(methodParameters);

        this.method = method;
        this.methodParameters = methodParameters;
    }

    public Set<String> getAcceptMediaTypes() {
        return acceptMediaTypes;
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

    public TypeRepresentation getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(final TypeRepresentation requestBody) {
        this.requestBody = requestBody;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final ResourceMethod that = (ResourceMethod) o;

        if (!acceptMediaTypes.equals(that.acceptMediaTypes)) return false;
        if (!responseMediaTypes.equals(that.responseMediaTypes)) return false;
        if (!responses.equals(that.responses)) return false;
        if (!methodParameters.equals(that.methodParameters)) return false;
        if (method != that.method) return false;
        return !(requestBody != null ? !requestBody.equals(that.requestBody) : that.requestBody != null);
    }

    @Override
    public int hashCode() {
        int result = acceptMediaTypes.hashCode();
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
                "acceptMediaTypes=" + acceptMediaTypes +
                ", responseMediaTypes=" + responseMediaTypes +
                ", responses=" + responses +
                ", methodParameters=" + methodParameters +
                ", method=" + method +
                ", requestBody=" + requestBody +
                '}';
    }

}
