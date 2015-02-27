package com.sebastian_daschner.jaxrs_analyzer.analysis.project.methods.testclasses.response;

import com.sebastian_daschner.jaxrs_analyzer.model.elements.HttpResponse;

import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;

public class TestClass32 {

    public Response method() {
        Function<Response.Status, Function<String, Response>> function = sta -> str -> Response.status(sta).header(str, "Hello").build();
        return function.apply(Response.Status.OK).apply("X-Header");
    }

    public static Set<HttpResponse> getResult() {
        final HttpResponse result = new HttpResponse();

        result.getStatuses().add(200);
        result.getHeaders().add("X-Header");

        return Collections.singleton(result);
    }

}
