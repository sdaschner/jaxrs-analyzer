package com.sebastian_daschner.jaxrs_analyzer.analysis.bytecode.reduction.testclasses;

import javax.ws.rs.core.Response;

public class TestClass3 {

    public Response method() {
        Response.Status status = Response.Status.ACCEPTED;
        System.out.println("Hello World");
        if ("".equals("")) {
            status = Response.Status.OK;
        }
        System.out.println("World");
        return Response.status(status).build();
    }

    public Response expected1() {
        Response.Status status = Response.Status.ACCEPTED;
        status = Response.Status.OK;
        return Response.status(status).build();
    }

}
