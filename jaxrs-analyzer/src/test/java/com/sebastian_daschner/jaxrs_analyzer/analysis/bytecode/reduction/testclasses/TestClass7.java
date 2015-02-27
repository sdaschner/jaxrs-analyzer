package com.sebastian_daschner.jaxrs_analyzer.analysis.bytecode.reduction.testclasses;

import javax.ws.rs.core.Response;

public class TestClass7 {

    public Response method() {
        Response.ResponseBuilder builder = Response.ok();
        if (otherMethod()) {
            builder.header("X-Test", "Hello World");
        }
        System.out.println("Test");
        return builder.build();
    }

    private boolean otherMethod() {
        return true;
    }

    public Response expected1() {
        Response.ResponseBuilder builder = Response.ok();
        builder.header("X-Test", "Hello World");
        return builder.build();
    }

}
