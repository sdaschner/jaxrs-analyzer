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

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * These sources are solely used for test purposes and not meant for deployment.
 */
@Path("json_tests")
public class JsonResources {

    @GET
    public JsonObject getJson() {
        return Json.createObjectBuilder().add("key", "value").add("duke", 42).build();
    }

    @POST
    public Response post() {
        if ("".equals(""))
            return Response.accepted(Json.createObjectBuilder().add("key", "value").build()).build();
        if ("a".equals("b"))
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        return Response.ok(Json.createArrayBuilder().add("duke").add(42).build()).build();
    }

    @Path("info")
    @GET
    public Response getInfo() {
        return Response.ok(Json.createObjectBuilder().add("key", "value").add("duke", "42").add("hello", "world")).build();
    }

}
