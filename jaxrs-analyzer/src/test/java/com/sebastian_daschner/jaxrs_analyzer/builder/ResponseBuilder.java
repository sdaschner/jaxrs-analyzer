package com.sebastian_daschner.jaxrs_analyzer.builder;

import com.sebastian_daschner.jaxrs_analyzer.model.rest.Response;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeRepresentation;

import java.util.stream.Stream;

/**
 * @author Sebastian Daschner
 */
public class ResponseBuilder {

    private final Response response;

    private ResponseBuilder(final Response response) {
        this.response = response;
    }

    public static ResponseBuilder newBuilder() {
        return new ResponseBuilder(new Response());
    }

    public static ResponseBuilder withResponseBody(final TypeRepresentation responseBody) {
        return new ResponseBuilder(new Response(responseBody));
    }

    public ResponseBuilder andHeaders(final String... headers) {
        Stream.of(headers).forEach(response.getHeaders()::add);
        return this;
    }

    public Response build() {
        return response;
    }

}
