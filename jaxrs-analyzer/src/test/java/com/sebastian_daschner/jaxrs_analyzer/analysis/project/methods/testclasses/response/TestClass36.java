package com.sebastian_daschner.jaxrs_analyzer.analysis.project.methods.testclasses.response;

import com.sebastian_daschner.jaxrs_analyzer.model.elements.HttpResponse;

import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.Set;
import java.util.function.BiFunction;

public class TestClass36 {

    public Response method() {
        BiFunction<Response.Status, Integer, BiFunction<String, Double, Response>> function = (sta, i) -> response(sta);
        return function.apply(Response.Status.OK, 1).apply("X-Header", 1d);
    }

    private BiFunction<String, Double, Response> response(final Response.Status status) {
        return (str, d) -> Response.status(status).header(str, "Test").build();
    }

    public static Set<HttpResponse> getResult() {
        final HttpResponse result = new HttpResponse();

        result.getStatuses().add(200);
        result.getHeaders().add("X-Header");

        return Collections.singleton(result);
    }

}
