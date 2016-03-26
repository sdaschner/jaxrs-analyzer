package com.sebastian_daschner.jaxrs_analyzer.backend.swagger;

import com.sebastian_daschner.jaxrs_analyzer.model.Types;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeIdentifier;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeRepresentation;
import org.junit.Before;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonObject;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class SchemaBuilderTest {

    private static final TypeIdentifier STRING_IDENTIFIER = TypeIdentifier.ofType(Types.STRING);
    private static final TypeIdentifier INT_IDENTIFIER = TypeIdentifier.ofType(Types.PRIMITIVE_INT);
    private static final TypeIdentifier INTEGER_IDENTIFIER = TypeIdentifier.ofType(Types.INTEGER);
    private static final TypeIdentifier INT_LIST_IDENTIFIER = TypeIdentifier.ofType("java.util.List<java.lang.Integer>");
    private static final TypeIdentifier OBJECT_IDENTIFIER = TypeIdentifier.ofType(Types.OBJECT);

    private SchemaBuilder cut;
    private final Map<TypeIdentifier, TypeRepresentation> representations = new HashMap<>();

    @Before
    public void resetRepresentations() {
        representations.clear();
    }

    @Test
    public void testSimpleDefinitions() {
        representations.put(INT_LIST_IDENTIFIER, TypeRepresentation.ofCollection(INTEGER_IDENTIFIER, TypeRepresentation.ofConcrete(INTEGER_IDENTIFIER)));

        final TypeIdentifier modelIdentifier = TypeIdentifier.ofType("com.sebastian_daschner.test.Model");
        final Map<String, TypeIdentifier> modelProperties = new HashMap<>();

        modelProperties.put("test1", INT_IDENTIFIER);
        modelProperties.put("hello1", STRING_IDENTIFIER);
        modelProperties.put("array1", INT_LIST_IDENTIFIER);

        representations.put(modelIdentifier, TypeRepresentation.ofConcrete(modelIdentifier, modelProperties));

        final Map<String, TypeIdentifier> dynamicProperties = new HashMap<>();

        dynamicProperties.put("test2", INT_IDENTIFIER);
        dynamicProperties.put("hello2", STRING_IDENTIFIER);
        dynamicProperties.put("array2", INT_LIST_IDENTIFIER);

        final TypeIdentifier nestedDynamicIdentifier = TypeIdentifier.ofDynamic();
        final Map<String, TypeIdentifier> nestedDynamicProperties = new HashMap<>();
        nestedDynamicProperties.put("test", INT_IDENTIFIER);

        dynamicProperties.put("object2", nestedDynamicIdentifier);

        representations.put(nestedDynamicIdentifier, TypeRepresentation.ofConcrete(nestedDynamicIdentifier, nestedDynamicProperties));
        representations.put(OBJECT_IDENTIFIER, TypeRepresentation.ofConcrete(OBJECT_IDENTIFIER, dynamicProperties));

        cut = new SchemaBuilder(representations);
        assertThat(cut.build(modelIdentifier), is(Json.createObjectBuilder().add("$ref", "#/definitions/Model").build()));
        assertThat(cut.build(OBJECT_IDENTIFIER), is(Json.createObjectBuilder().add("$ref", "#/definitions/Object").build()));

        final JsonObject definitions = cut.getDefinitions();
        assertThat(definitions, is(Json.createObjectBuilder()
                .add("Model", Json.createObjectBuilder().add("properties", Json.createObjectBuilder()
                        .add("array1", Json.createObjectBuilder().add("type", "array").add("items", type("integer")))
                        .add("hello1", type("string"))
                        .add("test1", type("integer"))))
                .add("JsonObject", Json.createObjectBuilder().add("properties", Json.createObjectBuilder().add("test", type("integer"))))
                .add("Object", Json.createObjectBuilder().add("properties", Json.createObjectBuilder()
                        .add("array2", Json.createObjectBuilder().add("type", "array").add("items", type("integer")))
                        .add("hello2", type("string"))
                        .add("object2", Json.createObjectBuilder().add("$ref", "#/definitions/JsonObject"))
                        .add("test2", type("integer"))))
                .build()));
    }

    @Test
    public void testMultipleDefinitionsNameCollisions() {
        final TypeIdentifier lockIdentifier = TypeIdentifier.ofType("java.util.concurrent.locks.Lock");
        final TypeIdentifier anotherLockIdentifier = TypeIdentifier.ofType("javax.ejb.Lock");

        final Map<String, TypeIdentifier> lockProperties = new HashMap<>();
        lockProperties.put("test1", INT_IDENTIFIER);
        lockProperties.put("hello1", STRING_IDENTIFIER);
        lockProperties.put("array1", INT_LIST_IDENTIFIER);

        final Map<String, TypeIdentifier> anotherLockProperties = new HashMap<>();
        anotherLockProperties.put("test1", INT_IDENTIFIER);
        anotherLockProperties.put("hello1", STRING_IDENTIFIER);
        anotherLockProperties.put("array1", INT_LIST_IDENTIFIER);

        representations.put(INT_LIST_IDENTIFIER, TypeRepresentation.ofCollection(INTEGER_IDENTIFIER, TypeRepresentation.ofConcrete(INTEGER_IDENTIFIER)));
        representations.put(lockIdentifier, TypeRepresentation.ofConcrete(lockIdentifier, lockProperties));
        representations.put(anotherLockIdentifier, TypeRepresentation.ofConcrete(anotherLockIdentifier, anotherLockProperties));

        cut = new SchemaBuilder(representations);

        assertThat(cut.build(lockIdentifier), is(Json.createObjectBuilder().add("$ref", "#/definitions/Lock").build()));
        assertThat(cut.build(anotherLockIdentifier), is(Json.createObjectBuilder().add("$ref", "#/definitions/Lock_2").build()));

        final JsonObject definitions = cut.getDefinitions();
        assertThat(definitions, is(Json.createObjectBuilder()
                .add("Lock", Json.createObjectBuilder().add("properties", Json.createObjectBuilder()
                        .add("array1", Json.createObjectBuilder().add("type", "array").add("items", type("integer")))
                        .add("hello1", type("string"))
                        .add("test1", type("integer"))))
                .add("Lock_2", Json.createObjectBuilder().add("properties", Json.createObjectBuilder()
                        .add("array1", Json.createObjectBuilder().add("type", "array").add("items", type("integer")))
                        .add("hello1", type("string"))
                        .add("test1", type("integer"))))
                .build()));
    }

    @Test
    public void testSingleDynamicDefinitionMissingNestedType() {
        final TypeIdentifier identifier = TypeIdentifier.ofDynamic();

        final Map<String, TypeIdentifier> properties = new HashMap<>();
        properties.put("test1", INT_IDENTIFIER);
        properties.put("hello1", STRING_IDENTIFIER);
        // unknown type identifier
        properties.put("array1", INT_LIST_IDENTIFIER);

        representations.put(identifier, TypeRepresentation.ofConcrete(identifier, properties));

        cut = new SchemaBuilder(representations);

        assertThat(cut.build(identifier), is(Json.createObjectBuilder().add("$ref", "#/definitions/JsonObject").build()));

        final JsonObject definitions = cut.getDefinitions();
        assertThat(definitions, is(Json.createObjectBuilder()
                .add("JsonObject", Json.createObjectBuilder().add("properties", Json.createObjectBuilder()
                        .add("array1", Json.createObjectBuilder().add("type", "object"))
                        .add("hello1", type("string"))
                        .add("test1", type("integer"))))
                .build()));
    }

    @Test
    public void testMultipleDifferentDefinitions() {
        final TypeIdentifier modelIdentifier = TypeIdentifier.ofType("com.sebastian_daschner.test.Model");
        final Map<String, TypeIdentifier> properties = new HashMap<>();

        properties.put("test1", INT_IDENTIFIER);
        properties.put("hello1", STRING_IDENTIFIER);
        properties.put("array1", INT_LIST_IDENTIFIER);

        representations.put(modelIdentifier, TypeRepresentation.ofConcrete(modelIdentifier, properties));
        representations.put(INT_LIST_IDENTIFIER, TypeRepresentation.ofCollection(INTEGER_IDENTIFIER, TypeRepresentation.ofConcrete(INTEGER_IDENTIFIER)));

        cut = new SchemaBuilder(representations);

        assertThat(cut.build(modelIdentifier), is(Json.createObjectBuilder().add("$ref", "#/definitions/Model").build()));
        assertThat(cut.build(TypeIdentifier.ofType(Types.OBJECT)), is(Json.createObjectBuilder().add("type", "object").build()));
        final TypeIdentifier secondModelIdentifier = TypeIdentifier.ofType("com.sebastian_daschner.test.Model");
        assertThat(cut.build(secondModelIdentifier), is(Json.createObjectBuilder().add("$ref", "#/definitions/Model").build()));

        final JsonObject definitions = cut.getDefinitions();
        assertThat(definitions, is(Json.createObjectBuilder()
                .add("Model", Json.createObjectBuilder().add("properties", Json.createObjectBuilder()
                        .add("array1", Json.createObjectBuilder().add("type", "array").add("items", type("integer")))
                        .add("hello1", type("string"))
                        .add("test1", type("integer"))))
                .build()));
    }

    @Test
    public void testSameDynamicDefinitions() {
        final TypeIdentifier firstIdentifier = TypeIdentifier.ofDynamic();
        final TypeIdentifier secondIdentifier = TypeIdentifier.ofDynamic();

        final Map<String, TypeIdentifier> firstProperties = new HashMap<>();
        firstProperties.put("test1", INT_IDENTIFIER);
        firstProperties.put("hello1", STRING_IDENTIFIER);
        firstProperties.put("array1", INT_LIST_IDENTIFIER);
        firstProperties.put("nested", secondIdentifier);

        final Map<String, TypeIdentifier> secondProperties = new HashMap<>();
        secondProperties.put("nested", firstIdentifier);

        representations.put(INT_LIST_IDENTIFIER, TypeRepresentation.ofCollection(INTEGER_IDENTIFIER, TypeRepresentation.ofConcrete(INTEGER_IDENTIFIER)));
        representations.put(firstIdentifier, TypeRepresentation.ofConcrete(firstIdentifier, firstProperties));
        representations.put(secondIdentifier, TypeRepresentation.ofConcrete(secondIdentifier, secondProperties));

        cut = new SchemaBuilder(representations);

        assertThat(cut.build(firstIdentifier), is(Json.createObjectBuilder().add("$ref", "#/definitions/JsonObject").build()));
        assertThat(cut.build(firstIdentifier), is(Json.createObjectBuilder().add("$ref", "#/definitions/JsonObject").build()));
        assertThat(cut.build(secondIdentifier), is(Json.createObjectBuilder().add("$ref", "#/definitions/JsonObject_2").build()));

        final JsonObject definitions = cut.getDefinitions();
        assertThat(definitions, is(Json.createObjectBuilder()
                .add("JsonObject", Json.createObjectBuilder().add("properties", Json.createObjectBuilder()
                        .add("array1", Json.createObjectBuilder().add("type", "array").add("items", type("integer")))
                        .add("hello1", type("string"))
                        .add("nested", Json.createObjectBuilder().add("$ref", "#/definitions/JsonObject_2"))
                        .add("test1", type("integer"))))
                .add("JsonObject_2", Json.createObjectBuilder().add("properties", Json.createObjectBuilder()
                        .add("nested", Json.createObjectBuilder().add("$ref", "#/definitions/JsonObject"))))
                .build()));
    }

    private static JsonObject type(final String type) {
        return Json.createObjectBuilder().add("type", type).build();
    }

}