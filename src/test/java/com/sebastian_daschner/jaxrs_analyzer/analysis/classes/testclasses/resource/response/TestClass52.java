package com.sebastian_daschner.jaxrs_analyzer.analysis.classes.testclasses.resource.response;

import com.sebastian_daschner.jaxrs_analyzer.model.elements.HttpResponse;

import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.Set;

public class TestClass52 {

    public static final TestClass52 TEST = new TestClass52();

    @javax.ws.rs.GET public Response method(@PathParam("input") String input) {
        return Response.status(TEST.getStatus()).build();
    }

    public static Set<HttpResponse> getResult() {
        final HttpResponse response = new HttpResponse();
        response.getStatuses().add(200);

        return Collections.singleton(response);
    }

    public int getStatus() {
        return 200;
    }

}
