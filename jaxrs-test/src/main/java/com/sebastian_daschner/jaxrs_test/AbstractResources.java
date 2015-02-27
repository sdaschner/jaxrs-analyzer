package com.sebastian_daschner.jaxrs_test;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public abstract class AbstractResources {

    @GET
    @Path("{info}")
    public Response getInfo(@PathParam("info") final String info) {
        return Response.status(Response.Status.NOT_IMPLEMENTED).header("X-Info", info + " is not implemented").build();
    }

    @GET
    @Path("string")
    @Produces(MediaType.APPLICATION_JSON)
    public Object getString() {
        return new Object();
    }

    @PUT
    public Response putComplex() {
        return Response.status(Response.Status.NO_CONTENT).build();
    }

}
