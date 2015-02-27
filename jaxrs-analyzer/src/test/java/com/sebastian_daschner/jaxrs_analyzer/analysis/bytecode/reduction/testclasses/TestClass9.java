package com.sebastian_daschner.jaxrs_analyzer.analysis.bytecode.reduction.testclasses;

import javax.ws.rs.core.Response;
import java.util.function.Supplier;

public class TestClass9 {

    public Response method() {
        final Supplier<Response> responseSupplier = () -> {
            Response.ResponseBuilder builder = Response.ok();
            builder.header("X-Test", "Hello World");
            return builder.build();
        };
        return responseSupplier.get();
    }

    public Response expected1() {
        final Supplier<Response> responseSupplier = () -> {
            Response.ResponseBuilder builder = Response.ok();
            builder.header("X-Test", "Hello World");
            return builder.build();
        };
        return responseSupplier.get();
    }

}
