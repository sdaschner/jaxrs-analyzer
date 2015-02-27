package com.sebastian_daschner.jaxrs_analyzer.analysis.project.methods.testclasses.response;

import com.sebastian_daschner.jaxrs_analyzer.model.elements.HttpResponse;

import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class TestClass30 {

    public Response method(final String id) {
        Function<Response.Status, Response> responseSupplier = this::response;
        if ("".equals(""))
            return responseSupplier.apply(Response.Status.INTERNAL_SERVER_ERROR);
        return responseSupplier.apply(Response.Status.ACCEPTED);
    }

    private Response response(final Response.Status status) {
        return Response.status(status).build();
    }

    public static Set<HttpResponse> getResult() {
        final HttpResponse firstResult = new HttpResponse();
        final HttpResponse secondResult = new HttpResponse();

        firstResult.getStatuses().add(202);
        secondResult.getStatuses().add(500);

        return new HashSet<>(Arrays.asList(firstResult, secondResult));
    }

}
