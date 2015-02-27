package com.sebastian_daschner.jaxrs_analyzer.analysis.results.testclasses.typeanalyzer;

import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeRepresentation;

import javax.json.Json;
import javax.json.JsonObject;
import java.time.LocalDate;
import java.util.Date;

/**
 * @author Sebastian Daschner
 */
public class TestClass4 {

    private LocalDate first;

    private Date second;

    public LocalDate getFirst() {
        return first;
    }

    public Date getSecond() {
        return second;
    }

    public static TypeRepresentation getResult() {
        final TypeRepresentation representation = new TypeRepresentation(TestClass4.class.getName());

        final JsonObject jsonObject = Json.createObjectBuilder().add("first", "date").add("second", "date").build();

        representation.getRepresentations().put("application/json", jsonObject);
        return representation;
    }

}
