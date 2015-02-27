package com.sebastian_daschner.jaxrs_analyzer.analysis.project.methods.testclasses.response;

import com.sebastian_daschner.jaxrs_analyzer.model.elements.HttpResponse;

import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

public class TestClass4 {

    public Response method() {
        Response.Status status = Response.Status.OK;

        if ("".equals(this.getClass().getName())) {
            status = Response.Status.ACCEPTED;
        }

        return responseBuilder(status).entity("Test").build();
    }

    private Response.ResponseBuilder responseBuilder(final Response.Status status) {
        return Response.status(status).header("X-Test", "Test");
    }

    public static Set<HttpResponse> getResult() {
        final HttpResponse result = new HttpResponse();

        result.getEntityTypes().add("java.lang.String");
        result.getStatuses().addAll(Arrays.asList(200, 202));
        result.getHeaders().add("X-Test");

        return Collections.singleton(result);
    }

}
