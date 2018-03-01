/*
 * Copyright (C) 2015 Sebastian Daschner, sebastian-daschner.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sebastian_daschner.jaxrs_test;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.validation.constraints.Pattern;

/**
 * These sources are solely used for test purposes and not meant for deployment.
 */
@Path("/test")
@Stateless
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TestResources {

    @Inject
    private TestStore testStore;

    /**
     * Returns a test string with plain text.
     *
     * @return Ignore this comment
     */
    @GET
    @Path("test")
    @Produces(MediaType.TEXT_HTML)
    public Response test() {
        return Response.ok("hi", MediaType.TEXT_PLAIN_TYPE).build();
    }

    @GET
    public List<? extends Model> getModels() {
        return this.testStore.getModels();
    }

    public <T extends Comparable<? super T>> T foobar() {
        return null;
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

    /**
     * Deletes a test.
     *
     * @param foobar The foo query
     */
    @DELETE
    @Path("{foobar}")
    public void deleteTest(@PathParam("foobar") @Pattern(regexp = "^\\S.*") final String foobar) {
        Logger.getLogger("").info("deleted " + foobar);
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
    public Response delete(@PathParam("id") final Map<String, List<String>> id) {
        try {
            this.testStore.delete("id");
            return Response.noContent().build();
        } catch (EntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).header("X-Message", "The entity with identifier " + id + " was not found.").build();
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

    /**
     * Deletes another test.
     *
     * @param id    The ID
     * @param query The deletion query
     * @return
     */
    @DELETE
    @Path("{id}/test")
    public Response anotherDelete(@PathParam("id") final String id, @QueryParam("query") final int query) {
        try {
            this.testStore.delete(id);
            return Response.noContent().build();
        } finally {
            Logger.getLogger("").info("deleted");
        }
    }

}
