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

package com.sebastian_daschner.jaxrs_analyzer.analysis.project.classes.testclasses;

import com.sebastian_daschner.jaxrs_analyzer.builder.ClassResultBuilder;
import com.sebastian_daschner.jaxrs_analyzer.builder.HttpResponseBuilder;
import com.sebastian_daschner.jaxrs_analyzer.builder.MethodResultBuilder;
import com.sebastian_daschner.jaxrs_analyzer.model.Types;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.HttpMethod;
import com.sebastian_daschner.jaxrs_analyzer.model.results.ClassResult;
import com.sebastian_daschner.jaxrs_analyzer.model.results.MethodResult;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

// not meant to be correct JAX-RS code, just for test purposes
// #55 - support for @DefaultValue annotation
@Path("test")
public class TestClass14 {

    @DefaultValue("1")
    @QueryParam("id")
    private Integer id;

    @GET
    @Path("{info}")
    public Response getInfo(@DefaultValue("1") @QueryParam("value") final Integer value) {
        return Response.ok().build();
    }

    public static ClassResult getResult() {
        final MethodResult method = MethodResultBuilder
                .withResponses(HttpResponseBuilder.withStatues(200).build())
                .andPath("{info}")
                .andMethodName("getInfo")
                .andMethod(HttpMethod.GET)
                .andQueryParam("value", Types.INTEGER, "1")
                .build();

        return ClassResultBuilder
                .withResourcePath("test")
                .andMethods(method)
                .andQueryParam("id", Types.INTEGER, "1")
                .build();
    }

}
