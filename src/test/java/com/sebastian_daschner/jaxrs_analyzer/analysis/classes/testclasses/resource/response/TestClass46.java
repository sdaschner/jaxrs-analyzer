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

package com.sebastian_daschner.jaxrs_analyzer.analysis.classes.testclasses.resource.response;

import com.sebastian_daschner.jaxrs_analyzer.model.elements.HttpResponse;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class TestClass46 {

    private List<Object> tasks;

    @javax.ws.rs.GET
    public Response method() {
        List<User> users = new LinkedList<>();
        if ("".equals(""))
            throw new WebApplicationException("message");
        else if ("a".equals("a"))
            throw new WebApplicationException(Response.accepted().build());
        else if ("a".equals("a"))
            throw new WebApplicationException("message", Response.status(Response.Status.GATEWAY_TIMEOUT).build());
        else if ("a".equals("a"))
            throw new WebApplicationException(201);
        else if ("a".equals("a"))
            throw new WebApplicationException("message", 204);
        else if ("a".equals("a"))
            throw new WebApplicationException(Response.Status.CONFLICT);
        else if ("a".equals("a"))
            throw new WebApplicationException("message", Response.Status.FORBIDDEN);
        else if ("a".equals("a"))
            throw new WebApplicationException(new RuntimeException(), Response.status(Response.Status.BAD_REQUEST).build());
        else if ("a".equals("a"))
            throw new WebApplicationException("message", new RuntimeException(), Response.status(Response.Status.NOT_IMPLEMENTED).build());
        else if ("a".equals("a"))
            throw new WebApplicationException(new RuntimeException(), 205);
        else if ("a".equals("a"))
            throw new WebApplicationException("message", new RuntimeException(), 206);
        else if ("a".equals("a"))
            throw new WebApplicationException(new RuntimeException(), Response.Status.EXPECTATION_FAILED);
        else if ("a".equals("a"))
            throw new WebApplicationException("message", new RuntimeException(), Response.Status.FOUND);
        return Response.ok(users).build();
    }

    public static Set<HttpResponse> getResult() {
        final Set<HttpResponse> results = new HashSet<>();

        final HttpResponse firstResult = new HttpResponse();
        firstResult.getStatuses().add(200);
        firstResult.getEntityTypes().add("Ljava/util/List<Lcom/sebastian_daschner/jaxrs_analyzer/analysis/classes/testclasses/resource/response/TestClass46$User;>;");
        firstResult.getEntityTypes().add("Ljava/util/LinkedList;");
        results.add(firstResult);

        final int[] statuses = {500, 202, 504, 201, 204, 409, 403, 400, 501, 205, 206, 417, 302};
        for (final int status : statuses) {
            final HttpResponse response = new HttpResponse();
            response.getStatuses().add(status);
            results.add(response);
        }

        return results;
    }

    private static class User {
        private String name;
    }

}
