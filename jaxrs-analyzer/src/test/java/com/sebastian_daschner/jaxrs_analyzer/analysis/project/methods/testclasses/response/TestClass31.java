package com.sebastian_daschner.jaxrs_analyzer.analysis.project.methods.testclasses.response;

import com.sebastian_daschner.jaxrs_analyzer.model.elements.HttpResponse;

import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class TestClass31 {

    public Response method(final String id) {
        Function<Response.Status, Response> responseSupplier;
        responseSupplier = this::responseWithHeader;
        if ("".equals(""))
            responseSupplier = this::response;
        else if ("foo".equals("foo"))
            responseSupplier = this::anotherResponse;

        return responseSupplier.apply(Response.Status.OK);
    }

    private Response response(final Response.Status status) {
        return Response.status(status).location(URI.create("")).build();
    }

    private Response responseWithHeader(final Response.Status status) {
        return Response.status(status).header("X-Test", "Test").build();
    }

    private Response anotherResponse(final Response.Status status) {
        Function<Response.Status, Response> responseSupplier = s -> Response.status(status).tag("").build();
        return responseSupplier.apply(status);
    }

    public static Set<HttpResponse> getResult() {
        final HttpResponse firstResult = new HttpResponse();
        final HttpResponse secondResult= new HttpResponse();
        final HttpResponse thirdResult = new HttpResponse();

        firstResult.getStatuses().add(200);
        firstResult.getHeaders().add("Location");
        secondResult.getStatuses().add(200);
        secondResult.getHeaders().add("X-Test");
        thirdResult.getStatuses().add(200);
        thirdResult.getHeaders().add("ETag");

        return new HashSet<>(Arrays.asList(firstResult, secondResult, thirdResult));
    }

}
