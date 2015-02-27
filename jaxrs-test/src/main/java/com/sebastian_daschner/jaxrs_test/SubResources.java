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

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

/**
 * These sources are solely used for test purposes and not meant for deployment.
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
