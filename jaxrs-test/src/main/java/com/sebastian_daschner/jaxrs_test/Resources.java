package com.sebastian_daschner.jaxrs_test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

public interface Resources {

    @GET
    @Path("status")
    String getStatus();

}
