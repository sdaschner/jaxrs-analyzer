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

import com.sebastian_daschner.jaxrs_analyzer.model.Types;
import com.sebastian_daschner.jaxrs_analyzer.model.elements.HttpResponse;

import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

public class TestClass4 {

    @javax.ws.rs.GET
    public Response method() {
        Response.Status status = Response.Status.OK;

        if ("".equals(this.getClass().getName())) {
            status = Response.Status.ACCEPTED;
        }

        return responseBuilder(status).entity("Test").build();
    }

    private Response.ResponseBuilder responseBuilder(final Response.Status status) {
        return Response.status(status).header("X-Test", "Test");
    }

    public static Set<HttpResponse> getResult() {
        final HttpResponse result = new HttpResponse();

        result.getEntityTypes().add(Types.STRING);
        result.getStatuses().addAll(Arrays.asList(200, 202));
        result.getHeaders().add("X-Test");

        return Collections.singleton(result);
    }

}
