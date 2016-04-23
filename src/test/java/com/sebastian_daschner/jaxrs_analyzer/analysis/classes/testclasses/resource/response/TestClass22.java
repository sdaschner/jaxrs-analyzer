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

import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.*;

public class TestClass22 {

    // test all available non-static Response methods
    @javax.ws.rs.GET public Response method() {
        Response.ResponseBuilder responseBuilder = Response.ok();
        responseBuilder.header("X-Test", "Hello");
        responseBuilder.cacheControl(CacheControl.valueOf(""));
        responseBuilder.contentLocation(URI.create(""));
        responseBuilder.cookie();
        responseBuilder.entity(12d);
        responseBuilder.expires(new Date());
        responseBuilder.language(Locale.ENGLISH);
        responseBuilder.encoding("UTF-8");
        responseBuilder.lastModified(new Date());
        responseBuilder.link(URI.create(""), "rel");
        responseBuilder.location(URI.create(""));
        responseBuilder.status(433);
        responseBuilder.tag(new EntityTag(""));
        responseBuilder.type(MediaType.APPLICATION_JSON_TYPE);
        responseBuilder.variants(new LinkedList<>());

        return responseBuilder.build();
    }

    public static Set<HttpResponse> getResult() {
        final HttpResponse result = new HttpResponse();

        result.getStatuses().addAll(Arrays.asList(200, 433));
        result.getEntityTypes().add(Types.DOUBLE);
        result.getHeaders().addAll(Arrays.asList("X-Test", "Cache-Control", "Set-Cookie", "Expires", "Content-Language", "Content-Encoding",
                "Last-Modified", "Link", "Location", "ETag", "Vary", "Content-Location"));
        result.getContentTypes().add("application/json");

        return Collections.singleton(result);
    }

}
