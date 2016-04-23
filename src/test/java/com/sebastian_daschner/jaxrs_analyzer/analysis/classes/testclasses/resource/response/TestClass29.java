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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class TestClass29 {

    @javax.ws.rs.GET public Response method(final String id) {
        Function<Response.Status, Response> responseSupplier = s -> Response.status(s).build();
        if ("".equals(""))
            return responseSupplier.apply(Response.Status.INTERNAL_SERVER_ERROR);
        return responseSupplier.apply(Response.Status.ACCEPTED);
    }

    public static Set<HttpResponse> getResult() {
        final HttpResponse firstResult = new HttpResponse();
        final HttpResponse secondResult = new HttpResponse();

        firstResult.getStatuses().add(202);
        secondResult.getStatuses().add(500);

        return new HashSet<>(Arrays.asList(firstResult, secondResult));
    }

}
