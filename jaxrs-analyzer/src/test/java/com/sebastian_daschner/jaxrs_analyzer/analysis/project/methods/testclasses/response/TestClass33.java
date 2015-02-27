package com.sebastian_daschner.jaxrs_analyzer.analysis.project.methods.testclasses.response;

import com.sebastian_daschner.jaxrs_analyzer.model.elements.HttpResponse;

import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

public class TestClass33 {

    public Response method(final String id) {
        BiFunction<Response.Status, String, Response> responseSupplier = this::response;

        return responseSupplier.apply(Response.Status.OK, "X-Header");
    }

    private Response response(final Response.Status status, final String string) {
        Function<String, Function<Response.Status, Response>> function = str -> sta -> Response.status(sta).header(str, "Hello").build();
        return function.apply(string).apply(status);
    }

    public static Set<HttpResponse> getResult() {
        final HttpResponse result = new HttpResponse();

        result.getStatuses().add(200);
        result.getHeaders().add("X-Header");

        return Collections.singleton(result);
    }

}
