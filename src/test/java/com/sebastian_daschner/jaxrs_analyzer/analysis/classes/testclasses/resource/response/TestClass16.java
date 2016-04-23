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

import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.Set;

/**
 * @author Sebastian Daschner
 */

public class TestClass16 {

    public TestClass16(final String test) {
        System.out.println(test);
    }

    @javax.ws.rs.GET public Response method(final String id) {
        final Response.Status status = Response.Status.OK;
        new TestClass16(status.getReasonPhrase());
        return Response.status(status).header("X-Header", "Hello World").build();
    }

    public static Set<HttpResponse> getResult() {
        final HttpResponse result = new HttpResponse();

        result.getStatuses().add(200);
        result.getHeaders().add("X-Header");

        return Collections.singleton(result);
    }

}
