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

package com.sebastian_daschner.jaxrs_analyzer.analysis.project.methods.testclasses.json;

import com.sebastian_daschner.jaxrs_analyzer.model.elements.*;
import com.sebastian_daschner.jaxrs_analyzer.model.elements.JsonValue;

import javax.json.Json;
import javax.json.JsonStructure;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TestClass7 {

    public JsonStructure method() {
        if ("".equals(""))
            return Json.createArrayBuilder().add(true).add("duke").build();

        return Json.createObjectBuilder().add("key", "value").build();
    }

    public static Set<JsonValue> getResult() {

        final JsonObject jsonObject = new JsonObject();
        final JsonArray jsonArray = new JsonArray();
        jsonArray.getElements().add(new Element("java.lang.Boolean", 1));
        jsonArray.getElements().add(new Element("java.lang.String", "duke"));
        jsonObject.getStructure().put("key", new Element("java.lang.String", "value"));

        return new HashSet<>(Arrays.asList(jsonArray, jsonObject));
    }

}
