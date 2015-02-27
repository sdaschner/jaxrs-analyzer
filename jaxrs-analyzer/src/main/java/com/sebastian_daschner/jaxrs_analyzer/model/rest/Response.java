package com.sebastian_daschner.jaxrs_analyzer.model.rest;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a response containing meta information which is sent for a specific status code.
 *
 * @author Sebastian Daschner
 */
public class Response {

    private final Set<String> headers = new HashSet<>();
    private final TypeRepresentation responseBody;

    public Response() {
        this(null);
    }

    public Response(final TypeRepresentation responseBody) {
        this.responseBody = responseBody;
    }

    public Set<String> getHeaders() {
        return headers;
    }

    public TypeRepresentation getResponseBody() {
        return responseBody;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Response response = (Response) o;

        if (!headers.equals(response.headers)) return false;
        return !(responseBody != null ? !responseBody.equals(response.responseBody) : response.responseBody != null);
    }

    @Override
    public int hashCode() {
        int result = headers.hashCode();
        result = 31 * result + (responseBody != null ? responseBody.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Response{" +
                "headers=" + headers +
                ", responseBody=" + responseBody +
                '}';
    }

}
