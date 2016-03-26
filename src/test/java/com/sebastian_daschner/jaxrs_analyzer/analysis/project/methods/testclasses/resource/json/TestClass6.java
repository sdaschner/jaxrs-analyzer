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

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import java.util.Collections;
import java.util.Set;

public class TestClass6 {

    public JsonObject method() {
        final JsonArray array = Json.createArrayBuilder().add(true).add("duke").build();
        return Json.createObjectBuilder()
                .add("array", array)
                .add("int", 42)
                .add("boolean", true)
                .add("long", 100000000000000L)
                .add("double", 1.2d)
                .add("jsonObject", Json.createObjectBuilder().add("key", "value").add("test", 1).build())
                .add("jsonArray", Json.createArrayBuilder().add("first").add("second").add(3d)
                        .add(Json.createObjectBuilder().add("key", "object").build())
                        .add(Json.createObjectBuilder().add("nested", "object")))
                .build();
    }

    public static Set<HttpResponse> getResult() {

        final com.sebastian_daschner.jaxrs_analyzer.model.elements.JsonObject jsonObject = new com.sebastian_daschner.jaxrs_analyzer.model.elements.JsonObject();
        final com.sebastian_daschner.jaxrs_analyzer.model.elements.JsonArray jsonArray = new com.sebastian_daschner.jaxrs_analyzer.model.elements.JsonArray();
        jsonArray.getElements().add(new Element(Types.PRIMITIVE_BOOLEAN, 1));
        jsonArray.getElements().add(new Element("java.lang.String", "duke"));
        jsonObject.getStructure().put("array", new Element("javax.json.JsonArray", jsonArray));
        jsonObject.getStructure().put("int", new Element("java.lang.Integer", 42));
        jsonObject.getStructure().put("boolean", new Element(Types.PRIMITIVE_BOOLEAN, 1));
        jsonObject.getStructure().put("long", new Element("java.lang.Long", 100000000000000L));
        jsonObject.getStructure().put("double", new Element("java.lang.Double", 1.2d));

        final com.sebastian_daschner.jaxrs_analyzer.model.elements.JsonObject object = new com.sebastian_daschner.jaxrs_analyzer.model.elements.JsonObject();
        object.getStructure().put("key", new Element("java.lang.String", "value"));
        object.getStructure().put("test", new Element("java.lang.Integer", 1));
        jsonObject.getStructure().put("jsonObject", new Element("javax.json.JsonObject", object));

        final com.sebastian_daschner.jaxrs_analyzer.model.elements.JsonArray array = new com.sebastian_daschner.jaxrs_analyzer.model.elements.JsonArray();
        array.getElements().add(new Element("java.lang.String", "first"));
        array.getElements().add(new Element("java.lang.String", "second"));
        array.getElements().add(new Element("java.lang.Double", 3d));

        final com.sebastian_daschner.jaxrs_analyzer.model.elements.JsonObject firstNested = new com.sebastian_daschner.jaxrs_analyzer.model.elements.JsonObject();
        firstNested.getStructure().put("key", new Element("java.lang.String", "object"));
        final com.sebastian_daschner.jaxrs_analyzer.model.elements.JsonObject secondNested = new com.sebastian_daschner.jaxrs_analyzer.model.elements.JsonObject();
        secondNested.getStructure().put("nested", new Element("java.lang.String", "object"));

        array.getElements().add(new Element("javax.json.JsonObject", firstNested));
        array.getElements().add(new Element("javax.json.JsonObject", secondNested));
        jsonObject.getStructure().put("jsonArray", new Element("javax.json.JsonArray", array));

        final HttpResponse httpResponse = new HttpResponse();
        httpResponse.getEntityTypes().add(JsonObject.class.getName());
        httpResponse.getInlineEntities().add(jsonObject);

        return Collections.singleton(httpResponse);
    }

}
