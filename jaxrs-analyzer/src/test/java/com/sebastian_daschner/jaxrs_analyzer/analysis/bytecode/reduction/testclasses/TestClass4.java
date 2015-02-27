package com.sebastian_daschner.jaxrs_analyzer.analysis.bytecode.reduction.testclasses;

import javax.ws.rs.core.Response;

public class TestClass4 {

    public Response method() {
        int status = 200;
        System.out.println(status);
        int anotherStatus = 100;
        status = anotherStatus = 300;
        return Response.status(status).entity(anotherStatus).build();
    }

    public Response expected1() {
        int status = 200;
        System.out.println(status);
        int anotherStatus = 100;
        status = anotherStatus = 300;
        return Response.status(status).entity(anotherStatus).build();
    }

}
