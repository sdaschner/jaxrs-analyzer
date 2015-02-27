package com.sebastian_daschner.jaxrs_analyzer.analysis.project.methods.testclasses.response;

import com.sebastian_daschner.jaxrs_analyzer.model.elements.HttpResponse;

import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Sebastian Daschner
 */

public class TestClass15 {

    public Response method(final String id) {
        if ("".equals("")) {
            return createResponse(Response.Status.NOT_FOUND);
        }
        return createResponse(Response.Status.OK);
    }

    private Response createResponse(final Response.Status status) {
        return Response.status(status).header("X-Header", "Hello World").build();
    }

    public static Set<HttpResponse> getResult() {
        final HttpResponse firstResult = new HttpResponse();
        final HttpResponse secondResult = new HttpResponse();

        firstResult.getStatuses().add(200);
        firstResult.getHeaders().add("X-Header");

        secondResult.getStatuses().add(404);
        secondResult.getHeaders().add("X-Header");

        return new HashSet<>(Arrays.asList(firstResult, secondResult));
    }

}
