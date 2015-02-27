package com.sebastian_daschner.jaxrs_analyzer.analysis.project.methods.testclasses.response;

import com.sebastian_daschner.jaxrs_analyzer.model.elements.HttpResponse;

import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class TestClass27 {

    public Response method(final String id) {
        Supplier<Response> responseSupplier = this::response;
        if ("".equals(""))
            responseSupplier = this::otherResponse;
        return responseSupplier.get();
    }

    private Response otherResponse() {
        return Response.serverError().build();
    }

    private Response response() {
        final Response.ResponseBuilder builder = Response.status(Response.Status.ACCEPTED);
        builder.header("X-Test", "Hello World");
        return builder.build();
    }

    public static Set<HttpResponse> getResult() {
        final HttpResponse firstResult = new HttpResponse();
        final HttpResponse secondResult = new HttpResponse();

        firstResult.getStatuses().add(202);
        firstResult.getHeaders().add("X-Test");
        secondResult.getStatuses().add(500);

        return new HashSet<>(Arrays.asList(firstResult, secondResult));
    }

}
