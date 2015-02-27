package com.sebastian_daschner.jaxrs_analyzer.analysis.bytecode.reduction.testclasses;

import javax.ws.rs.core.Response;

public class TestClass2 {

    public Response method() {
        if ("".equals(new Object())) {
            return Response.status(Response.Status.OK).build();
        }
        System.out.println("Hello World");
        return Response.status(Response.Status.ACCEPTED).build();
    }

    public Response expected1() {
        return Response.status(Response.Status.OK).build();
    }

    public Response expected2() {
        return Response.status(Response.Status.ACCEPTED).build();
    }

}
