package com.sebastian_daschner.jaxrs_analyzer.backend.swagger;

import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeRepresentation;
import com.sebastian_daschner.jaxrs_analyzer.model.types.Type;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.core.MediaType;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class SchemaBuilderTest {

    private SchemaBuilder cut = new SchemaBuilder();

    @Test
    public void testSimpleDefinitions() {
        final TypeRepresentation model = new TypeRepresentation(new Type("com.sebastian_daschner.test.Model"));
        model.getRepresentations().put(MediaType.APPLICATION_JSON, Json.createObjectBuilder().add("test1", 0).add("hello1", "world")
                .add("array1", Json.createArrayBuilder().add(0).add("string")).add("object1", Json.createObjectBuilder().add("hello", "world")).build());

        final TypeRepresentation object = new TypeRepresentation(new Type("java.lang.Object"));
        object.getRepresentations().put(MediaType.APPLICATION_JSON, Json.createObjectBuilder().add("test2", 0).add("hello2", "world")
                .add("array2", Json.createArrayBuilder().add(0).add("string")).add("object2", Json.createObjectBuilder().add("test", 0)).build());

        assertThat(cut.build(model), is(Json.createObjectBuilder().add("$ref", "#/definitions/Model").build()));
        assertThat(cut.build(object), is(Json.createObjectBuilder().add("$ref", "#/definitions/Object").build()));

        final JsonObject definitions = cut.getDefinitions();
        assertThat(definitions, is(Json.createObjectBuilder()
                .add("Model", Json.createObjectBuilder().add("properties", Json.createObjectBuilder()
                        .add("test1", type("number"))
                        .add("hello1", type("string"))
                        .add("array1", Json.createObjectBuilder().add("type", "array").add("items", Json.createArrayBuilder().add(type("number")).add(type("string"))))
                        .add("object1", Json.createObjectBuilder().add("$ref", "#/definitions/NestedType"))))
                .add("Object", Json.createObjectBuilder().add("properties", Json.createObjectBuilder()
                        .add("test2", type("number"))
                        .add("hello2", type("string"))
                        .add("array2", Json.createObjectBuilder().add("type", "array").add("items", Json.createArrayBuilder().add(type("number")).add(type("string"))))
                        .add("object2", Json.createObjectBuilder().add("$ref", "#/definitions/NestedType_2"))))
                .add("NestedType", Json.createObjectBuilder().add("properties", Json.createObjectBuilder().add("hello", type("string"))))
                .add("NestedType_2", Json.createObjectBuilder().add("properties", Json.createObjectBuilder().add("test", type("number"))))
                .build()));
    }

    @Test
    public void testIgnoreKnownTypeDefinitions() {
        // representation will be ignored as Types are known types

        final TypeRepresentation string = new TypeRepresentation(new Type("java.lang.String"));
        string.getRepresentations().put(MediaType.APPLICATION_JSON, Json.createObjectBuilder().add("test1", 0).add("hello1", "world")
                .add("array1", Json.createArrayBuilder().add(0).add("string")).build());

        final TypeRepresentation number = new TypeRepresentation(new Type("java.lang.Double"));
        number.getRepresentations().put(MediaType.APPLICATION_JSON, Json.createObjectBuilder().add("test1", 0).add("hello1", "world")
                .add("array1", Json.createArrayBuilder().add(0).add("string")).build());

        final TypeRepresentation booleanType = new TypeRepresentation(new Type("boolean"));
        booleanType.getRepresentations().put(MediaType.APPLICATION_JSON, Json.createObjectBuilder().add("test1", 0).add("hello1", "world")
                .add("array1", Json.createArrayBuilder().add(0).add("string")).build());

        assertThat(cut.build(string), is(type("string")));
        assertThat(cut.build(number), is(type("number")));
        assertThat(cut.build(booleanType), is(type("boolean")));

        final JsonObject definitions = cut.getDefinitions();
        assertTrue("definitions is not empty: " + definitions, definitions.isEmpty());
    }

    @Test
    public void testMultipleDefinitions() {
        final TypeRepresentation model = new TypeRepresentation(new Type("com.sebastian_daschner.test.Model"));
        model.getRepresentations().put(MediaType.APPLICATION_JSON, Json.createObjectBuilder().add("test1", 0).add("hello1", "world")
                .add("array1", Json.createArrayBuilder().add(0).add("string")).build());

        final TypeRepresentation object = new TypeRepresentation(new Type("java.lang.Object"));
        object.getRepresentations().put(MediaType.APPLICATION_JSON, Json.createObjectBuilder().add("test2", 0).add("hello2", "world")
                .add("array2", Json.createArrayBuilder().add(0).add("string")).build());

        final TypeRepresentation anotherModel = new TypeRepresentation(new Type("com.sebastian_daschner.test.Model"));
        anotherModel.getRepresentations().put(MediaType.APPLICATION_JSON, Json.createObjectBuilder().add("test1", 0).add("hello1", "world")
                .add("array1", Json.createArrayBuilder().add(0).add("string")).build());

        assertThat(cut.build(model), is(Json.createObjectBuilder().add("$ref", "#/definitions/Model").build()));
        assertThat(cut.build(object), is(Json.createObjectBuilder().add("$ref", "#/definitions/Object").build()));
        assertThat(cut.build(anotherModel), is(Json.createObjectBuilder().add("$ref", "#/definitions/Model").build()));

        final JsonObject definitions = cut.getDefinitions();
        assertThat(definitions, is(Json.createObjectBuilder()
                .add("Model", Json.createObjectBuilder().add("properties", Json.createObjectBuilder()
                        .add("test1", type("number"))
                        .add("hello1", type("string"))
                        .add("array1", Json.createObjectBuilder().add("type", "array").add("items", Json.createArrayBuilder().add(type("number")).add(type("string"))))))
                .add("Object", Json.createObjectBuilder().add("properties", Json.createObjectBuilder()
                        .add("test2", type("number"))
                        .add("hello2", type("string"))
                        .add("array2", Json.createObjectBuilder().add("type", "array").add("items", Json.createArrayBuilder().add(type("number")).add(type("string"))))))
                .build()));
    }

    @Test
    public void testMultipleDefinitionsNameCollisions() {
        final TypeRepresentation lock = new TypeRepresentation(new Type("java.util.concurrent.locks.Lock"));
        lock.getRepresentations().put(MediaType.APPLICATION_JSON, Json.createObjectBuilder().add("test1", 0).add("hello1", "world")
                .add("array1", Json.createArrayBuilder().add(0).add("string")).build());

        final TypeRepresentation anotherLock = new TypeRepresentation(new Type("javax.ejb.Lock"));
        anotherLock.getRepresentations().put(MediaType.APPLICATION_JSON, Json.createObjectBuilder().add("test1", 0).add("hello1", "world")
                .add("array1", Json.createArrayBuilder().add(0).add("string")).build());

        assertThat(cut.build(lock), is(Json.createObjectBuilder().add("$ref", "#/definitions/Lock").build()));
        assertThat(cut.build(anotherLock), is(Json.createObjectBuilder().add("$ref", "#/definitions/Lock_2").build()));

        final JsonObject definitions = cut.getDefinitions();
        assertThat(definitions, is(Json.createObjectBuilder()
                .add("Lock", Json.createObjectBuilder().add("properties", Json.createObjectBuilder()
                        .add("test1", type("number"))
                        .add("hello1", type("string"))
                        .add("array1", Json.createObjectBuilder().add("type", "array").add("items", Json.createArrayBuilder().add(type("number")).add(type("string"))))))
                .add("Lock_2", Json.createObjectBuilder().add("properties", Json.createObjectBuilder()
                        .add("test1", type("number"))
                        .add("hello1", type("string"))
                        .add("array1", Json.createObjectBuilder().add("type", "array").add("items", Json.createArrayBuilder().add(type("number")).add(type("string"))))))
                .build()));
    }

    @Test
    public void testMultipleDifferentDefinitions() {
        final TypeRepresentation model = new TypeRepresentation(new Type("com.sebastian_daschner.test.Model"));
        model.getRepresentations().put(MediaType.APPLICATION_JSON, Json.createObjectBuilder().add("test1", 0).add("hello1", "world")
                .add("array1", Json.createArrayBuilder().add(0).add("string")).build());

        final TypeRepresentation object = new TypeRepresentation(new Type("java.lang.Object"));
        object.getRepresentations().put(MediaType.APPLICATION_JSON, Json.createObjectBuilder().add("test2", 0).add("hello2", "world")
                .add("array2", Json.createArrayBuilder().add(0).add("string")).build());

        // different representation is ignored
        final TypeRepresentation anotherModel = new TypeRepresentation(new Type("com.sebastian_daschner.test.Model"));
        anotherModel.getRepresentations().put(MediaType.APPLICATION_JSON, Json.createObjectBuilder().add("test3", 0).add("hello1", "world")
                .add("array1", Json.createArrayBuilder().add(0).add("string")).build());

        assertThat(cut.build(model), is(Json.createObjectBuilder().add("$ref", "#/definitions/Model").build()));
        assertThat(cut.build(object), is(Json.createObjectBuilder().add("$ref", "#/definitions/Object").build()));
        assertThat(cut.build(anotherModel), is(Json.createObjectBuilder().add("$ref", "#/definitions/Model").build()));

        final JsonObject definitions = cut.getDefinitions();
        assertThat(definitions, is(Json.createObjectBuilder()
                .add("Model", Json.createObjectBuilder().add("properties", Json.createObjectBuilder()
                        .add("test1", type("number"))
                        .add("hello1", type("string"))
                        .add("array1", Json.createObjectBuilder().add("type", "array").add("items", Json.createArrayBuilder().add(type("number")).add(type("string"))))))
                .add("Object", Json.createObjectBuilder().add("properties", Json.createObjectBuilder()
                        .add("test2", type("number"))
                        .add("hello2", type("string"))
                        .add("array2", Json.createObjectBuilder().add("type", "array").add("items", Json.createArrayBuilder().add(type("number")).add(type("string"))))))
                .build()));
    }

    private static JsonObject type(final String type) {
        return Json.createObjectBuilder().add("type", type).build();
    }

}