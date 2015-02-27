package com.sebastian_daschner.jaxrs_analyzer.analysis.project.methods.testclasses.json;

import com.sebastian_daschner.jaxrs_analyzer.model.elements.Element;
import com.sebastian_daschner.jaxrs_analyzer.model.elements.JsonValue;

import javax.json.Json;
import javax.json.JsonObject;
import java.util.Collections;
import java.util.Set;

public class TestClass1 {

    public JsonObject method() {
        return Json.createObjectBuilder().add("key", "value").build();
    }

    public static Set<JsonValue> getResult() {
        final com.sebastian_daschner.jaxrs_analyzer.model.elements.JsonObject jsonObject = new com.sebastian_daschner.jaxrs_analyzer.model.elements.JsonObject();
        jsonObject.getStructure().put("key", new Element("java.lang.String", "value"));

        return Collections.singleton(jsonObject);
    }

}
