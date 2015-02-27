package com.sebastian_daschner.jaxrs_analyzer.analysis.bytecode.reduction.testclasses;

import javax.ws.rs.core.Response;

public class TestClass5 {

    public Response method() {
        int status = 200;
        if ((status = 300) > 0) {
            status = 100;
        }
        return Response.status(status).build();
    }

    public Response expected1() {
        int status = 200;
        if ((status = 300) > 0) {
            status = 100;
        }
        return Response.status(status).build();
    }

}
