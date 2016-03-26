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

import javax.ws.rs.core.*;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.BiFunction;
import java.util.function.Function;
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

    RESPONSE_BUILDER_BUILD(ofNonStatic(RESPONSE_BUILDER, "build", RESPONSE), (object, arguments) -> object),

    RESPONSE_BUILDER_CACHE_CONTROL(ofNonStatic(RESPONSE_BUILDER, "cacheControl", RESPONSE_BUILDER, CacheControl.class.getName()), (object, arguments) ->
            addHeader(object, HttpHeaders.CACHE_CONTROL)),

    RESPONSE_BUILDER_CONTENT_LOCATION(ofNonStatic(RESPONSE_BUILDER, "contentLocation", RESPONSE_BUILDER, URI), (object, arguments) ->
            addHeader(object, HttpHeaders.CONTENT_LOCATION)),

    RESPONSE_BUILDER_COOKIE(ofNonStatic(RESPONSE_BUILDER, "cookie", RESPONSE_BUILDER, NewCookie[].class.getName()), (object, arguments) ->
            addHeader(object, HttpHeaders.SET_COOKIE)),

    RESPONSE_BUILDER_ENCODING(ofNonStatic(RESPONSE_BUILDER, "encoding", RESPONSE_BUILDER, STRING), (object, arguments) ->
            addHeader(object, HttpHeaders.CONTENT_ENCODING)),

    RESPONSE_BUILDER_ENTITY(ofNonStatic(RESPONSE_BUILDER, "entity", RESPONSE_BUILDER, OBJECT), (object, arguments) ->
            addEntity(object, arguments.get(0))),

    RESPONSE_BUILDER_ENTITY_ANNOTATION(ofNonStatic(RESPONSE_BUILDER, "entity", RESPONSE_BUILDER, OBJECT, Annotation[].class.getName()), (object, arguments) ->
            addEntity(object, arguments.get(0))),

    RESPONSE_BUILDER_EXPIRES(ofNonStatic(RESPONSE_BUILDER, "expires", RESPONSE_BUILDER, DATE), (object, arguments) ->
            addHeader(object, HttpHeaders.EXPIRES)),

    RESPONSE_BUILDER_HEADER(ofNonStatic(RESPONSE_BUILDER, "header", RESPONSE_BUILDER, STRING, OBJECT), (object, arguments) -> {
        arguments.get(0).getPossibleValues().stream()
                .map(header -> (String) header).forEach(h -> addHeader(object, h));
        return object;
    }),

    RESPONSE_BUILDER_LANGUAGE_LOCALE(ofNonStatic(RESPONSE_BUILDER, "language", RESPONSE_BUILDER, Locale.class.getName()), (object, arguments) ->
            addHeader(object, HttpHeaders.CONTENT_LANGUAGE)),

    RESPONSE_BUILDER_LANGUAGE_STRING(ofNonStatic(RESPONSE_BUILDER, "language", RESPONSE_BUILDER, STRING), (object, arguments) ->
            addHeader(object, HttpHeaders.CONTENT_LANGUAGE)),

    RESPONSE_BUILDER_LAST_MODIFIED(ofNonStatic(RESPONSE_BUILDER, "lastModified", RESPONSE_BUILDER, DATE), (object, arguments) ->
            addHeader(object, HttpHeaders.LAST_MODIFIED)),

    RESPONSE_BUILDER_LINK_URI(ofNonStatic(RESPONSE_BUILDER, "link", RESPONSE_BUILDER, URI, STRING), (object, arguments) ->
            addHeader(object, HttpHeaders.LINK)),

    RESPONSE_BUILDER_LINK_STRING(ofNonStatic(RESPONSE_BUILDER, "link", RESPONSE_BUILDER, STRING, STRING), (object, arguments) ->
            addHeader(object, HttpHeaders.LINK)),

    RESPONSE_BUILDER_LINKS(ofNonStatic(RESPONSE_BUILDER, "links", RESPONSE_BUILDER, Link[].class.getName()), (object, arguments) ->
            addHeader(object, HttpHeaders.LINK)),

    RESPONSE_BUILDER_LOCATION(ofNonStatic(RESPONSE_BUILDER, "location", RESPONSE_BUILDER, URI), (object, arguments) ->
            addHeader(object, HttpHeaders.LOCATION)),

    RESPONSE_BUILDER_STATUS_ENUM(ofNonStatic(RESPONSE_BUILDER, "status", RESPONSE_BUILDER, RESPONSE_STATUS), (object, arguments) -> {
        arguments.get(0).getPossibleValues().stream()
                .map(status -> ((Response.Status) status).getStatusCode()).forEach(s -> addStatus(object, s));
        return object;
    }),

    RESPONSE_BUILDER_STATUS_INT(ofNonStatic(RESPONSE_BUILDER, "status", RESPONSE_BUILDER, PRIMITIVE_INT), (object, arguments) -> {
        arguments.get(0).getPossibleValues().stream()
                .map(status -> (int) status).forEach(s -> addStatus(object, s));
        return object;
    }),

    RESPONSE_BUILDER_TAG_ENTITY(ofNonStatic(RESPONSE_BUILDER, "tag", RESPONSE_BUILDER, ENTITY_TAG), (object, arguments) ->
            addHeader(object, HttpHeaders.ETAG)),

    RESPONSE_BUILDER_TAG_STRING(ofNonStatic(RESPONSE_BUILDER, "tag", RESPONSE_BUILDER, STRING), (object, arguments) ->
            addHeader(object, HttpHeaders.ETAG)),

    RESPONSE_BUILDER_TYPE(ofNonStatic(RESPONSE_BUILDER, "type", RESPONSE_BUILDER, MediaType.class.getName()), (object, arguments) -> {
        arguments.get(0).getPossibleValues().stream()
                .map(m -> (MediaType) m).map(m -> m.getType() + '/' + m.getSubtype()).forEach(t -> addContentType(object, t));
        return object;
    }),

    RESPONSE_BUILDER_TYPE_STRING(ofNonStatic(RESPONSE_BUILDER, "type", RESPONSE_BUILDER, STRING), (object, arguments) -> {
        arguments.get(0).getPossibleValues().stream()
                .map(t -> (String) t).forEach(t -> addContentType(object, t));
        return object;
    }),

    RESPONSE_BUILDER_VARIANT(ofNonStatic(RESPONSE_BUILDER, "variant", RESPONSE_BUILDER, VARIANT), (object, arguments) -> {
        addHeader(object, HttpHeaders.CONTENT_LANGUAGE);
        addHeader(object, HttpHeaders.CONTENT_ENCODING);
        return object;
    }),

    RESPONSE_BUILDER_VARIANTS_LIST(ofNonStatic(RESPONSE_BUILDER, "variants", RESPONSE_BUILDER, LIST), (object, arguments) ->
            addHeader(object, HttpHeaders.VARY)),

    RESPONSE_BUILDER_VARIANTS_ARRAY(ofNonStatic(RESPONSE_BUILDER, "variants", RESPONSE_BUILDER, Variant[].class.getName()), (object, arguments) ->
            addHeader(object, HttpHeaders.VARY)),

    // static methods in Response --------------------------

    RESPONSE_STATUS_ENUM(ofStatic(RESPONSE, "status", RESPONSE_BUILDER, RESPONSE_STATUS), (notAvailable, arguments) -> {
        final Element object = new Element(RESPONSE, new HttpResponse());
        arguments.get(0).getPossibleValues().stream()
                .map(status -> ((Response.Status) status).getStatusCode()).forEach(s -> addStatus(object, s));
        return object;
    }),

    RESPONSE_STATUS_INT(ofStatic(RESPONSE, "status", RESPONSE_BUILDER, PRIMITIVE_INT), (notAvailable, arguments) -> {
        final Element object = new Element(RESPONSE, new HttpResponse());
        arguments.get(0).getPossibleValues().stream()
                .map(status -> (int) status).forEach(s -> addStatus(object, s));
        return object;
    }),

    RESPONSE_OK(ofStatic(RESPONSE, "ok", RESPONSE_BUILDER), (notAvailable, arguments) -> {
        final Element object = new Element(RESPONSE, new HttpResponse());
        return addStatus(object, Response.Status.OK.getStatusCode());
    }),

    RESPONSE_OK_ENTITY(ofStatic(RESPONSE, "ok", RESPONSE_BUILDER, OBJECT), (notAvailable, arguments) -> {
        final Element object = new Element(RESPONSE, new HttpResponse());
        addStatus(object, Response.Status.OK.getStatusCode());
        return addEntity(object, arguments.get(0));
    }),

    RESPONSE_OK_VARIANT(ofStatic(RESPONSE, "ok", RESPONSE_BUILDER, OBJECT, VARIANT), (notAvailable, arguments) -> {
        final Element object = new Element(RESPONSE, new HttpResponse());
        addStatus(object, Response.Status.OK.getStatusCode());
        addEntity(object, arguments.get(0));
        addHeader(object, HttpHeaders.CONTENT_LANGUAGE);
        return addHeader(object, HttpHeaders.CONTENT_ENCODING);
    }),

    RESPONSE_OK_MEDIATYPE(ofStatic(RESPONSE, "ok", RESPONSE_BUILDER, OBJECT, MediaType.class.getName()), (notAvailable, arguments) -> {
        final Element object = new Element(RESPONSE, new HttpResponse());
        addStatus(object, Response.Status.OK.getStatusCode());
        arguments.get(1).getPossibleValues().stream().map(m -> (MediaType) m)
                .map(m -> m.getType() + '/' + m.getSubtype()).forEach(t -> addContentType(object, t));
        return addEntity(object, arguments.get(0));
    }),

    RESPONSE_OK_MEDIATYPE_STRING(ofStatic(RESPONSE, "ok", RESPONSE_BUILDER, OBJECT, STRING), (notAvailable, arguments) -> {
        final Element object = new Element(RESPONSE, new HttpResponse());
        addStatus(object, Response.Status.OK.getStatusCode());
        arguments.get(1).getPossibleValues().stream()
                .map(t -> (String) t).forEach(t -> addContentType(object, t));
        return addEntity(object, arguments.get(0));
    }),

    RESPONSE_ACCEPTED(ofStatic(RESPONSE, "accepted", RESPONSE_BUILDER), (notAvailable, arguments) -> {
        final Element object = new Element(RESPONSE, new HttpResponse());
        return addStatus(object, Response.Status.ACCEPTED.getStatusCode());
    }),

    RESPONSE_ACCEPTED_ENTITY(ofStatic(RESPONSE, "accepted", RESPONSE_BUILDER, OBJECT), (notAvailable, arguments) -> {
        final Element object = new Element(RESPONSE, new HttpResponse());
        addStatus(object, Response.Status.ACCEPTED.getStatusCode());
        return addEntity(object, arguments.get(0));
    }),

    RESPONSE_CREATED(ofStatic(RESPONSE, "created", RESPONSE_BUILDER, URI), (notAvailable, arguments) -> {
        final Element object = new Element(RESPONSE, new HttpResponse());
        addStatus(object, Response.Status.CREATED.getStatusCode());
        return addHeader(object, HttpHeaders.LOCATION);
    }),

    RESPONSE_NO_CONTENT(ofStatic(RESPONSE, "noContent", RESPONSE_BUILDER), (notAvailable, arguments) -> {
        final Element object = new Element(RESPONSE, new HttpResponse());
        return addStatus(object, Response.Status.NO_CONTENT.getStatusCode());
    }),

    RESPONSE_NOT_ACCEPTABLE(ofStatic(RESPONSE, "notAcceptable", RESPONSE_BUILDER, LIST), (notAvailable, arguments) -> {
        final Element object = new Element(RESPONSE, new HttpResponse());
        addStatus(object, Response.Status.NOT_ACCEPTABLE.getStatusCode());
        return addHeader(object, HttpHeaders.VARY);
    }),

    RESPONSE_NOT_MODIFIED(ofStatic(RESPONSE, "notModified", RESPONSE_BUILDER), (notAvailable, arguments) -> {
        final Element object = new Element(RESPONSE, new HttpResponse());
        return addStatus(object, Response.Status.NOT_MODIFIED.getStatusCode());
    }),

    RESPONSE_NOT_MODIFIED_ENTITYTAG(ofStatic(RESPONSE, "notModified", RESPONSE_BUILDER, ENTITY_TAG), (notAvailable, arguments) -> {
        final Element object = new Element(RESPONSE, new HttpResponse());
        addStatus(object, Response.Status.NOT_MODIFIED.getStatusCode());
        return addHeader(object, HttpHeaders.ETAG);
    }),

    RESPONSE_NOT_MODIFIED_ENTITYTAG_STRING(ofStatic(RESPONSE, "notModified", RESPONSE_BUILDER, STRING), (notAvailable, arguments) -> {
        final Element object = new Element(RESPONSE, new HttpResponse());
        addStatus(object, Response.Status.NOT_MODIFIED.getStatusCode());
        return addHeader(object, HttpHeaders.ETAG);
    }),

    RESPONSE_SEE_OTHER(ofStatic(RESPONSE, "seeOther", RESPONSE_BUILDER, URI), (notAvailable, arguments) -> {
        final Element object = new Element(RESPONSE, new HttpResponse());
        addStatus(object, Response.Status.SEE_OTHER.getStatusCode());
        return addHeader(object, HttpHeaders.LOCATION);
    }),

    RESPONSE_TEMPORARY_REDIRECT(ofStatic(RESPONSE, "temporaryRedirect", RESPONSE_BUILDER, URI), (notAvailable, arguments) -> {
        final Element object = new Element(RESPONSE, new HttpResponse());
        addStatus(object, Response.Status.TEMPORARY_REDIRECT.getStatusCode());
        return addHeader(object, HttpHeaders.LOCATION);
    }),

    RESPONSE_SERVER_ERROR(ofStatic(RESPONSE, "serverError", RESPONSE_BUILDER), (notAvailable, arguments) -> {
        final Element object = new Element(RESPONSE, new HttpResponse());
        return addStatus(object, Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    }),

    // WebApplicationExceptions --------------------------

    WEB_APPLICATION_EXCEPTION_EMPTY(ofNonStatic(WEB_APPLICATION_EXCEPTION, INITIALIZER_NAME, PRIMITIVE_VOID), (notAvailable, arguments) -> {
        final Element object = new Element(RESPONSE, new HttpResponse());
        return addStatus(object, Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    }),

    WEB_APPLICATION_EXCEPTION_MESSAGE(ofNonStatic(WEB_APPLICATION_EXCEPTION, INITIALIZER_NAME, PRIMITIVE_VOID, STRING), (notAvailable, arguments) -> {
        final Element object = new Element(RESPONSE, new HttpResponse());
        return addStatus(object, Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    }),

    WEB_APPLICATION_EXCEPTION_RESPONSE(ofNonStatic(WEB_APPLICATION_EXCEPTION, INITIALIZER_NAME, PRIMITIVE_VOID, RESPONSE), (notAvailable, arguments) -> arguments.get(0)),

    WEB_APPLICATION_EXCEPTION_MESSAGE_RESPONSE(ofNonStatic(WEB_APPLICATION_EXCEPTION, INITIALIZER_NAME, PRIMITIVE_VOID, STRING, RESPONSE),
            (notAvailable, arguments) -> arguments.get(1)),

    WEB_APPLICATION_EXCEPTION_STATUS(ofNonStatic(WEB_APPLICATION_EXCEPTION, INITIALIZER_NAME, PRIMITIVE_VOID, PRIMITIVE_INT), (notAvailable, arguments) -> {
        final Element object = new Element(RESPONSE, new HttpResponse());
        arguments.get(0).getPossibleValues().stream()
                .map(status -> (int) status).forEach(s -> addStatus(object, s));
        return object;
    }),

    WEB_APPLICATION_EXCEPTION_MESSAGE_STATUS(ofNonStatic(WEB_APPLICATION_EXCEPTION, INITIALIZER_NAME, PRIMITIVE_VOID, STRING, PRIMITIVE_INT), (notAvailable, arguments) -> {
        final Element object = new Element(RESPONSE, new HttpResponse());
        arguments.get(1).getPossibleValues().stream()
                .map(status -> (int) status).forEach(s -> addStatus(object, s));
        return object;
    }),

    WEB_APPLICATION_EXCEPTION_RESPONSE_STATUS(ofNonStatic(WEB_APPLICATION_EXCEPTION, INITIALIZER_NAME, PRIMITIVE_VOID, RESPONSE_STATUS), (notAvailable, arguments) -> {
        final Element object = new Element(RESPONSE, new HttpResponse());
        arguments.get(0).getPossibleValues().stream()
                .map(status -> ((Response.Status) status).getStatusCode()).forEach(s -> addStatus(object, s));
        return object;
    }),

    WEB_APPLICATION_EXCEPTION_MESSAGE_RESPONSE_STATUS(ofNonStatic(WEB_APPLICATION_EXCEPTION, INITIALIZER_NAME, PRIMITIVE_VOID, STRING, RESPONSE_STATUS), (notAvailable, arguments) -> {
        final Element object = new Element(RESPONSE, new HttpResponse());
        arguments.get(1).getPossibleValues().stream()
                .map(status -> ((Response.Status) status).getStatusCode()).forEach(s -> addStatus(object, s));
        return object;
    }),

    WEB_APPLICATION_EXCEPTION_CAUSE(ofNonStatic(WEB_APPLICATION_EXCEPTION, INITIALIZER_NAME, PRIMITIVE_VOID, THROWABLE), (notAvailable, arguments) -> {
        final Element object = new Element(RESPONSE, new HttpResponse());
        return addStatus(object, Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    }),

    WEB_APPLICATION_EXCEPTION_MESSAGE_CAUSE(ofNonStatic(WEB_APPLICATION_EXCEPTION, INITIALIZER_NAME, PRIMITIVE_VOID, STRING, THROWABLE), (notAvailable, arguments) -> {
        final Element object = new Element(RESPONSE, new HttpResponse());
        return addStatus(object, Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    }),

    WEB_APPLICATION_EXCEPTION_CAUSE_RESPONSE(ofNonStatic(WEB_APPLICATION_EXCEPTION, INITIALIZER_NAME, PRIMITIVE_VOID, THROWABLE, RESPONSE),
            (notAvailable, arguments) -> arguments.get(1)),

    WEB_APPLICATION_EXCEPTION_MESSAGE_CAUSE_RESPONSE(ofNonStatic(WEB_APPLICATION_EXCEPTION, INITIALIZER_NAME, PRIMITIVE_VOID, STRING, THROWABLE, RESPONSE),
            (notAvailable, arguments) -> arguments.get(2)),

    WEB_APPLICATION_EXCEPTION_CAUSE_STATUS(ofNonStatic(WEB_APPLICATION_EXCEPTION, INITIALIZER_NAME, PRIMITIVE_VOID, THROWABLE, PRIMITIVE_INT), (notAvailable, arguments) -> {
        final Element object = new Element(RESPONSE, new HttpResponse());
        arguments.get(1).getPossibleValues().stream()
                .map(status -> (int) status).forEach(s -> addStatus(object, s));
        return object;
    }),

    WEB_APPLICATION_EXCEPTION_MESSAGE_CAUSE_STATUS(ofNonStatic(WEB_APPLICATION_EXCEPTION, INITIALIZER_NAME, PRIMITIVE_VOID, STRING, THROWABLE, PRIMITIVE_INT), (notAvailable, arguments) -> {
        final Element object = new Element(RESPONSE, new HttpResponse());
        arguments.get(2).getPossibleValues().stream()
                .map(status -> (int) status).forEach(s -> addStatus(object, s));
        return object;
    }),

    WEB_APPLICATION_EXCEPTION_CAUSE_RESPONSE_STATUS(ofNonStatic(WEB_APPLICATION_EXCEPTION, INITIALIZER_NAME, PRIMITIVE_VOID, THROWABLE, RESPONSE_STATUS), (notAvailable, arguments) -> {
        final Element object = new Element(RESPONSE, new HttpResponse());
        arguments.get(1).getPossibleValues().stream()
                .map(status -> ((Response.Status) status).getStatusCode()).forEach(s -> addStatus(object, s));
        return object;
    }),

    WEB_APPLICATION_EXCEPTION_MESSAGE_CAUSE_RESPONSE_STATUS(ofNonStatic(WEB_APPLICATION_EXCEPTION, INITIALIZER_NAME, PRIMITIVE_VOID, STRING, THROWABLE, RESPONSE_STATUS),
            (notAvailable, arguments) -> {
                final Element object = new Element(RESPONSE, new HttpResponse());
                arguments.get(2).getPossibleValues().stream()
                        .map(status -> ((Response.Status) status).getStatusCode()).forEach(s -> addStatus(object, s));
                return object;
            }),

    // other methods --------------------------

    RESOURCE_CONTEXT_INIT(ofNonStatic(RESOURCE_CONTEXT, "getResource", OBJECT, Class.class.getName()),
            (object, arguments) -> new Element(arguments.get(0).getPossibleValues().stream()
                    .filter(s -> s instanceof String).map(s -> (String) s).collect(Collectors.toSet()))
    ),

    RESOURCE_CONTEXT_GET(ofNonStatic(RESOURCE_CONTEXT, "initResource", OBJECT, OBJECT),
            (object, arguments) -> new Element(arguments.get(0).getTypes())),

    INTEGER_VALUE_OF(ofStatic(INTEGER, "valueOf", PRIMITIVE_INT, INTEGER),
            (object, arguments) -> new Element(INTEGER, arguments.get(0).getPossibleValues().toArray())),

    // stream related methods --------------------------

    LIST_STREAM(ofNonStatic(LIST, "stream", STREAM),
            (object, arguments) -> new Element(object.getTypes())),

    LIST_FOR_EACH(ofNonStatic(LIST, "forEach", PRIMITIVE_VOID, CONSUMER), (object, arguments) -> {
        if (arguments.get(0) instanceof MethodHandle)
            ((Method) arguments.get(0)).invoke(null, Collections.singletonList(object));
        return null;
    }),

    SET_STREAM(ofNonStatic(SET, "stream", STREAM),
            (object, arguments) -> new Element(object.getTypes())),

    SET_FOR_EACH(ofNonStatic(SET, "forEach", PRIMITIVE_VOID, CONSUMER), (object, arguments) -> {
        if (arguments.get(0) instanceof MethodHandle)
            ((Method) arguments.get(0)).invoke(null, Collections.singletonList(object));
        return null;
    }),

    STREAM_COLLECT(ofNonStatic(STREAM, "collect", OBJECT, SUPPLIER, BI_CONSUMER, BI_CONSUMER),
            (object, arguments) -> {
                if (arguments.get(0) instanceof MethodHandle && arguments.get(1) instanceof MethodHandle) {
                    final Element collectionElement = ((Method) arguments.get(0)).invoke(null, Collections.emptyList());
                    ((Method) arguments.get(1)).invoke(null, Arrays.asList(collectionElement, object));
                    return collectionElement;
                }
                return Element.EMPTY;
            }),

    STREAM_FOR_EACH(ofNonStatic(STREAM, "forEach", PRIMITIVE_VOID, CONSUMER), (object, arguments) -> {
        if (arguments.get(0) instanceof MethodHandle)
            ((Method) arguments.get(0)).invoke(null, Collections.singletonList(object));
        return null;
    }),

    STREAM_MAP(ofNonStatic(STREAM, "map", STREAM, Function.class.getName()), (object, arguments) -> {
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