package com.sebastian_daschner.jaxrs_analyzer.analysis.results.testclasses.typeanalyzer;

import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeRepresentation;

import javax.json.Json;
import javax.json.JsonObject;

/**
 * @author Sebastian Daschner
 */
public class TestClass1 {

    private static String PRIVATE_FIELD;
    public static String PUBLIC_FIELD;
    private String privateField;
    protected String protectedField;
    public String publicField;

    public String getTest() {
        return null;
    }

    public int getInt() {
        return 0;
    }

    public static String getStaticString() {
        return null;
    }

    public String string() {
        return null;
    }


    public static TypeRepresentation getResult() {
        final TypeRepresentation representation = new TypeRepresentation(TestClass1.class.getName());

        final JsonObject jsonObject = Json.createObjectBuilder().add("publicField", "string").add("test", "string").add("int", 0).build();

        representation.getRepresentations().put("application/json", jsonObject);
        return representation;
    }

}
