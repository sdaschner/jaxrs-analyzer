package com.sebastian_daschner.jaxrs_test;

import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;

@Stateless
@Path("complex")
public class ComplexResources extends AbstractResources implements Resources {

    @Context
    ResourceContext rc;

    @Override
    public Response getInfo(final String info) {
        return Response.ok().header("X-Info", info + " is complex").build();
    }

    @Override
    public String getStatus() {
        return "ok";
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<String> getStrings() {
        final ArrayList<String> strings = new ArrayList<>();
        strings.add("hi");
        strings.add("hello");
        return strings;
    }

    @Override
    public String getString() {
        return "hello";
    }

    @Path("sub")
    public SomeSubResource subResources() {
        return createSomeSubResource();
    }

    private SomeSubResource createSomeSubResource() {
        return new SubResources("complex");
    }

    @Path("anotherSub")
    public SomeSubResource anotherSubResource() {
        return rc.initResource(new SubResources("complex"));
    }

    @Path("anotherSubres")
    public SomeSubResource anotherSubresResource() {
        // just for testing, this would fail due to missing default constructor
        return rc.getResource(SubResources.class);
    }

}
