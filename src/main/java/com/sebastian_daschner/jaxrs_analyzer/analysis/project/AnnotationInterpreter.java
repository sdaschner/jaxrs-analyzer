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

package com.sebastian_daschner.jaxrs_analyzer.analysis.project;

import com.sebastian_daschner.jaxrs_analyzer.model.results.ClassResult;
import com.sebastian_daschner.jaxrs_analyzer.model.results.MethodResult;

import javax.ws.rs.*;
import java.util.Arrays;
import java.util.function.Consumer;

/**
 * Interprets given annotations and adds JAX-RS relevant information.
 *
 * @author Sebastian Daschner
 */
public final class AnnotationInterpreter {

    private AnnotationInterpreter() {
        throw new UnsupportedOperationException();
    }

    /**
     * Interprets the given class annotation and adds corresponding information to the result.
     *
     * @param annotation The annotation to analyze
     * @param result     The class result
     */
    public static void interpretClassAnnotation(final Object annotation, final ClassResult result) {
        consumeIfMatches(annotation, ApplicationPath.class, applicationPath -> result.setApplicationPath(applicationPath.value()));

        consumeIfMatches(annotation, Path.class, path -> result.setResourcePath(path.value()));

        consumeIfMatches(annotation, Consumes.class, consumes -> result.getAcceptMediaTypes().addAll(Arrays.asList(consumes.value())));

        consumeIfMatches(annotation, Produces.class, produces -> result.getResponseMediaTypes().addAll(Arrays.asList(produces.value())));
    }

    /**
     * Interprets the given method annotation and adds corresponding information to the result.
     *
     * @param annotation The annotation to analyze
     * @param result     The method result
     */
    public static void interpretMethodAnnotation(final Object annotation, final MethodResult result) {
        consumeIfMatches(annotation, Path.class, path -> result.setPath(path.value()));

        consumeIfMatches(annotation, GET.class, m -> result.setHttpMethod(com.sebastian_daschner.jaxrs_analyzer.model.rest.HttpMethod.GET));

        consumeIfMatches(annotation, PUT.class, m -> result.setHttpMethod(com.sebastian_daschner.jaxrs_analyzer.model.rest.HttpMethod.PUT));

        consumeIfMatches(annotation, POST.class, m -> result.setHttpMethod(com.sebastian_daschner.jaxrs_analyzer.model.rest.HttpMethod.POST));

        consumeIfMatches(annotation, DELETE.class, m -> result.setHttpMethod(com.sebastian_daschner.jaxrs_analyzer.model.rest.HttpMethod.DELETE));

        consumeIfMatches(annotation, HEAD.class, m -> result.setHttpMethod(com.sebastian_daschner.jaxrs_analyzer.model.rest.HttpMethod.HEAD));

        consumeIfMatches(annotation, OPTIONS.class, m -> result.setHttpMethod(com.sebastian_daschner.jaxrs_analyzer.model.rest.HttpMethod.OPTIONS));

        consumeIfMatches(annotation, Consumes.class, consumes -> result.getAcceptMediaTypes().addAll(Arrays.asList(consumes.value())));

        consumeIfMatches(annotation, Produces.class, produces -> result.getResponseMediaTypes().addAll(Arrays.asList(produces.value())));
    }

    /**
     * Interprets the given parameter annotation of a method and adds corresponding information to the result.
     *
     * @param annotation    The annotation to analyze
     * @param annotatedType The type of the annotated parameter
     * @param result        The method result
     */
    public static void interpretMethodParameterAnnotation(final Object annotation, final String annotatedType, final MethodResult result) {
        consumeIfMatches(annotation, MatrixParam.class, m -> result.getMethodParameters().getMatrixParams().put(m.value(), annotatedType));

        consumeIfMatches(annotation, QueryParam.class, m -> result.getMethodParameters().getQueryParams().put(m.value(), annotatedType));

        consumeIfMatches(annotation, PathParam.class, m -> result.getMethodParameters().getPathParams().put(m.value(), annotatedType));

        consumeIfMatches(annotation, CookieParam.class, m -> result.getMethodParameters().getCookieParams().put(m.value(), annotatedType));

        consumeIfMatches(annotation, HeaderParam.class, m -> result.getMethodParameters().getHeaderParams().put(m.value(), annotatedType));

        consumeIfMatches(annotation, FormParam.class, m -> result.getMethodParameters().getFormParams().put(m.value(), annotatedType));
    }

    /**
     * Checks if the given object matches the class and if so then invokes the consumer to consume the object.
     *
     * @param object   The annotation
     * @param clazz    The class to test
     * @param consumer The consumer which is invoked if the types match
     * @param <T>      The type of the annotation
     */
    @SuppressWarnings("unchecked")
    private static <T> void consumeIfMatches(final Object object, final Class<T> clazz, final Consumer<T> consumer) {
        if (clazz.isAssignableFrom(object.getClass()))
            consumer.accept((T) object);
    }

}
