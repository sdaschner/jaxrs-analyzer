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

import javax.ws.rs.GET;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.Set;

// https://github.com/sdaschner/jaxrs-analyzer/issues/80
public class TestClass62 {

    @GET
    public Response method() {
        // scope will cause variable indexes 1 for i and contentType
        for (int i = 0; i < 0; i++) ;

        final String contentType = null;

        return Response
                .ok()
                .type(contentType)
                .build();
    }

    public static Set<HttpResponse> getResult() {
        final HttpResponse responseFound = new HttpResponse();
        responseFound.getStatuses().add(200);

        return Collections.singleton(responseFound);
    }

}
