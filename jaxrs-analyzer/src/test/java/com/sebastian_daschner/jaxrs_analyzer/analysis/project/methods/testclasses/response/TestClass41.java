package com.sebastian_daschner.jaxrs_analyzer.analysis.project.methods.testclasses.response;

import com.sebastian_daschner.jaxrs_analyzer.model.elements.HttpResponse;

import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.Set;
import java.util.function.BiFunction;

public class TestClass41 {

    public Response method() {
        final Response.Status status = Response.Status.OK;
        BiFunction<Response.Status, Integer, BiFunction<String, Double, Response>> function = (sta, i) -> (str, d) -> Response.status(status).header(str, "Test").build();
        return function.apply(Response.Status.NOT_FOUND, 1).apply("X-Header", 1d);
    }

    public static Set<HttpResponse> getResult() {
        final HttpResponse result = new HttpResponse();

        result.getStatuses().add(200);
        result.getHeaders().add("X-Header");

        return Collections.singleton(result);
    }

}
