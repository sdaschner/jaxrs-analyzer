package com.sebastian_daschner.jaxrs_analyzer.builder;

import com.sebastian_daschner.jaxrs_analyzer.model.rest.HttpMethod;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.ResourceMethod;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.Response;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeRepresentation;

import java.util.Arrays;

public class ResourceMethodBuilder {

    private final ResourceMethod method;

    private ResourceMethodBuilder(final HttpMethod method) {
        this.method = new ResourceMethod(method);
    }

    public static ResourceMethodBuilder withMethod(final HttpMethod method) {
        return new ResourceMethodBuilder(method);
    }

    public ResourceMethodBuilder andRequestBodyType(final String type) {
        method.setRequestBody(new TypeRepresentation(type));
        return this;
    }

    public ResourceMethodBuilder andRequestBodyType(final TypeRepresentation representation) {
        method.setRequestBody(representation);
        return this;
    }

    public ResourceMethodBuilder andResponse(final int status, final Response response) {
        method.getResponses().put(status, response);
        return this;
    }

    public ResourceMethodBuilder andAcceptMediaTypes(final String... mediaTypes) {
        method.getAcceptMediaTypes().addAll(Arrays.asList(mediaTypes));
        return this;
    }

    public ResourceMethodBuilder andResponseMediaTypes(final String... mediaTypes) {
        method.getResponseMediaTypes().addAll(Arrays.asList(mediaTypes));
        return this;
    }

    public ResourceMethodBuilder andMatrixParam(final String name, final String type) {
        method.getMethodParameters().getMatrixParams().put(name, type);
        return this;
    }

    public ResourceMethodBuilder andQueryParam(final String name, final String type) {
        method.getMethodParameters().getQueryParams().put(name, type);
        return this;
    }

    public ResourceMethodBuilder andPathParam(final String name, final String type) {
        method.getMethodParameters().getPathParams().put(name, type);
        return this;
    }

    public ResourceMethodBuilder andCookieParam(final String name, final String type) {
        method.getMethodParameters().getCookieParams().put(name, type);
        return this;
    }

    public ResourceMethodBuilder andHeaderParam(final String name, final String type) {
        method.getMethodParameters().getHeaderParams().put(name, type);
        return this;
    }

    public ResourceMethodBuilder andFormParam(final String name, final String type) {
        method.getMethodParameters().getFormParams().put(name, type);
        return this;
    }

    public ResourceMethod build() {
        return method;
    }

}
