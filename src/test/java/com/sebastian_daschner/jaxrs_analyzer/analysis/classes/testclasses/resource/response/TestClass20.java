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

import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TestClass20 {

    // continue testing all available static Response methods
    @javax.ws.rs.GET public Response method() {
        Response.ResponseBuilder responseBuilder = Response.accepted("Hello");
        responseBuilder = Response.notModified(new EntityTag(""));
        responseBuilder = Response.ok(1, MediaType.APPLICATION_XML);

        return responseBuilder.build();
    }

    public static Set<HttpResponse> getResult() {
        final HttpResponse firstResult = new HttpResponse();
        final HttpResponse secondResult = new HttpResponse();
        final HttpResponse thirdResult = new HttpResponse();

        firstResult.getStatuses().add(202);
        firstResult.getEntityTypes().add(Types.STRING);

        secondResult.getStatuses().add(304);
        secondResult.getHeaders().add("ETag");

        thirdResult.getStatuses().add(200);
        thirdResult.getContentTypes().add("application/xml");
        thirdResult.getEntityTypes().add(Types.INTEGER);

        return new HashSet<>(Arrays.asList(firstResult, secondResult, thirdResult));
    }

}
