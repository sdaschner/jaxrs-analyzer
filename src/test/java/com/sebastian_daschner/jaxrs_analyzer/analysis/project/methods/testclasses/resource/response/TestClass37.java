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

package com.sebastian_daschner.jaxrs_analyzer.analysis.project.methods.testclasses.resource.response;

import com.sebastian_daschner.jaxrs_analyzer.model.Types;
import com.sebastian_daschner.jaxrs_analyzer.model.elements.Element;
import com.sebastian_daschner.jaxrs_analyzer.model.elements.HttpResponse;
import com.sebastian_daschner.jaxrs_analyzer.model.elements.JsonArray;
import com.sebastian_daschner.jaxrs_analyzer.model.elements.JsonObject;

import javax.json.Json;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TestClass37 {

    public Response method() {
        if ("".equals(""))
            return Response.accepted(Json.createArrayBuilder().add(true).add("hello").build()).build();
        return Response.status(200).entity(Json.createObjectBuilder().add("key", "value").add("duke", 42).build()).build();
    }

    public static Set<HttpResponse> getResult() {
        final HttpResponse firstResult = new HttpResponse();
        final HttpResponse secondResult = new HttpResponse();

        firstResult.getStatuses().add(202);
        firstResult.getEntityTypes().add("javax.json.JsonArray");

        secondResult.getStatuses().add(200);
        secondResult.getEntityTypes().add("javax.json.JsonObject");

        final JsonObject jsonObject = new JsonObject();
        jsonObject.getStructure().put("key", new Element("java.lang.String", "value"));
        jsonObject.getStructure().put("duke", new Element("java.lang.Integer", 42));

        final JsonArray jsonArray = new JsonArray();
        jsonArray.getElements().add(new Element(Types.PRIMITIVE_BOOLEAN, 1));
        jsonArray.getElements().add(new Element("java.lang.String", "hello"));

        firstResult.getInlineEntities().add(jsonArray);
        secondResult.getInlineEntities().add(jsonObject);

        return new HashSet<>(Arrays.asList(firstResult, secondResult));
    }

}
