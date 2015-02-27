package com.sebastian_daschner.jaxrs_analyzer.analysis.project.methods.testclasses.response;

import com.sebastian_daschner.jaxrs_analyzer.model.elements.HttpResponse;

import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.Set;
import java.util.function.Supplier;

public class TestClass25 {

    public Response method() {
        final Supplier<Response> responseSupplier = () -> {
            Response.ResponseBuilder builder = Response.ok();
            builder.header("X-Test", "Hello World");
            return builder.build();
        };
        return responseSupplier.get();
    }

    public static Set<HttpResponse> getResult() {
        final HttpResponse result = new HttpResponse();

        result.getStatuses().add(200);
        result.getHeaders().add("X-Test");

        return Collections.singleton(result);
    }

}
