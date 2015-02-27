package com.sebastian_daschner.jaxrs_analyzer.analysis.results.testclasses.typeanalyzer;

import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeRepresentation;

import javax.json.Json;
import javax.json.JsonObject;
import java.util.List;
import java.util.Set;

/**
 * @author Sebastian Daschner
 */
public class TestClass5 {

    private List<String> first;

    private Set<String> second;

    public List<String> getFirst() {
        return first;
    }

    public Set<String> getSecond() {
        return second;
    }

    public static TypeRepresentation getResult() {
        final TypeRepresentation representation = new TypeRepresentation(TestClass5.class.getName());

        final JsonObject jsonObject = Json.createObjectBuilder().add("first", Json.createArrayBuilder()
                .add("string")).add("second", Json.createArrayBuilder().add("string")).build();

        representation.getRepresentations().put("application/json", jsonObject);
        return representation;
    }

}
