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
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.HeaderParam;
import java.util.ArrayList;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;


/**
 * These sources are solely used for test purposes and not meant for deployment.
 */
@Stateless
@Path("complex")
public class ComplexResources extends AbstractResources implements Resources {

    @Context
    ResourceContext rc;

    @Inject
    Manager<Integer> manager;

    @Override
    public Response getInfo(final String info) {
        return Response.ok().header("X-Info", manager.getInstance(String.class, info.length()) + " is complex").build();
    }

    @Override
    public String getStatus() {
        return "status";
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

    /**
     * Creates an authorization endpoint.
     *
     * @param token auth token
     */
    @GET
    @Path("auth")
    public Response get(@HeaderParam(AUTHORIZATION) String token) {
        return Response.ok("Authorized").build();
    }

}
