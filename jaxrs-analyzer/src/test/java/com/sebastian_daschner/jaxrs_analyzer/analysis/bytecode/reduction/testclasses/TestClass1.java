package com.sebastian_daschner.jaxrs_analyzer.analysis.bytecode.reduction.testclasses;

import javax.ws.rs.core.Response;

public class TestClass1 {

    public Response method() {
        System.out.println("Hello World");
        return Response.status(Response.Status.ACCEPTED).build();
    }

    public Response expected1() {
        return Response.status(Response.Status.ACCEPTED).build();
    }

}
