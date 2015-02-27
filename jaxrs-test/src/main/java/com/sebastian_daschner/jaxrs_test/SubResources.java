package com.sebastian_daschner.jaxrs_test;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

/**
 * @author Sebastian Daschner
 */
@Path("subsub")
public class SubResources implements SomeSubResource {

    private final String name;

    public SubResources(final String name) {
        this.name = name;
    }

    @GET
    @Path("{name}")
    public String getSub(@PathParam("name") final String name) {
        return this.name + name;
    }

    @POST
    public Response postSub(final String entity) {
        System.out.println("posted new: " + entity);
        return Response.accepted().header("X-Info", "Added " + entity).build();
    }

}
