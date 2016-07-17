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
import com.sebastian_daschner.jaxrs_analyzer.model.rest.HttpMethod;
import com.sebastian_daschner.jaxrs_analyzer.model.results.ClassResult;
import com.sebastian_daschner.jaxrs_analyzer.model.results.MethodResult;
import com.sebastian_daschner.jaxrs_analyzer.model.Types;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("test")
@Produces(MediaType.APPLICATION_JSON)
public class TestClass2 {

    @GET
    @Produces(MediaType.TEXT_HTML)
    public int method() {
        int status = 200;
        return status;
    }

    @POST
    public Response post(final String string) {
        return Response.accepted().build();
    }

    public static ClassResult getResult() {

        final MethodResult firstMethod = MethodResultBuilder.withResponses(HttpResponseBuilder.newBuilder().andEntityTypes(Types.PRIMITIVE_INT).build())
                .andResponseMediaTypes("text/html").andMethodName("method").andMethod(HttpMethod.GET).build();
        final MethodResult secondMethod = MethodResultBuilder.withResponses(HttpResponseBuilder.withStatues(202).build())
                .andMethodName("post").andMethod(HttpMethod.POST).andRequestBodyType(Types.STRING).build();
        return ClassResultBuilder.withResourcePath("test").andResponseMediaTypes("application/json").andMethods(firstMethod, secondMethod).build();
    }

}
