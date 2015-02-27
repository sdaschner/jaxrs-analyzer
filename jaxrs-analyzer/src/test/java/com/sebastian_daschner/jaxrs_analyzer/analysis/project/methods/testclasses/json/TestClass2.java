package com.sebastian_daschner.jaxrs_analyzer.analysis.project.methods.testclasses.json;

import com.sebastian_daschner.jaxrs_analyzer.model.elements.Element;
import com.sebastian_daschner.jaxrs_analyzer.model.elements.JsonValue;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.Collections;
import java.util.Set;

public class TestClass2 {

    public JsonObject method() {
        final JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
        objectBuilder.add("key", "value");
        if ("".equals(""))
            objectBuilder.add("another", "value");
        else
            objectBuilder.add("key", "test");
        return objectBuilder.build();
    }

    public static Set<JsonValue> getResult() {
        final com.sebastian_daschner.jaxrs_analyzer.model.elements.JsonObject jsonObject = new com.sebastian_daschner.jaxrs_analyzer.model.elements.JsonObject();
        jsonObject.getStructure().put("key", new Element("java.lang.String", "value", "test"));
        jsonObject.getStructure().put("another", new Element("java.lang.String", "value"));

        return Collections.singleton(jsonObject);
    }

}
