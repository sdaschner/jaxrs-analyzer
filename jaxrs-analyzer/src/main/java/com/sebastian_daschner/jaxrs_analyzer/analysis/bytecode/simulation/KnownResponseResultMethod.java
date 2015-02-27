package com.sebastian_daschner.jaxrs_analyzer.analysis.bytecode.simulation;

import com.sebastian_daschner.jaxrs_analyzer.model.elements.Element;
import com.sebastian_daschner.jaxrs_analyzer.model.elements.HttpResponse;
import com.sebastian_daschner.jaxrs_analyzer.model.elements.JsonValue;
import com.sebastian_daschner.jaxrs_analyzer.model.methods.IdentifiableMethod;
import com.sebastian_daschner.jaxrs_analyzer.model.methods.MethodIdentifier;

import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.*;
import java.lang.annotation.Annotation;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.function.BiFunction;

import static com.sebastian_daschner.jaxrs_analyzer.analysis.bytecode.simulation.KnownResponseResultMethod.KnownNames.*;

/**
 * Known methods which apply logic to the result or to the return element.
 *
 * @author Sebastian Daschner
 */
public enum KnownResponseResultMethod implements IdentifiableMethod {

    // non-static methods in ResponseBuilder --------------------------

    RESPONSE_BUILDER_BUILD(MethodIdentifier.ofNonStatic(RESPONSE_BUILDER, "build", RESPONSE), (object, arguments) -> object),

    RESPONSE_BUILDER_CACHE_CONTROL(MethodIdentifier.ofNonStatic(RESPONSE_BUILDER, "cacheControl", RESPONSE_BUILDER, CacheControl.class.getName()), (object, arguments) ->
            addHeader(object, HttpHeaders.CACHE_CONTROL)),

    RESPONSE_BUILDER_CONTENT_LOCATION(MethodIdentifier.ofNonStatic(RESPONSE_BUILDER, "contentLocation", RESPONSE_BUILDER, URI), (object, arguments) ->
            addHeader(object, HttpHeaders.CONTENT_LOCATION)),

    RESPONSE_BUILDER_COOKIE(MethodIdentifier.ofNonStatic(RESPONSE_BUILDER, "cookie", RESPONSE_BUILDER, NewCookie.class.getName() + "[]"), (object, arguments) ->
            addHeader(object, HttpHeaders.SET_COOKIE)),

    RESPONSE_BUILDER_ENCODING(MethodIdentifier.ofNonStatic(RESPONSE_BUILDER, "encoding", RESPONSE_BUILDER, STRING), (object, arguments) ->
            addHeader(object, HttpHeaders.CONTENT_ENCODING)),

    RESPONSE_BUILDER_ENTITY(MethodIdentifier.ofNonStatic(RESPONSE_BUILDER, "entity", RESPONSE_BUILDER, OBJECT), (object, arguments) ->
            addEntity(object, arguments.get(0))),

    RESPONSE_BUILDER_ENTITY_ANNOTATION(MethodIdentifier.ofNonStatic(RESPONSE_BUILDER, "entity", RESPONSE_BUILDER, OBJECT, Annotation.class.getName() + "[]"), (object, arguments) ->
            addEntity(object, arguments.get(0))),

    RESPONSE_BUILDER_EXPIRES(MethodIdentifier.ofNonStatic(RESPONSE_BUILDER, "expires", RESPONSE_BUILDER, DATE), (object, arguments) ->
            addHeader(object, HttpHeaders.EXPIRES)),

    RESPONSE_BUILDER_HEADER(MethodIdentifier.ofNonStatic(RESPONSE_BUILDER, "header", RESPONSE_BUILDER, STRING, OBJECT), (object, arguments) -> {
        arguments.get(0).getPossibleValues().stream()
                .map(header -> (String) header).forEach(h -> addHeader(object, h));
        return object;
    }),

    RESPONSE_BUILDER_LANGUAGE_LOCALE(MethodIdentifier.ofNonStatic(RESPONSE_BUILDER, "language", RESPONSE_BUILDER, Locale.class.getName()), (object, arguments) ->
            addHeader(object, HttpHeaders.CONTENT_LANGUAGE)),

    RESPONSE_BUILDER_LANGUAGE_STRING(MethodIdentifier.ofNonStatic(RESPONSE_BUILDER, "language", RESPONSE_BUILDER, STRING), (object, arguments) ->
            addHeader(object, HttpHeaders.CONTENT_LANGUAGE)),

    RESPONSE_BUILDER_LAST_MODIFIED(MethodIdentifier.ofNonStatic(RESPONSE_BUILDER, "lastModified", RESPONSE_BUILDER, DATE), (object, arguments) ->
            addHeader(object, HttpHeaders.LAST_MODIFIED)),

    RESPONSE_BUILDER_LINK_URI(MethodIdentifier.ofNonStatic(RESPONSE_BUILDER, "link", RESPONSE_BUILDER, URI, STRING), (object, arguments) ->
            addHeader(object, HttpHeaders.LINK)),

    RESPONSE_BUILDER_LINK_STRING(MethodIdentifier.ofNonStatic(RESPONSE_BUILDER, "link", RESPONSE_BUILDER, STRING, STRING), (object, arguments) ->
            addHeader(object, HttpHeaders.LINK)),

    RESPONSE_BUILDER_LINKS(MethodIdentifier.ofNonStatic(RESPONSE_BUILDER, "links", RESPONSE_BUILDER, Link.class.getName() + "[]"), (object, arguments) ->
            addHeader(object, HttpHeaders.LINK)),

    RESPONSE_BUILDER_LOCATION(MethodIdentifier.ofNonStatic(RESPONSE_BUILDER, "location", RESPONSE_BUILDER, URI), (object, arguments) ->
            addHeader(object, HttpHeaders.LOCATION)),

    RESPONSE_BUILDER_STATUS_ENUM(MethodIdentifier.ofNonStatic(RESPONSE_BUILDER, "status", RESPONSE_BUILDER, RESPONSE_STATUS), (object, arguments) -> {
        arguments.get(0).getPossibleValues().stream()
                .map(status -> ((Response.Status) status).getStatusCode()).forEach(s -> addStatus(object, s));
        return object;
    }),

    RESPONSE_BUILDER_STATUS_INT(MethodIdentifier.ofNonStatic(RESPONSE_BUILDER, "status", RESPONSE_BUILDER, "int"), (object, arguments) -> {
        arguments.get(0).getPossibleValues().stream()
                .map(status -> (int) status).forEach(s -> addStatus(object, s));
        return object;
    }),

    RESPONSE_BUILDER_TAG_ENTITY(MethodIdentifier.ofNonStatic(RESPONSE_BUILDER, "tag", RESPONSE_BUILDER, EntityTag.class.getName()), (object, arguments) ->
            addHeader(object, HttpHeaders.ETAG)),

    RESPONSE_BUILDER_TAG_STRING(MethodIdentifier.ofNonStatic(RESPONSE_BUILDER, "tag", RESPONSE_BUILDER, STRING), (object, arguments) ->
            addHeader(object, HttpHeaders.ETAG)),

    RESPONSE_BUILDER_TYPE(MethodIdentifier.ofNonStatic(RESPONSE_BUILDER, "type", RESPONSE_BUILDER, MediaType.class.getName()), (object, arguments) -> {
        arguments.get(0).getPossibleValues().stream()
                .map(m -> (MediaType) m).map(m -> m.getType() + '/' + m.getSubtype()).forEach(t -> addContentType(object, t));
        return object;
    }),

    RESPONSE_BUILDER_TYPE_STRING(MethodIdentifier.ofNonStatic(RESPONSE_BUILDER, "type", RESPONSE_BUILDER, STRING), (object, arguments) -> {
        arguments.get(0).getPossibleValues().stream()
                .map(t -> (String) t).forEach(t -> addContentType(object, t));
        return object;
    }),

    RESPONSE_BUILDER_VARIANT(MethodIdentifier.ofNonStatic(RESPONSE_BUILDER, "variant", RESPONSE_BUILDER, VARIANT), (object, arguments) -> {
        addHeader(object, HttpHeaders.CONTENT_LANGUAGE);
        addHeader(object, HttpHeaders.CONTENT_ENCODING);
        return object;
    }),

    RESPONSE_BUILDER_VARIANTS_LIST(MethodIdentifier.ofNonStatic(RESPONSE_BUILDER, "variants", RESPONSE_BUILDER, List.class.getName()), (object, arguments) ->
            addHeader(object, HttpHeaders.VARY)),

    RESPONSE_BUILDER_VARIANTS_ARRAY(MethodIdentifier.ofNonStatic(RESPONSE_BUILDER, "variants", RESPONSE_BUILDER, VARIANT + "[]"), (object, arguments) ->
            addHeader(object, HttpHeaders.VARY)),

    // static methods in Response --------------------------

    RESPONSE_STATUS_ENUM(MethodIdentifier.ofStatic(RESPONSE, "status", RESPONSE_BUILDER, RESPONSE_STATUS), (notAvailable, arguments) -> {
        final Element object = new Element(HTTP_RESPONSE, new HttpResponse());
        arguments.get(0).getPossibleValues().stream()
                .map(status -> ((Response.Status) status).getStatusCode()).forEach(s -> addStatus(object, s));
        return object;
    }),

    RESPONSE_STATUS_INT(MethodIdentifier.ofStatic(RESPONSE, "status", RESPONSE_BUILDER, "int"), (notAvailable, arguments) -> {
        final Element object = new Element(HTTP_RESPONSE, new HttpResponse());
        arguments.get(0).getPossibleValues().stream()
                .map(status -> (int) status).forEach(s -> addStatus(object, s));
        return object;
    }),

    RESPONSE_OK(MethodIdentifier.ofStatic(RESPONSE, "ok", RESPONSE_BUILDER), (notAvailable, arguments) -> {
        final Element object = new Element(HTTP_RESPONSE, new HttpResponse());
        return addStatus(object, Response.Status.OK.getStatusCode());
    }),

    RESPONSE_OK_ENTITY(MethodIdentifier.ofStatic(RESPONSE, "ok", RESPONSE_BUILDER, OBJECT), (notAvailable, arguments) -> {
        final Element object = new Element(HTTP_RESPONSE, new HttpResponse());
        addStatus(object, Response.Status.OK.getStatusCode());
        return addEntity(object, arguments.get(0));
    }),

    RESPONSE_OK_VARIANT(MethodIdentifier.ofStatic(RESPONSE, "ok", RESPONSE_BUILDER, OBJECT, VARIANT), (notAvailable, arguments) -> {
        final Element object = new Element(HTTP_RESPONSE, new HttpResponse());
        addStatus(object, Response.Status.OK.getStatusCode());
        addEntity(object, arguments.get(0));
        addHeader(object, HttpHeaders.CONTENT_LANGUAGE);
        return addHeader(object, HttpHeaders.CONTENT_ENCODING);
    }),

    RESPONSE_OK_MEDIATYPE(MethodIdentifier.ofStatic(RESPONSE, "ok", RESPONSE_BUILDER, OBJECT, MediaType.class.getName()), (notAvailable, arguments) -> {
        final Element object = new Element(HTTP_RESPONSE, new HttpResponse());
        addStatus(object, Response.Status.OK.getStatusCode());
        arguments.get(1).getPossibleValues().stream().map(m -> (MediaType) m)
                .map(m -> m.getType() + '/' + m.getSubtype()).forEach(t -> addContentType(object, t));
        return addEntity(object, arguments.get(0));
    }),

    RESPONSE_OK_MEDIATYPE_STRING(MethodIdentifier.ofStatic(RESPONSE, "ok", RESPONSE_BUILDER, OBJECT, STRING), (notAvailable, arguments) -> {
        final Element object = new Element(HTTP_RESPONSE, new HttpResponse());
        addStatus(object, Response.Status.OK.getStatusCode());
        arguments.get(1).getPossibleValues().stream()
                .map(t -> (String) t).forEach(t -> addContentType(object, t));
        return addEntity(object, arguments.get(0));
    }),

    RESPONSE_ACCEPTED(MethodIdentifier.ofStatic(RESPONSE, "accepted", RESPONSE_BUILDER), (notAvailable, arguments) -> {
        final Element object = new Element(HTTP_RESPONSE, new HttpResponse());
        return addStatus(object, Response.Status.ACCEPTED.getStatusCode());
    }),

    RESPONSE_ACCEPTED_ENTITY(MethodIdentifier.ofStatic(RESPONSE, "accepted", RESPONSE_BUILDER, OBJECT), (notAvailable, arguments) -> {
        final Element object = new Element(HTTP_RESPONSE, new HttpResponse());
        addStatus(object, Response.Status.ACCEPTED.getStatusCode());
        return addEntity(object, arguments.get(0));
    }),

    RESPONSE_CREATED(MethodIdentifier.ofStatic(RESPONSE, "created", RESPONSE_BUILDER, URI), (notAvailable, arguments) -> {
        final Element object = new Element(HTTP_RESPONSE, new HttpResponse());
        addStatus(object, Response.Status.CREATED.getStatusCode());
        return addHeader(object, HttpHeaders.LOCATION);
    }),

    RESPONSE_NO_CONTENT(MethodIdentifier.ofStatic(RESPONSE, "noContent", RESPONSE_BUILDER), (notAvailable, arguments) -> {
        final Element object = new Element(HTTP_RESPONSE, new HttpResponse());
        return addStatus(object, Response.Status.NO_CONTENT.getStatusCode());
    }),

    RESPONSE_NOT_ACCEPTABLE(MethodIdentifier.ofStatic(RESPONSE, "notAcceptable", RESPONSE_BUILDER, "java.util.List"), (notAvailable, arguments) -> {
        final Element object = new Element(HTTP_RESPONSE, new HttpResponse());
        addStatus(object, Response.Status.NOT_ACCEPTABLE.getStatusCode());
        return addHeader(object, HttpHeaders.VARY);
    }),

    RESPONSE_NOT_MODIFIED(MethodIdentifier.ofStatic(RESPONSE, "notModified", RESPONSE_BUILDER), (notAvailable, arguments) -> {
        final Element object = new Element(HTTP_RESPONSE, new HttpResponse());
        return addStatus(object, Response.Status.NOT_MODIFIED.getStatusCode());
    }),

    RESPONSE_NOT_MODIFIED_ENTITYTAG(MethodIdentifier.ofStatic(RESPONSE, "notModified", RESPONSE_BUILDER, EntityTag.class.getName()), (notAvailable, arguments) -> {
        final Element object = new Element(HTTP_RESPONSE, new HttpResponse());
        addStatus(object, Response.Status.NOT_MODIFIED.getStatusCode());
        return addHeader(object, HttpHeaders.ETAG);
    }),

    RESPONSE_NOT_MODIFIED_ENTITYTAG_STRING(MethodIdentifier.ofStatic(RESPONSE, "notModified", RESPONSE_BUILDER, STRING), (notAvailable, arguments) -> {
        final Element object = new Element(HTTP_RESPONSE, new HttpResponse());
        addStatus(object, Response.Status.NOT_MODIFIED.getStatusCode());
        return addHeader(object, HttpHeaders.ETAG);
    }),

    RESPONSE_SEE_OTHER(MethodIdentifier.ofStatic(RESPONSE, "seeOther", RESPONSE_BUILDER, URI), (notAvailable, arguments) -> {
        final Element object = new Element(HTTP_RESPONSE, new HttpResponse());
        addStatus(object, Response.Status.SEE_OTHER.getStatusCode());
        return addHeader(object, HttpHeaders.LOCATION);
    }),

    RESPONSE_TEMPORARY_REDIRECT(MethodIdentifier.ofStatic(RESPONSE, "temporaryRedirect", RESPONSE_BUILDER, URI), (notAvailable, arguments) -> {
        final Element object = new Element(HTTP_RESPONSE, new HttpResponse());
        addStatus(object, Response.Status.TEMPORARY_REDIRECT.getStatusCode());
        return addHeader(object, HttpHeaders.LOCATION);
    }),

    RESPONSE_SERVER_ERROR(MethodIdentifier.ofStatic(RESPONSE, "serverError", RESPONSE_BUILDER), (notAvailable, arguments) -> {
        final Element object = new Element(HTTP_RESPONSE, new HttpResponse());
        return addStatus(object, Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    }),

    // other methods --------------------------

    RESOURCE_CONTEXT_INIT(MethodIdentifier.ofNonStatic(RESOURCE_CONTEXT, "getResource", OBJECT, "java.lang.Class"),
            (object, arguments) -> new Element(arguments.get(0).getPossibleValues().stream()
                    .map(c -> (String) c).findFirst().orElse(OBJECT))
    ),

    RESOURCE_CONTEXT_GET(MethodIdentifier.ofNonStatic(RESOURCE_CONTEXT, "initResource", OBJECT, OBJECT),
            (object, arguments) -> new Element(arguments.get(0).getType())),

    INTEGER_VALUE_OF(MethodIdentifier.ofStatic(INTEGER, "valueOf", INTEGER, "int"),
            (object, arguments) -> new Element(int.class.getCanonicalName(), arguments.get(0).getPossibleValues().toArray()));

    private final MethodIdentifier identifier;

    private final BiFunction<Element, List<Element>, Element> function;

    KnownResponseResultMethod(final MethodIdentifier identifier,
                              final BiFunction<Element, List<Element>, Element> function) {
        this.identifier = identifier;
        this.function = function;
    }

    @Override
    public Element invoke(final Element object, final List<Element> arguments) {
        if (arguments.size() != identifier.getParameterTypes().length)
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
                    r.getEntityTypes().add(argument.getType());
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

    /**
     * Contains known JAX-RS names.
     *
     * @author Sebastian Daschner
     */
    static class KnownNames {

        static final String HTTP_RESPONSE = HttpResponse.class.getName();
        static final String RESPONSE_BUILDER = Response.ResponseBuilder.class.getName();
        static final String RESPONSE = Response.class.getName();
        static final String RESPONSE_STATUS = Response.Status.class.getName();
        static final String RESOURCE_CONTEXT = ResourceContext.class.getName();
        static final String OBJECT = Object.class.getName();
        static final String STRING = String.class.getName();
        static final String INTEGER = Integer.class.getName();
        static final String URI = java.net.URI.class.getName();
        static final String DATE = Date.class.getName();
        static final String VARIANT = Variant.class.getName();

    }

}