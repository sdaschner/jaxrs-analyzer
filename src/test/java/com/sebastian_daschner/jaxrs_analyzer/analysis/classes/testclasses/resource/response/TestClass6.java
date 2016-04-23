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
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TestClass6 {

    private InnerTestClass6 innerTestClass;

    @javax.ws.rs.GET public Response method(final String id) {
        try {
            innerTestClass.method(id);
            return Response.noContent().build();
        } catch (EntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).header("X-Message", "The entity with identifier " + id + " was not found.").build();
        }
    }

    public static Set<HttpResponse> getResult() {
        final HttpResponse firstResult = new HttpResponse();
        final HttpResponse secondResult = new HttpResponse();

        firstResult.getStatuses().add(404);
        firstResult.getHeaders().add("X-Message");

        secondResult.getStatuses().add(204);

        return new HashSet<>(Arrays.asList(firstResult, secondResult));
    }

    private class InnerTestClass6 {

        private EntityManager entityManager;

        @javax.ws.rs.GET public void method(final String id) {
            final Object managedEntity = entityManager.find(Object.class, id);
            entityManager.remove(managedEntity);
        }

    }

}
