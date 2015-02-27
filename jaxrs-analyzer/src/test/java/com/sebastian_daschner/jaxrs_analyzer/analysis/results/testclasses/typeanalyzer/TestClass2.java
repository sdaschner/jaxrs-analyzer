package com.sebastian_daschner.jaxrs_analyzer.analysis.results.testclasses.typeanalyzer;

import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeRepresentation;

import javax.json.Json;
import javax.json.JsonObject;
import javax.xml.bind.annotation.XmlTransient;

/**
 * @author Sebastian Daschner
 */
public class TestClass2 {

    private String first;

    @XmlTransient // ignored due to XMLAccessorTypes#PUBLIC_MEMBER
    private String second;

    @XmlTransient
    public String third;

    @XmlTransient
    public String getFirst() {
        return first;
    }

    public String getSecond() {
        return second;
    }

    public static TypeRepresentation getResult() {
        final TypeRepresentation representation = new TypeRepresentation(TestClass2.class.getName());

        final JsonObject jsonObject = Json.createObjectBuilder().add("second", "string").build();

        representation.getRepresentations().put("application/json", jsonObject);
        return representation;
    }

}
