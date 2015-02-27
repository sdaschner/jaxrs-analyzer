package com.sebastian_daschner.jaxrs_analyzer.builder;

import com.sebastian_daschner.jaxrs_analyzer.model.results.ClassResult;
import com.sebastian_daschner.jaxrs_analyzer.model.results.MethodResult;

import java.util.Arrays;
import java.util.stream.Stream;

public class ClassResultBuilder {

    private final ClassResult classResult = new ClassResult();

    private ClassResultBuilder() {
        // prevent other instances
    }

    public static ClassResultBuilder withApplicationPath(final String applicationPath) {
        final ClassResultBuilder classResultBuilder = new ClassResultBuilder();
        classResultBuilder.classResult.setApplicationPath(applicationPath);
        return classResultBuilder;
    }

    public static ClassResultBuilder withResourcePath(final String resourcePath) {
        final ClassResultBuilder classResultBuilder = new ClassResultBuilder();
        classResultBuilder.classResult.setResourcePath(resourcePath);
        return classResultBuilder;
    }

    public ClassResultBuilder andMethods(final MethodResult... methodResults) {
        Stream.of(methodResults).forEach(classResult::add);
        return this;
    }

    public ClassResultBuilder andAcceptMediaTypes(final String... mediaType) {
        classResult.getAcceptMediaTypes().addAll(Arrays.asList(mediaType));
        return this;
    }

    public ClassResultBuilder andResponseMediaTypes(final String... mediaType) {
        classResult.getResponseMediaTypes().addAll(Arrays.asList(mediaType));
        return this;
    }

    public ClassResult build() {
        return classResult;
    }

}
