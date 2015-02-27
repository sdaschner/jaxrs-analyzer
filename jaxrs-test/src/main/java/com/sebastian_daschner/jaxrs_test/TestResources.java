package com.sebastian_daschner.jaxrs_test;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;
import java.util.logging.Logger;

@Path("/test")
@Stateless
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TestResources {

    @Inject
    private TestStore testStore;

    @GET
    @Path("test")
    @Produces(MediaType.TEXT_HTML)
    public Response test() {
        return Response.ok("hi", MediaType.TEXT_PLAIN_TYPE).build();
    }

    @GET
    public List<Model> getModels() {
        return this.testStore.getModels();
    }

    @POST
    public Response simplePost(String string) {
        final Model managedModel = this.testStore.getModel(string);

        final URI uri = URI.create("/test/" + managedModel.getId());

        return Response.created(uri).build();
    }

    @PUT
    public Response put(final Model model) {
        this.testStore.addModel(model);

        return Response.accepted().build();
    }

    @GET
    @Path("{id}")
    public Model getModel(@PathParam("id") final String id) {
        synchronized (this) {
            return this.testStore.getModel(id);
        }
    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") final String id) {
        try {
            this.testStore.delete(id);
            return Response.noContent().build();
        } catch (EntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).header("X-Message", "The entity with identifier " + id + " was not found.").build();
        }
    }

    @DELETE
    @Path("{id}/test")
    public Response anotherDelete(@PathParam("id") final String id) {
        try {
            this.testStore.delete(id);
            return Response.noContent().build();
        } finally {
            Logger.getLogger("").info("deleted");
        }
    }

}
