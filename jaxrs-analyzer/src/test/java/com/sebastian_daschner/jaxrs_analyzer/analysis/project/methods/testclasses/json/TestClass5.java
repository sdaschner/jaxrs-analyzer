package com.sebastian_daschner.jaxrs_analyzer.analysis.project.methods.testclasses.json;

import com.sebastian_daschner.jaxrs_analyzer.model.elements.Element;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import java.util.Collections;
import java.util.Set;

public class TestClass5 {

    public JsonObject method() {
        final JsonObject object = Json.createObjectBuilder().add("test", true).build();
        final JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
        objectBuilder.addNull("key");
        if ("".equals(""))
            objectBuilder.add("value", JsonValue.FALSE);
        objectBuilder.add("test", object.getBoolean("test"));
        return objectBuilder.build();
    }

    public static Set<com.sebastian_daschner.jaxrs_analyzer.model.elements.JsonValue> getResult() {
        final com.sebastian_daschner.jaxrs_analyzer.model.elements.JsonObject jsonObject = new com.sebastian_daschner.jaxrs_analyzer.model.elements.JsonObject();
        jsonObject.getStructure().put("key", new Element("java.lang.Object", null));
        jsonObject.getStructure().put("value", new Element("javax.json.JsonValue", JsonValue.FALSE));
        jsonObject.getStructure().put("test", new Element("java.lang.Boolean", 1));

        return Collections.singleton(jsonObject);
    }

}
