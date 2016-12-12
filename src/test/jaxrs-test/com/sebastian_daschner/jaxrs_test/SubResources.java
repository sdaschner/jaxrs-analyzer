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

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

/**
 * These sources are solely used for test purposes and not meant for deployment.
 */
@Path("subsub")
public class SubResources implements SomeSubResource {

    private final String name;

    /**
     * The query param.
     */
    @QueryParam("query")
    private String query;

    public SubResources(final String name) {
        this.name = name;
    }

    /**
     * Gets a sub resource.
     *
     * @param name The name
     * @return
     */
    @GET
    @Path("{name}")
    public String getSub(@PathParam("name") final String name) {
        return this.name + name;
    }

    /**
     * Creates a sub resource.
     *
     * @param entity The entity
     * @return
     */
    @POST
    public Response postSub(final String entity) {
        System.out.println("posted new: " + entity + " q: " + query);
        return Response.accepted().header("X-Info", "Added " + entity).build();
    }

}
