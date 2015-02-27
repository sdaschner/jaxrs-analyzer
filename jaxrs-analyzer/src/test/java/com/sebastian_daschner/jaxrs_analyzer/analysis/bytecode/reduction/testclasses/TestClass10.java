package com.sebastian_daschner.jaxrs_analyzer.analysis.bytecode.reduction.testclasses;

import javax.ws.rs.core.Response;
import java.util.function.Supplier;

public class TestClass10 {

    public Response method() {
        final Supplier<Response> responseSupplier = this::response;
        return responseSupplier.get();
    }

    private Response response() {
        final Response.ResponseBuilder builder = Response.ok();
        builder.header("X-Test", "Hello World");
        return builder.build();
    }

    public Response expected1() {
        final Supplier<Response> responseSupplier = this::response;
        return responseSupplier.get();
    }

}
