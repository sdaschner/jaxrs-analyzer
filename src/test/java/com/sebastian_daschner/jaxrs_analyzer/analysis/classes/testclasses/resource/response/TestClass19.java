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

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Variant;
import java.net.URI;
import java.util.*;

public class TestClass19 {

    // test all available static Response methods
    @javax.ws.rs.GET public Response method() {
        Response.ResponseBuilder responseBuilder = Response.accepted();
        responseBuilder = Response.created(URI.create(""));
        responseBuilder = Response.noContent();
        responseBuilder = Response.notAcceptable(new LinkedList<>());
        responseBuilder = Response.notModified();
        responseBuilder = Response.ok();
        responseBuilder = Response.ok(1L, new Variant(MediaType.TEXT_PLAIN_TYPE, Locale.ENGLISH, "UTF-8"));
        responseBuilder = Response.seeOther(URI.create(""));
        responseBuilder = Response.serverError();
        responseBuilder = Response.temporaryRedirect(URI.create(""));

        return responseBuilder.build();
    }

    public static Set<HttpResponse> getResult() {
        final HttpResponse firstResult = new HttpResponse();
        final HttpResponse secondResult = new HttpResponse();
        final HttpResponse thirdResult = new HttpResponse();
        final HttpResponse fourthResult = new HttpResponse();
        final HttpResponse fifthResult = new HttpResponse();
        final HttpResponse sixthResult = new HttpResponse();
        final HttpResponse seventhResult = new HttpResponse();
        final HttpResponse eighthResult = new HttpResponse();
        final HttpResponse ninthResult = new HttpResponse();
        final HttpResponse tenthResult = new HttpResponse();

        firstResult.getStatuses().add(202);
        secondResult.getStatuses().add(201);
        thirdResult.getStatuses().add(204);
        fourthResult.getStatuses().add(406);
        fifthResult.getStatuses().add(304);
        sixthResult.getStatuses().add(200);
        seventhResult.getStatuses().add(200);
        eighthResult.getStatuses().add(303);
        ninthResult.getStatuses().add(500);
        tenthResult.getStatuses().add(307);

        seventhResult.getEntityTypes().add(Types.LONG);

        secondResult.getHeaders().add("Location");
        fourthResult.getHeaders().add("Vary");
        eighthResult.getHeaders().add("Location");
        tenthResult.getHeaders().add("Location");
        seventhResult.getHeaders().addAll(Arrays.asList("Content-Language", "Content-Encoding"));

        return new HashSet<>(Arrays.asList(firstResult, secondResult, thirdResult, fourthResult, fifthResult, sixthResult,
                seventhResult, eighthResult, ninthResult, tenthResult));
    }

}
