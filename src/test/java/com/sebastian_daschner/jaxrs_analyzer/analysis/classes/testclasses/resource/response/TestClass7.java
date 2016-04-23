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
import java.util.Collections;
import java.util.Set;
import java.util.logging.Logger;

public class TestClass7 {

    private InnerTestClass6 innerTestClass;

    @javax.ws.rs.GET public Response method(final String id) {
        try {
            innerTestClass.method(id);
            return Response.noContent().build();
        } finally {
            Logger.getLogger("").info("deleted");
        }
    }

    public static Set<HttpResponse> getResult() {
        final HttpResponse result = new HttpResponse();

        result.getStatuses().add(204);

        return Collections.singleton(result);
    }

    private class InnerTestClass6 {

        private EntityManager entityManager;

        public void method(final String id) {
            final Object managedEntity = entityManager.find(Object.class, id);
            entityManager.remove(managedEntity);
            if ("".equals(id))
                throw new RuntimeException("");
        }

    }

}
