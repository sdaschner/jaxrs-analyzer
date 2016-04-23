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
import com.sebastian_daschner.jaxrs_analyzer.builder.MethodResultBuilder;
import com.sebastian_daschner.jaxrs_analyzer.model.Types;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.HttpMethod;
import com.sebastian_daschner.jaxrs_analyzer.model.results.ClassResult;
import com.sebastian_daschner.jaxrs_analyzer.model.results.MethodResult;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("test")
public interface TestClass6 {

    @GET
    @Path("{info}")
    Model getInfo(final String info);

    static ClassResult getResult() {
        final MethodResult method = MethodResultBuilder.withResponses()//HttpResponseBuilder.newBuilder()
//                .andEntityTypes("com.sebastian_daschner.jaxrs_analyzer.analysis.project.classes.testclasses.TestClass6$Model").build())
                .andPath("{info}").andMethod(HttpMethod.GET).andRequestBodyType(Types.STRING).build();
        return ClassResultBuilder.withResourcePath("test").andMethods(method).build();
    }

    class Model {

    }

}
