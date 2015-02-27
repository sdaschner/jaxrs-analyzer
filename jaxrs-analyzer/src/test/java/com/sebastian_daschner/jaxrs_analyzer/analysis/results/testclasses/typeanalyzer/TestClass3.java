package com.sebastian_daschner.jaxrs_analyzer.analysis.results.testclasses.typeanalyzer;

import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeRepresentation;

import javax.json.Json;
import javax.json.JsonObject;

/**
 * @author Sebastian Daschner
 */
public class TestClass3 {

    private InnerClass first;

    private Type second;

    private AnotherInner third;

    public InnerClass getFirst() {
        return first;
    }

    public Type getSecond() {
        return second;
    }

    public static TypeRepresentation getResult() {
        final TypeRepresentation representation = new TypeRepresentation(TestClass3.class.getName());

        final JsonObject jsonObject = Json.createObjectBuilder().add("first", Json.createObjectBuilder().add("name", "string"))
                .add("second", "string").add("third", Json.createObjectBuilder()).build();

        representation.getRepresentations().put("application/json", jsonObject);
        return representation;
    }

    public AnotherInner getThird() {
        return third;
    }

    private enum Type {
        ONE, TWO, THREE
    }

    private class InnerClass {
        private String name;

        public String getName() {
            return name;
        }
    }

    private class AnotherInner {
        private String notUsed;
    }
}
