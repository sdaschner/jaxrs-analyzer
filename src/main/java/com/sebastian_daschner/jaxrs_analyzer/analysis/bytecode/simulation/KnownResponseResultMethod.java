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

package com.sebastian_daschner.jaxrs_analyzer.analysis.bytecode.simulation;

import com.sebastian_daschner.jaxrs_analyzer.model.elements.Element;
import com.sebastian_daschner.jaxrs_analyzer.model.elements.HttpResponse;
import com.sebastian_daschner.jaxrs_analyzer.model.elements.JsonValue;
import com.sebastian_daschner.jaxrs_analyzer.model.elements.MethodHandle;
import com.sebastian_daschner.jaxrs_analyzer.model.methods.IdentifiableMethod;
import com.sebastian_daschner.jaxrs_analyzer.model.methods.Method;
import com.sebastian_daschner.jaxrs_analyzer.model.methods.MethodIdentifier;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static com.sebastian_daschner.jaxrs_analyzer.model.JavaUtils.INITIALIZER_NAME;
import static com.sebastian_daschner.jaxrs_analyzer.model.Types.*;
import static com.sebastian_daschner.jaxrs_analyzer.model.methods.MethodIdentifier.ofNonStatic;
import static com.sebastian_daschner.jaxrs_analyzer.model.methods.MethodIdentifier.ofStatic;

/**
 * Known methods which apply logic to the result or to the return element.
 *
 * @author Sebastian Daschner
 */
enum KnownResponseResultMethod implements IdentifiableMethod {

    // non-static methods in ResponseBuilder --------------------------

    RESPONSE_BUILDER_BUILD(ofNonStatic(CLASS_RESPONSE_BUILDER, "build", RESPONSE), (object, arguments) -> object),

    RESPONSE_BUILDER_CACHE_CONTROL(ofNonStatic(CLASS_RESPONSE_BUILDER, "cacheControl", RESPONSE_BUILDER, "Ljavax/ws/rs/core/CacheControl;"), (object, arguments) ->
            addHeader(object, HttpHeaders.CACHE_CONTROL)),

    RESPONSE_BUILDER_CONTENT_LOCATION(ofNonStatic(CLASS_RESPONSE_BUILDER, "contentLocation", RESPONSE_BUILDER, URI), (object, arguments) ->
            addHeader(object, HttpHeaders.CONTENT_LOCATION)),

    RESPONSE_BUILDER_COOKIE(ofNonStatic(CLASS_RESPONSE_BUILDER, "cookie", RESPONSE_BUILDER, "[Ljavax/ws/rs/core/NewCookie;"), (object, arguments) ->
            addHeader(object, HttpHeaders.SET_COOKIE)),

    RESPONSE_BUILDER_ENCODING(ofNonStatic(CLASS_RESPONSE_BUILDER, "encoding", RESPONSE_BUILDER, STRING), (object, arguments) ->
            addHeader(object, HttpHeaders.CONTENT_ENCODING)),

    RESPONSE_BUILDER_ENTITY(ofNonStatic(CLASS_RESPONSE_BUILDER, "entity", RESPONSE_BUILDER, OBJECT), (object, arguments) ->
            addEntity(object, arguments.get(0))),

    RESPONSE_BUILDER_ENTITY_ANNOTATION(ofNonStatic(CLASS_RESPONSE_BUILDER, "entity", RESPONSE_BUILDER, OBJECT, "[Ljava/lang/annotation/Annotation;"), (object, arguments) ->
            addEntity(object, arguments.get(0))),

    RESPONSE_BUILDER_EXPIRES(ofNonStatic(CLASS_RESPONSE_BUILDER, "expires", RESPONSE_BUILDER, DATE), (object, arguments) ->
            addHeader(object, HttpHeaders.EXPIRES)),

    RESPONSE_BUILDER_HEADER(ofNonStatic(CLASS_RESPONSE_BUILDER, "header", RESPONSE_BUILDER, STRING, OBJECT), (object, arguments) -> {
        arguments.get(0).getPossibleValues().stream()
                .map(header -> (String) header).forEach(h -> addHeader(object, h));
        return object;
    }),

    RESPONSE_BUILDER_LANGUAGE_LOCALE(ofNonStatic(CLASS_RESPONSE_BUILDER, "language", RESPONSE_BUILDER, "Ljava/util/Locale;"), (object, arguments) ->
            addHeader(object, HttpHeaders.CONTENT_LANGUAGE)),

    RESPONSE_BUILDER_LANGUAGE_STRING(ofNonStatic(CLASS_RESPONSE_BUILDER, "language", RESPONSE_BUILDER, STRING), (object, arguments) ->
            addHeader(object, HttpHeaders.CONTENT_LANGUAGE)),

    RESPONSE_BUILDER_LAST_MODIFIED(ofNonStatic(CLASS_RESPONSE_BUILDER, "lastModified", RESPONSE_BUILDER, DATE), (object, arguments) ->
            addHeader(object, HttpHeaders.LAST_MODIFIED)),

    RESPONSE_BUILDER_LINK_URI(ofNonStatic(CLASS_RESPONSE_BUILDER, "link", RESPONSE_BUILDER, URI, STRING), (object, arguments) ->
            addHeader(object, HttpHeaders.LINK)),

    RESPONSE_BUILDER_LINK_STRING(ofNonStatic(CLASS_RESPONSE_BUILDER, "link", RESPONSE_BUILDER, STRING, STRING), (object, arguments) ->
            addHeader(object, HttpHeaders.LINK)),

    RESPONSE_BUILDER_LINKS(ofNonStatic(CLASS_RESPONSE_BUILDER, "links", RESPONSE_BUILDER, "[Ljavax/ws/rs/core/Link;"), (object, arguments) ->
            addHeader(object, HttpHeaders.LINK)),

    RESPONSE_BUILDER_LOCATION(ofNonStatic(CLASS_RESPONSE_BUILDER, "location", RESPONSE_BUILDER, URI), (object, arguments) ->
            addHeader(object, HttpHeaders.LOCATION)),

    RESPONSE_BUILDER_STATUS_ENUM(ofNonStatic(CLASS_RESPONSE_BUILDER, "status", RESPONSE_BUILDER, RESPONSE_STATUS), (object, arguments) -> {
        arguments.get(0).getPossibleValues().stream()
                .map(status -> ((Response.Status) status).getStatusCode()).forEach(s -> addStatus(object, s));
        return object;
    }),

    RESPONSE_BUILDER_STATUS_INT(ofNonStatic(CLASS_RESPONSE_BUILDER, "status", RESPONSE_BUILDER, PRIMITIVE_INT), (object, arguments) -> {
        arguments.get(0).getPossibleValues().stream()
                .map(status -> (int) status).forEach(s -> addStatus(object, s));
        return object;
    }),

    RESPONSE_BUILDER_TAG_ENTITY(ofNonStatic(CLASS_RESPONSE_BUILDER, "tag", RESPONSE_BUILDER, ENTITY_TAG), (object, arguments) ->
            addHeader(object, HttpHeaders.ETAG)),

    RESPONSE_BUILDER_TAG_STRING(ofNonStatic(CLASS_RESPONSE_BUILDER, "tag", RESPONSE_BUILDER, STRING), (object, arguments) ->
            addHeader(object, HttpHeaders.ETAG)),

    RESPONSE_BUILDER_TYPE(ofNonStatic(CLASS_RESPONSE_BUILDER, "type", RESPONSE_BUILDER, "Ljavax/ws/rs/core/MediaType;"), (object, arguments) -> {
        arguments.get(0).getPossibleValues().stream()
                .map(m -> (MediaType) m).map(m -> m.getType() + '/' + m.getSubtype()).forEach(t -> addContentType(object, t));
        return object;
    }),

    RESPONSE_BUILDER_TYPE_STRING(ofNonStatic(CLASS_RESPONSE_BUILDER, "type", RESPONSE_BUILDER, STRING), (object, arguments) -> {
        arguments.get(0).getPossibleValues().stream()
                .map(t -> (String) t).forEach(t -> addContentType(object, t));
        return object;
    }),

    RESPONSE_BUILDER_VARIANT(ofNonStatic(CLASS_RESPONSE_BUILDER, "variant", RESPONSE_BUILDER, VARIANT), (object, arguments) -> {
        addHeader(object, HttpHeaders.CONTENT_LANGUAGE);
        addHeader(object, HttpHeaders.CONTENT_ENCODING);
        return object;
    }),

    RESPONSE_BUILDER_VARIANTS_LIST(ofNonStatic(CLASS_RESPONSE_BUILDER, "variants", RESPONSE_BUILDER, LIST), (object, arguments) ->
            addHeader(object, HttpHeaders.VARY)),

    RESPONSE_BUILDER_VARIANTS_ARRAY(ofNonStatic(CLASS_RESPONSE_BUILDER, "variants", RESPONSE_BUILDER, "[Ljavax/ws/rs/core/Variant;"), (object, arguments) ->
            addHeader(object, HttpHeaders.VARY)),

    // static methods in Response --------------------------

    RESPONSE_STATUS_ENUM(ofStatic(CLASS_RESPONSE, "status", RESPONSE_BUILDER, RESPONSE_STATUS), (notAvailable, arguments) -> {
        final Element object = new Element(RESPONSE, new HttpResponse());
        arguments.get(0).getPossibleValues().stream()
                .map(status -> ((Response.Status) status).getStatusCode()).forEach(s -> addStatus(object, s));
        return object;
    }),

    RESPONSE_STATUS_INT(ofStatic(CLASS_RESPONSE, "status", RESPONSE_BUILDER, PRIMITIVE_INT), (notAvailable, arguments) -> {
        final Element object = new Element(RESPONSE, new HttpResponse());
        arguments.get(0).getPossibleValues().stream()
                .map(status -> (int) status).forEach(s -> addStatus(object, s));
        return object;
    }),

    RESPONSE_OK(ofStatic(CLASS_RESPONSE, "ok", RESPONSE_BUILDER), (notAvailable, arguments) -> {
        final Element object = new Element(RESPONSE, new HttpResponse());
        return addStatus(object, Response.Status.OK.getStatusCode());
    }),

    RESPONSE_OK_ENTITY(ofStatic(CLASS_RESPONSE, "ok", RESPONSE_BUILDER, OBJECT), (notAvailable, arguments) -> {
        final Element object = new Element(RESPONSE, new HttpResponse());
        addStatus(object, Response.Status.OK.getStatusCode());
        return addEntity(object, arguments.get(0));
    }),

    RESPONSE_OK_VARIANT(ofStatic(CLASS_RESPONSE, "ok", RESPONSE_BUILDER, OBJECT, VARIANT), (notAvailable, arguments) -> {
        final Element object = new Element(RESPONSE, new HttpResponse());
        addStatus(object, Response.Status.OK.getStatusCode());
        addEntity(object, arguments.get(0));
        addHeader(object, HttpHeaders.CONTENT_LANGUAGE);
        return addHeader(object, HttpHeaders.CONTENT_ENCODING);
    }),

    RESPONSE_OK_MEDIATYPE(ofStatic(CLASS_RESPONSE, "ok", RESPONSE_BUILDER, OBJECT, "Ljavax/ws/rs/core/MediaType;"), (notAvailable, arguments) -> {
        final Element object = new Element(RESPONSE, new HttpResponse());
        addStatus(object, Response.Status.OK.getStatusCode());
        arguments.get(1).getPossibleValues().stream().map(m -> (MediaType) m)
                .map(m -> m.getType() + '/' + m.getSubtype()).forEach(t -> addContentType(object, t));
        return addEntity(object, arguments.get(0));
    }),

    RESPONSE_OK_MEDIATYPE_STRING(ofStatic(CLASS_RESPONSE, "ok", RESPONSE_BUILDER, OBJECT, STRING), (notAvailable, arguments) -> {
        final Element object = new Element(RESPONSE, new HttpResponse());
        addStatus(object, Response.Status.OK.getStatusCode());
        arguments.get(1).getPossibleValues().stream()
                .map(t -> (String) t).forEach(t -> addContentType(object, t));
        return addEntity(object, arguments.get(0));
    }),

    RESPONSE_ACCEPTED(ofStatic(CLASS_RESPONSE, "accepted", RESPONSE_BUILDER), (notAvailable, arguments) -> {
        final Element object = new Element(RESPONSE, new HttpResponse());
        return addStatus(object, Response.Status.ACCEPTED.getStatusCode());
    }),

    RESPONSE_ACCEPTED_ENTITY(ofStatic(CLASS_RESPONSE, "accepted", RESPONSE_BUILDER, OBJECT), (notAvailable, arguments) -> {
        final Element object = new Element(RESPONSE, new HttpResponse());
        addStatus(object, Response.Status.ACCEPTED.getStatusCode());
        return addEntity(object, arguments.get(0));
    }),

    RESPONSE_CREATED(ofStatic(CLASS_RESPONSE, "created", RESPONSE_BUILDER, URI), (notAvailable, arguments) -> {
        final Element object = new Element(RESPONSE, new HttpResponse());
        addStatus(object, Response.Status.CREATED.getStatusCode());
        return addHeader(object, HttpHeaders.LOCATION);
    }),

    RESPONSE_NO_CONTENT(ofStatic(CLASS_RESPONSE, "noContent", RESPONSE_BUILDER), (notAvailable, arguments) -> {
        final Element object = new Element(RESPONSE, new HttpResponse());
        return addStatus(object, Response.Status.NO_CONTENT.getStatusCode());
    }),

    RESPONSE_NOT_ACCEPTABLE(ofStatic(CLASS_RESPONSE, "notAcceptable", RESPONSE_BUILDER, LIST), (notAvailable, arguments) -> {
        final Element object = new Element(RESPONSE, new HttpResponse());
        addStatus(object, Response.Status.NOT_ACCEPTABLE.getStatusCode());
        return addHeader(object, HttpHeaders.VARY);
    }),

    RESPONSE_NOT_MODIFIED(ofStatic(CLASS_RESPONSE, "notModified", RESPONSE_BUILDER), (notAvailable, arguments) -> {
        final Element object = new Element(RESPONSE, new HttpResponse());
        return addStatus(object, Response.Status.NOT_MODIFIED.getStatusCode());
    }),

    RESPONSE_NOT_MODIFIED_ENTITYTAG(ofStatic(CLASS_RESPONSE, "notModified", RESPONSE_BUILDER, ENTITY_TAG), (notAvailable, arguments) -> {
        final Element object = new Element(RESPONSE, new HttpResponse());
        addStatus(object, Response.Status.NOT_MODIFIED.getStatusCode());
        return addHeader(object, HttpHeaders.ETAG);
    }),

    RESPONSE_NOT_MODIFIED_ENTITYTAG_STRING(ofStatic(CLASS_RESPONSE, "notModified", RESPONSE_BUILDER, STRING), (notAvailable, arguments) -> {
        final Element object = new Element(RESPONSE, new HttpResponse());
        addStatus(object, Response.Status.NOT_MODIFIED.getStatusCode());
        return addHeader(object, HttpHeaders.ETAG);
    }),

    RESPONSE_SEE_OTHER(ofStatic(CLASS_RESPONSE, "seeOther", RESPONSE_BUILDER, URI), (notAvailable, arguments) -> {
        final Element object = new Element(RESPONSE, new HttpResponse());
        addStatus(object, Response.Status.SEE_OTHER.getStatusCode());
        return addHeader(object, HttpHeaders.LOCATION);
    }),

    RESPONSE_TEMPORARY_REDIRECT(ofStatic(CLASS_RESPONSE, "temporaryRedirect", RESPONSE_BUILDER, URI), (notAvailable, arguments) -> {
        final Element object = new Element(RESPONSE, new HttpResponse());
        addStatus(object, Response.Status.TEMPORARY_REDIRECT.getStatusCode());
        return addHeader(object, HttpHeaders.LOCATION);
    }),

    RESPONSE_SERVER_ERROR(ofStatic(CLASS_RESPONSE, "serverError", RESPONSE_BUILDER), (notAvailable, arguments) -> {
        final Element object = new Element(RESPONSE, new HttpResponse());
        return addStatus(object, Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    }),

    // WebApplicationExceptions --------------------------

    WEB_APPLICATION_EXCEPTION_EMPTY(ofNonStatic(CLASS_WEB_APPLICATION_EXCEPTION, INITIALIZER_NAME, PRIMITIVE_VOID), (notAvailable, arguments) -> {
        final Element object = new Element(RESPONSE, new HttpResponse());
        return addStatus(object, Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    }),

    WEB_APPLICATION_EXCEPTION_MESSAGE(ofNonStatic(CLASS_WEB_APPLICATION_EXCEPTION, INITIALIZER_NAME, PRIMITIVE_VOID, STRING), (notAvailable, arguments) -> {
        final Element object = new Element(RESPONSE, new HttpResponse());
        return addStatus(object, Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    }),

    WEB_APPLICATION_EXCEPTION_RESPONSE(ofNonStatic(CLASS_WEB_APPLICATION_EXCEPTION, INITIALIZER_NAME, PRIMITIVE_VOID, RESPONSE), (notAvailable, arguments) -> arguments.get(0)),

    WEB_APPLICATION_EXCEPTION_MESSAGE_RESPONSE(ofNonStatic(CLASS_WEB_APPLICATION_EXCEPTION, INITIALIZER_NAME, PRIMITIVE_VOID, STRING, RESPONSE),
            (notAvailable, arguments) -> arguments.get(1)),

    WEB_APPLICATION_EXCEPTION_STATUS(ofNonStatic(CLASS_WEB_APPLICATION_EXCEPTION, INITIALIZER_NAME, PRIMITIVE_VOID, PRIMITIVE_INT), (notAvailable, arguments) -> {
        final Element object = new Element(RESPONSE, new HttpResponse());
        arguments.get(0).getPossibleValues().stream()
                .map(status -> (int) status).forEach(s -> addStatus(object, s));
        return object;
    }),

    WEB_APPLICATION_EXCEPTION_MESSAGE_STATUS(ofNonStatic(CLASS_WEB_APPLICATION_EXCEPTION, INITIALIZER_NAME, PRIMITIVE_VOID, STRING, PRIMITIVE_INT), (notAvailable, arguments) -> {
        final Element object = new Element(RESPONSE, new HttpResponse());
        arguments.get(1).getPossibleValues().stream()
                .map(status -> (int) status).forEach(s -> addStatus(object, s));
        return object;
    }),

    WEB_APPLICATION_EXCEPTION_RESPONSE_STATUS(ofNonStatic(CLASS_WEB_APPLICATION_EXCEPTION, INITIALIZER_NAME, PRIMITIVE_VOID, RESPONSE_STATUS), (notAvailable, arguments) -> {
        final Element object = new Element(RESPONSE, new HttpResponse());
        arguments.get(0).getPossibleValues().stream()
                .map(status -> ((Response.Status) status).getStatusCode()).forEach(s -> addStatus(object, s));
        return object;
    }),

    WEB_APPLICATION_EXCEPTION_MESSAGE_RESPONSE_STATUS(ofNonStatic(CLASS_WEB_APPLICATION_EXCEPTION, INITIALIZER_NAME, PRIMITIVE_VOID, STRING, RESPONSE_STATUS), (notAvailable, arguments) -> {
        final Element object = new Element(RESPONSE, new HttpResponse());
        arguments.get(1).getPossibleValues().stream()
                .map(status -> ((Response.Status) status).getStatusCode()).forEach(s -> addStatus(object, s));
        return object;
    }),

    WEB_APPLICATION_EXCEPTION_CAUSE(ofNonStatic(CLASS_WEB_APPLICATION_EXCEPTION, INITIALIZER_NAME, PRIMITIVE_VOID, THROWABLE), (notAvailable, arguments) -> {
        final Element object = new Element(RESPONSE, new HttpResponse());
        return addStatus(object, Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    }),

    WEB_APPLICATION_EXCEPTION_MESSAGE_CAUSE(ofNonStatic(CLASS_WEB_APPLICATION_EXCEPTION, INITIALIZER_NAME, PRIMITIVE_VOID, STRING, THROWABLE), (notAvailable, arguments) -> {
        final Element object = new Element(RESPONSE, new HttpResponse());
        return addStatus(object, Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    }),

    WEB_APPLICATION_EXCEPTION_CAUSE_RESPONSE(ofNonStatic(CLASS_WEB_APPLICATION_EXCEPTION, INITIALIZER_NAME, PRIMITIVE_VOID, THROWABLE, RESPONSE),
            (notAvailable, arguments) -> arguments.get(1)),

    WEB_APPLICATION_EXCEPTION_MESSAGE_CAUSE_RESPONSE(ofNonStatic(CLASS_WEB_APPLICATION_EXCEPTION, INITIALIZER_NAME, PRIMITIVE_VOID, STRING, THROWABLE, RESPONSE),
            (notAvailable, arguments) -> arguments.get(2)),

    WEB_APPLICATION_EXCEPTION_CAUSE_STATUS(ofNonStatic(CLASS_WEB_APPLICATION_EXCEPTION, INITIALIZER_NAME, PRIMITIVE_VOID, THROWABLE, PRIMITIVE_INT), (notAvailable, arguments) -> {
        final Element object = new Element(RESPONSE, new HttpResponse());
        arguments.get(1).getPossibleValues().stream()
                .map(status -> (int) status).forEach(s -> addStatus(object, s));
        return object;
    }),

    WEB_APPLICATION_EXCEPTION_MESSAGE_CAUSE_STATUS(ofNonStatic(CLASS_WEB_APPLICATION_EXCEPTION, INITIALIZER_NAME, PRIMITIVE_VOID, STRING, THROWABLE, PRIMITIVE_INT), (notAvailable, arguments) -> {
        final Element object = new Element(RESPONSE, new HttpResponse());
        arguments.get(2).getPossibleValues().stream()
                .map(status -> (int) status).forEach(s -> addStatus(object, s));
        return object;
    }),

    WEB_APPLICATION_EXCEPTION_CAUSE_RESPONSE_STATUS(ofNonStatic(CLASS_WEB_APPLICATION_EXCEPTION, INITIALIZER_NAME, PRIMITIVE_VOID, THROWABLE, RESPONSE_STATUS), (notAvailable, arguments) -> {
        final Element object = new Element(RESPONSE, new HttpResponse());
        arguments.get(1).getPossibleValues().stream()
                .map(status -> ((Response.Status) status).getStatusCode()).forEach(s -> addStatus(object, s));
        return object;
    }),

    WEB_APPLICATION_EXCEPTION_MESSAGE_CAUSE_RESPONSE_STATUS(ofNonStatic(CLASS_WEB_APPLICATION_EXCEPTION, INITIALIZER_NAME, PRIMITIVE_VOID, STRING, THROWABLE, RESPONSE_STATUS),
            (notAvailable, arguments) -> {
                final Element object = new Element(RESPONSE, new HttpResponse());
                arguments.get(2).getPossibleValues().stream()
                        .map(status -> ((Response.Status) status).getStatusCode()).forEach(s -> addStatus(object, s));
                return object;
            }),

    // other methods --------------------------

    RESOURCE_CONTEXT_INIT(ofNonStatic(CLASS_RESOURCE_CONTEXT, "getResource", OBJECT, CLASS),
            (object, arguments) -> new Element(arguments.get(0).getPossibleValues().stream()
                    .filter(s -> s instanceof String).map(s -> (String) s).collect(Collectors.toSet()))
    ),

    RESOURCE_CONTEXT_GET(ofNonStatic(CLASS_RESOURCE_CONTEXT, "initResource", OBJECT, OBJECT),
            (object, arguments) -> new Element(arguments.get(0).getTypes())),

    INTEGER_VALUE_OF(ofStatic(CLASS_INTEGER, "valueOf", PRIMITIVE_INT, INTEGER),
            (object, arguments) -> new Element(INTEGER, arguments.get(0).getPossibleValues().toArray())),

    DOUBLE_VALUE_OF(ofStatic(CLASS_DOUBLE, "valueOf", PRIMITIVE_DOUBLE, DOUBLE),
            (object, arguments) -> new Element(INTEGER, arguments.get(0).getPossibleValues().toArray())),

    LONG_VALUE_OF(ofStatic(CLASS_LONG, "valueOf", PRIMITIVE_LONG, LONG),
            (object, arguments) -> new Element(INTEGER, arguments.get(0).getPossibleValues().toArray())),

    // stream related methods --------------------------

    LIST_STREAM(ofNonStatic(CLASS_LIST, "stream", STREAM),
            (object, arguments) -> new Element(object.getTypes())),

    LIST_FOR_EACH(ofNonStatic(CLASS_LIST, "forEach", PRIMITIVE_VOID, CONSUMER), (object, arguments) -> {
        if (arguments.get(0) instanceof MethodHandle)
            ((Method) arguments.get(0)).invoke(null, Collections.singletonList(object));
        return null;
    }),

    SET_STREAM(ofNonStatic(CLASS_SET, "stream", STREAM),
            (object, arguments) -> new Element(object.getTypes())),

    SET_FOR_EACH(ofNonStatic(CLASS_SET, "forEach", PRIMITIVE_VOID, CONSUMER), (object, arguments) -> {
        if (arguments.get(0) instanceof MethodHandle)
            ((Method) arguments.get(0)).invoke(null, Collections.singletonList(object));
        return null;
    }),

    STREAM_COLLECT(ofNonStatic(CLASS_STREAM, "collect", OBJECT, SUPPLIER, BI_CONSUMER, BI_CONSUMER),
            (object, arguments) -> {
                if (arguments.get(0) instanceof MethodHandle && arguments.get(1) instanceof MethodHandle) {
                    final Element collectionElement = ((Method) arguments.get(0)).invoke(null, Collections.emptyList());
                    ((Method) arguments.get(1)).invoke(null, Arrays.asList(collectionElement, object));
                    return collectionElement;
                }
                return Element.EMPTY;
            }),

    STREAM_FOR_EACH(ofNonStatic(CLASS_STREAM, "forEach", PRIMITIVE_VOID, CONSUMER), (object, arguments) -> {
        if (arguments.get(0) instanceof MethodHandle)
            ((Method) arguments.get(0)).invoke(null, Collections.singletonList(object));
        return null;
    }),

    STREAM_MAP(ofNonStatic(CLASS_STREAM, "map", STREAM, "Ljava/util/function/Function;"), (object, arguments) -> {
        if (arguments.get(0) instanceof MethodHandle) {
            return ((MethodHandle) arguments.get(0)).invoke(null, Collections.singletonList(object));
        }
        return Element.EMPTY;
    });

    private final MethodIdentifier identifier;

    private final BiFunction<Element, List<Element>, Element> function;

    KnownResponseResultMethod(final MethodIdentifier identifier,
                              final BiFunction<Element, List<Element>, Element> function) {
        this.identifier = identifier;
        this.function = function;
    }

    @Override
    public Element invoke(final Element object, final List<Element> arguments) {
        if (arguments.size() != identifier.getParameters())
            throw new IllegalArgumentException("Method arguments do not match expected signature!");

        return function.apply(object, arguments);
    }

    @Override
    public boolean matches(final MethodIdentifier identifier) {
        return this.identifier.equals(identifier);
    }

    private static Element addHeader(final Element object, final String header) {
        object.getPossibleValues().stream().filter(r -> r instanceof HttpResponse).map(r -> (HttpResponse) r).forEach(r -> r.getHeaders().add(header));
        return object;
    }

    private static Element addEntity(final Element object, final Element argument) {
        object.getPossibleValues().stream().filter(r -> r instanceof HttpResponse).map(r -> (HttpResponse) r)
                .forEach(r -> {
                    r.getEntityTypes().addAll(argument.getTypes());
                    argument.getPossibleValues().stream().filter(j -> j instanceof JsonValue).map(j -> (JsonValue) j).forEach(j -> r.getInlineEntities().add(j));
                });
        return object;
    }

    private static Element addStatus(final Element object, final Integer status) {
        object.getPossibleValues().stream().filter(r -> r instanceof HttpResponse).map(r -> (HttpResponse) r).forEach(r -> r.getStatuses().add(status));
        return object;
    }

    private static Element addContentType(final Element object, final String type) {
        object.getPossibleValues().stream().filter(r -> r instanceof HttpResponse).map(r -> (HttpResponse) r).forEach(r -> r.getContentTypes().add(type));
        return object;
    }

}