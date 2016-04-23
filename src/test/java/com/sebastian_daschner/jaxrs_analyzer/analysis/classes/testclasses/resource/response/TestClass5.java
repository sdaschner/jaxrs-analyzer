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

import javax.persistence.EntityManager;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TestClass5 {

    private EntityManager entityManager;

    @javax.ws.rs.GET public Response method() {

        final TestClass5 testClass = entityManager.find(TestClass5.class, 1);

        if (testClass.method().hasEntity())
            return buildWithStatus(Response.Status.ACCEPTED);

        return buildWithStatus(Response.Status.CONFLICT);
    }

    private static Response buildWithStatus(final Response.Status status) {
        return Response.status(status).header("X-Message", "Secret message").build();
    }

    public static Set<HttpResponse> getResult() {
        final HttpResponse firstResult = new HttpResponse();
        final HttpResponse secondResult = new HttpResponse();

        firstResult.getStatuses().add(202);
        firstResult.getHeaders().add("X-Message");


        secondResult.getStatuses().add(409);
        secondResult.getHeaders().add("X-Message");

        return new HashSet<>(Arrays.asList(firstResult, secondResult));
    }

}
