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

import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Variant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;

public class TestClass24 {

    // continue testing all available non-static Response methods
    @javax.ws.rs.GET public Response method() {
        Response.ResponseBuilder responseBuilder = Response.ok();
        responseBuilder.links(new Link[0]);
        responseBuilder.variant(new Variant(MediaType.TEXT_PLAIN_TYPE, Locale.ENGLISH, "UTF-8"));

        return responseBuilder.build();
    }

    public static Set<HttpResponse> getResult() {
        final HttpResponse result = new HttpResponse();

        result.getStatuses().addAll(Arrays.asList(200));
        result.getHeaders().addAll(Arrays.asList("Link", "Content-Language", "Content-Encoding"));

        return Collections.singleton(result);
    }

}
