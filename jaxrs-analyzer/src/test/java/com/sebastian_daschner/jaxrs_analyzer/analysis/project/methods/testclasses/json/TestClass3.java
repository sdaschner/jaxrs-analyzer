package com.sebastian_daschner.jaxrs_analyzer.analysis.project.methods.testclasses.json;

import com.sebastian_daschner.jaxrs_analyzer.model.elements.JsonValue;
import com.sebastian_daschner.jaxrs_analyzer.model.elements.Element;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.Collections;
import java.util.Set;

public class TestClass3 {

    public JsonObject method() {
        final JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
        objectBuilder.addNull("key");
        if ("".equals(""))
            objectBuilder.add("key", "test");
        objectBuilder.add("object", Json.createObjectBuilder().add("duke", 42).build());
        return objectBuilder.build();
    }

    public static Set<JsonValue> getResult() {
        final com.sebastian_daschner.jaxrs_analyzer.model.elements.JsonObject jsonObject = new com.sebastian_daschner.jaxrs_analyzer.model.elements.JsonObject();
        jsonObject.getStructure().put("key", new Element("java.lang.Object", null, "test"));

        final com.sebastian_daschner.jaxrs_analyzer.model.elements.JsonObject innerObject = new com.sebastian_daschner.jaxrs_analyzer.model.elements.JsonObject();
        innerObject.getStructure().put("duke", new Element("java.lang.Integer", 42));
        jsonObject.getStructure().put("object", new Element("javax.json.JsonObject", innerObject));

        return Collections.singleton(jsonObject);
    }

}
