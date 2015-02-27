package com.sebastian_daschner.jaxrs_analyzer.analysis.project.methods.testclasses.response;

import com.sebastian_daschner.jaxrs_analyzer.model.elements.HttpResponse;
import com.sebastian_daschner.jaxrs_analyzer.model.elements.Element;
import com.sebastian_daschner.jaxrs_analyzer.model.elements.JsonArray;
import com.sebastian_daschner.jaxrs_analyzer.model.elements.JsonObject;

import javax.json.Json;
import javax.json.JsonStructure;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.Set;

public class TestClass38 {

    public Response method() {
        JsonStructure structure = Json.createArrayBuilder().add("duke").add(42).build();
        if ("".equals(""))
            structure = Json.createObjectBuilder().add("duke", 42).add("key", "value").build();
        return Response.ok(structure).build();
    }

    public static Set<HttpResponse> getResult() {
        final HttpResponse result = new HttpResponse();

        result.getStatuses().add(200);
        result.getEntityTypes().add("javax.json.JsonStructure");

        final JsonObject jsonObject = new JsonObject();
        jsonObject.getStructure().put("key", new Element("java.lang.String", "value"));
        jsonObject.getStructure().put("duke", new Element("java.lang.Integer", 42));

        final JsonArray jsonArray = new JsonArray();
        jsonArray.getElements().add(new Element("java.lang.String", "duke"));
        jsonArray.getElements().add(new Element("java.lang.Integer", 42));

        result.getInlineEntities().add(jsonObject);
        result.getInlineEntities().add(jsonArray);

        return Collections.singleton(result);
    }

}
