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

package com.sebastian_daschner.jaxrs_analyzer.analysis.project.methods.testclasses.resource.json;

import com.sebastian_daschner.jaxrs_analyzer.model.Types;
import com.sebastian_daschner.jaxrs_analyzer.model.elements.Element;
import com.sebastian_daschner.jaxrs_analyzer.model.elements.HttpResponse;
import com.sebastian_daschner.jaxrs_analyzer.model.elements.JsonObject;

import javax.json.Json;
import javax.json.JsonStructure;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TestClass8 {

    public JsonStructure method() {
        if ("".equals(""))
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);

        return Json.createObjectBuilder().add("key", "value").build();
    }

    public static Set<HttpResponse> getResult() {

        final JsonObject jsonObject = new JsonObject();
        jsonObject.getStructure().put("key", new Element(Types.STRING, "value"));


        final HttpResponse httpResponse = new HttpResponse();
        httpResponse.getEntityTypes().add(JsonStructure.class.getName());
        httpResponse.getInlineEntities().add(jsonObject);

        final HttpResponse errorResponse = new HttpResponse();
        errorResponse.getStatuses().add(500);

        return new HashSet<>(Arrays.asList(httpResponse, errorResponse));
    }

}
