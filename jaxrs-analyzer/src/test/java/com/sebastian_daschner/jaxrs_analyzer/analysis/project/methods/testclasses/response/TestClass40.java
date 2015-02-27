package com.sebastian_daschner.jaxrs_analyzer.analysis.project.methods.testclasses.response;

import com.sebastian_daschner.jaxrs_analyzer.model.elements.HttpResponse;
import com.sebastian_daschner.jaxrs_analyzer.model.elements.Element;
import com.sebastian_daschner.jaxrs_analyzer.model.elements.JsonArray;
import com.sebastian_daschner.jaxrs_analyzer.model.elements.JsonObject;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;

public class TestClass40 {

    public Response method() {
        BiFunction<JsonObjectBuilder, String, JsonObjectBuilder> function = JsonObjectBuilder::addNull;
        BinaryOperator<JsonArrayBuilder> anotherFunction = JsonArrayBuilder::add;

        JsonObjectBuilder builder = Json.createObjectBuilder();
        function.apply(builder, "test");

        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        JsonArrayBuilder anotherArrayBuilder = Json.createArrayBuilder();

        arrayBuilder.add("test1");
        anotherArrayBuilder.add("test2");
        anotherFunction.apply(arrayBuilder, anotherArrayBuilder);

        builder.add("array", arrayBuilder);

        return Response.ok(builder.build()).build();
    }


    public static Set<HttpResponse> getResult() {
        final HttpResponse result = new HttpResponse();

        result.getStatuses().add(200);
        result.getEntityTypes().add("javax.json.JsonObject");

        final JsonArray jsonArray = new JsonArray();
        jsonArray.getElements().add(new Element("java.lang.String", "test1"));

        final JsonArray nestedArray = new JsonArray();
        nestedArray.getElements().add(new Element("java.lang.String", "test2"));
        jsonArray.getElements().add(new Element("javax.json.JsonArray", nestedArray));

        JsonObject jsonObject = new JsonObject();
        jsonObject.getStructure().put("test", new Element("java.lang.Object", null));
        jsonObject.getStructure().put("array", new Element("javax.json.JsonArray", jsonArray));

        result.getInlineEntities().add(jsonObject);

        return Collections.singleton(result);
    }

}
