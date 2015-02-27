package com.sebastian_daschner.jaxrs_analyzer.builder;

import com.sebastian_daschner.jaxrs_analyzer.model.elements.HttpResponse;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.HttpMethod;
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

    public MethodResultBuilder andAcceptMediaTypes(final String... mediaTypes) {
        methodResult.getAcceptMediaTypes().addAll(Arrays.asList(mediaTypes));
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
        methodResult.getMethodParameters().getMatrixParams().put(name, type);
        return this;
    }

    public MethodResultBuilder andQueryParam(final String name, final String type) {
        methodResult.getMethodParameters().getQueryParams().put(name, type);
        return this;
    }

    public MethodResultBuilder andPathParam(final String name, final String type) {
        methodResult.getMethodParameters().getPathParams().put(name, type);
        return this;
    }

    public MethodResultBuilder andCookieParam(final String name, final String type) {
        methodResult.getMethodParameters().getCookieParams().put(name, type);
        return this;
    }

    public MethodResultBuilder andHeaderParam(final String name, final String type) {
        methodResult.getMethodParameters().getHeaderParams().put(name, type);
        return this;
    }

    public MethodResultBuilder andFormParam(final String name, final String type) {
        methodResult.getMethodParameters().getFormParams().put(name, type);
        return this;
    }

    public MethodResult build() {
        return methodResult;
    }

}
