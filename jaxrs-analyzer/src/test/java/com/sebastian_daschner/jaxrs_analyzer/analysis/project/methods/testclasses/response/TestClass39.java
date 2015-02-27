package com.sebastian_daschner.jaxrs_analyzer.analysis.project.methods.testclasses.response;

import com.sebastian_daschner.jaxrs_analyzer.model.elements.HttpResponse;
import com.sebastian_daschner.jaxrs_analyzer.model.elements.Element;
import com.sebastian_daschner.jaxrs_analyzer.model.elements.JsonArray;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collector;

public class TestClass39 {

    private List<Object> tasks;

    public Response method() {
        return Response.ok(buildJsonArray()).build();
    }

    public javax.json.JsonArray buildJsonArray() {
        final Collector<String, JsonArrayBuilder, JsonArrayBuilder> collector = Collector.of(Json::createArrayBuilder, JsonArrayBuilder::add,
                JsonArrayBuilder::add);
        return tasks.stream().map(Object::toString).
                collect(collector).build();
    }

    public static Set<HttpResponse> getResult() {
        final HttpResponse result = new HttpResponse();

        result.getStatuses().add(200);
        // TODO un-comment
//        result.getEntityTypes().add("javax.json.JsonStructure");
        result.getEntityTypes().add("javax.json.JsonArray");

        final JsonArray jsonArray = new JsonArray();
        jsonArray.getElements().add(new Element("java.lang.String"));

//        result.getInlineEntities().add(jsonArray);

        return Collections.singleton(result);
    }

}
