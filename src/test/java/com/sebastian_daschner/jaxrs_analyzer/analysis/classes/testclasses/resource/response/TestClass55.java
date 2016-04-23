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

public class TestClass55 {

    @javax.ws.rs.GET public Response method(final String id) {
        if ("".equals(""))
            return Response.status(status(Integer.valueOf(1))).build();
        return Response.status(status(1)).build();
    }

    public static Set<HttpResponse> getResult() {
        final HttpResponse firstResponse = new HttpResponse();
        firstResponse.getStatuses().add(200);

        final HttpResponse secondResponse = new HttpResponse();
        secondResponse.getStatuses().add(201);

        return new HashSet<>(Arrays.asList(firstResponse, secondResponse));
    }

    private int status(final int number) {
        return 200;
    }

    private int status(final Integer number) {
        return 201;
    }

}
