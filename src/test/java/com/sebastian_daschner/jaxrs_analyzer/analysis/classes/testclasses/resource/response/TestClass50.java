package com.sebastian_daschner.jaxrs_analyzer.analysis.classes.testclasses.resource.response;

import com.sebastian_daschner.jaxrs_analyzer.model.elements.HttpResponse;

import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.Set;

public class TestClass50 {

    private Converter converter;

    @javax.ws.rs.GET public Response method(@PathParam("input") String input) {
        return Response.status(Response.Status.OK).header("X-Info", "Converted: " + converter.convert(input)).build();
    }

    public static Set<HttpResponse> getResult() {
        final HttpResponse response = new HttpResponse();
        response.getStatuses().add(200);
        response.getHeaders().add("X-Info");

        return Collections.singleton(response);
    }

    private interface Converter {
        String convert(String string);
    }

}
