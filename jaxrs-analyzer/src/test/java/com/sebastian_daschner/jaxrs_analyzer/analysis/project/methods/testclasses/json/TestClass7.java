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
