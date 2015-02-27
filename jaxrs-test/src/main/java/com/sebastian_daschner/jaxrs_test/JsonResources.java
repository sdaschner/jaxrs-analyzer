package com.sebastian_daschner.jaxrs_test;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

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
        return Response.ok(Json.createArrayBuilder().add("duke").add(42).build()).build();
    }

    @Path("info")
    @GET
    public Response getInfo() {
        return Response.ok(Json.createObjectBuilder().add("key", "value").add("duke", "42").add("hello", "world")).build();
    }

}
