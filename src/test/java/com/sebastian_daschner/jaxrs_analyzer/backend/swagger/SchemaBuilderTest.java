package com.sebastian_daschner.jaxrs_analyzer.backend.swagger;

import static com.sebastian_daschner.jaxrs_analyzer.analysis.results.TypeUtils.INT_IDENTIFIER;
import static com.sebastian_daschner.jaxrs_analyzer.analysis.results.TypeUtils.MODEL_IDENTIFIER;
import static com.sebastian_daschner.jaxrs_analyzer.analysis.results.TypeUtils.OBJECT_IDENTIFIER;
import static com.sebastian_daschner.jaxrs_analyzer.analysis.results.TypeUtils.STRING_IDENTIFIER;
import static com.sebastian_daschner.jaxrs_analyzer.backend.swagger.TypeIdentifierTestSupport.resetTypeIdentifierCounter;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import com.sebastian_daschner.jaxrs_analyzer.model.Types;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeIdentifier;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeRepresentation;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonObject;

public class SchemaBuilderTest {

    private static final TypeIdentifier INTEGER_IDENTIFIER = TypeIdentifier.ofType(Types.INTEGER);
    private static final TypeIdentifier INT_LIST_IDENTIFIER = TypeIdentifier.ofType("Ljava/util/List<Ljava/lang/Integer;>;");

    private SchemaBuilder cut;
    private final Map<TypeIdentifier, TypeRepresentation> representations = new HashMap<>();

    @Before
    public void resetRepresentations() {
        representations.clear();
        resetTypeIdentifierCounter();
    }

    @Test
    public void testSimpleDefinitions() {
        representations.put(INT_LIST_IDENTIFIER, TypeRepresentation.ofCollection(INTEGER_IDENTIFIER, TypeRepresentation.ofConcreteBuilder().identifier(INTEGER_IDENTIFIER).build()));

        final TypeIdentifier modelIdentifier = MODEL_IDENTIFIER;
        final Map<String, TypeIdentifier> modelProperties = new HashMap<>();

        modelProperties.put("test1", INT_IDENTIFIER);
        modelProperties.put("hello1", STRING_IDENTIFIER);
        modelProperties.put("array1", INT_LIST_IDENTIFIER);

        final Map<String, String> modelPropertyComments = new HashMap<>();

        modelPropertyComments.put("test1", "This is a description for test1");
        modelPropertyComments.put("hello1", "Some other description");
        modelPropertyComments.put("array1", "This is an array");

        representations.put(modelIdentifier,
            TypeRepresentation.ofConcreteBuilder().identifier(modelIdentifier).description("Our overall model").properties(modelProperties)
                .propertyDescriptions(modelPropertyComments).build());

        final Map<String, TypeIdentifier> dynamicProperties = new HashMap<>();

        dynamicProperties.put("test2", INT_IDENTIFIER);
        dynamicProperties.put("hello2", STRING_IDENTIFIER);
        dynamicProperties.put("array2", INT_LIST_IDENTIFIER);

        final TypeIdentifier nestedDynamicIdentifier = TypeIdentifier.ofDynamic();
        final Map<String, TypeIdentifier> nestedDynamicProperties = new HashMap<>();
        nestedDynamicProperties.put("test", INT_IDENTIFIER);

        dynamicProperties.put("object2", nestedDynamicIdentifier);

        representations.put(nestedDynamicIdentifier, TypeRepresentation.ofConcreteBuilder().identifier(nestedDynamicIdentifier).properties(nestedDynamicProperties).build());
        representations.put(OBJECT_IDENTIFIER, TypeRepresentation.ofConcreteBuilder().identifier(OBJECT_IDENTIFIER).properties(dynamicProperties).build());

        cut = new SchemaBuilder(representations);
        assertThat(cut.build(modelIdentifier).build(), is(Json.createObjectBuilder().add("$ref", "#/definitions/Model").build()));
        assertThat(cut.build(OBJECT_IDENTIFIER).build(), is(Json.createObjectBuilder().add("$ref", "#/definitions/Object").build()));

        final JsonObject definitions = cut.getDefinitions();
        assertThat(definitions, is(Json.createObjectBuilder()
                .add("Model", Json.createObjectBuilder().add("description", "Our overall model").add("properties", Json.createObjectBuilder()
                        .add("array1", Json.createObjectBuilder().add("type", "array").add("items", type("integer")).add("description", "This is an array"))
                        .add("hello1", typeWithDesc("string", "Some other description"))
                        .add("test1", typeWithDesc("integer", "This is a description for test1"))))
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
        final TypeIdentifier lockIdentifier = TypeIdentifier.ofType("Ljava/util/concurrent/locks/Lock;");
        final TypeIdentifier anotherLockIdentifier = TypeIdentifier.ofType("Ljavax/ejb/Lock;");

        final Map<String, TypeIdentifier> lockProperties = new HashMap<>();
        lockProperties.put("test1", INT_IDENTIFIER);
        lockProperties.put("hello1", STRING_IDENTIFIER);
        lockProperties.put("array1", INT_LIST_IDENTIFIER);

        final Map<String, TypeIdentifier> anotherLockProperties = new HashMap<>();
        anotherLockProperties.put("test1", INT_IDENTIFIER);
        anotherLockProperties.put("hello1", STRING_IDENTIFIER);
        anotherLockProperties.put("array1", INT_LIST_IDENTIFIER);

        representations.put(INT_LIST_IDENTIFIER, TypeRepresentation.ofCollection(INTEGER_IDENTIFIER, TypeRepresentation.ofConcreteBuilder().identifier(INTEGER_IDENTIFIER).build()));
        representations.put(lockIdentifier, TypeRepresentation.ofConcreteBuilder().identifier(lockIdentifier).properties(lockProperties).build());
        representations.put(anotherLockIdentifier, TypeRepresentation.ofConcreteBuilder().identifier(anotherLockIdentifier).properties(anotherLockProperties).build());

        cut = new SchemaBuilder(representations);

        assertThat(cut.build(lockIdentifier).build(), is(Json.createObjectBuilder().add("$ref", "#/definitions/Lock").build()));
        assertThat(cut.build(anotherLockIdentifier).build(), is(Json.createObjectBuilder().add("$ref", "#/definitions/Lock_2").build()));

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

        representations.put(identifier, TypeRepresentation.ofConcreteBuilder().identifier(identifier).properties(properties).build());

        cut = new SchemaBuilder(representations);

        assertThat(cut.build(identifier).build(), is(Json.createObjectBuilder().add("$ref", "#/definitions/JsonObject").build()));

        final JsonObject definitions = cut.getDefinitions();
        assertThat(definitions, is(Json.createObjectBuilder()
                .add("JsonObject", Json.createObjectBuilder().add("properties", Json.createObjectBuilder()
                        .add("array1", Json.createObjectBuilder().add("type", "object"))
                        .add("hello1", type("string"))
                        .add("test1", type("integer"))))
                .build()));
    }

    @Test
    public void testSimpleDefinitionSomeMissingComments() {
        representations.put(INT_LIST_IDENTIFIER, TypeRepresentation.ofCollection(INTEGER_IDENTIFIER, TypeRepresentation.ofConcreteBuilder().identifier(INTEGER_IDENTIFIER).build()));

        final TypeIdentifier modelIdentifier = MODEL_IDENTIFIER;
        final Map<String, TypeIdentifier> modelProperties = new HashMap<>();

        modelProperties.put("test1", INT_IDENTIFIER);
        modelProperties.put("hello1", STRING_IDENTIFIER);
        modelProperties.put("hello2", STRING_IDENTIFIER);
        modelProperties.put("array1", INT_LIST_IDENTIFIER);

        final Map<String, String> modelPropertyComments = new HashMap<>();

        modelPropertyComments.put("test1", "This is a description for test1");
        modelPropertyComments.put("hello2", "This is a description for hello2");

        representations.put(modelIdentifier,
            TypeRepresentation.ofConcreteBuilder().identifier(modelIdentifier).description("Our overall model").properties(modelProperties)
                .propertyDescriptions(modelPropertyComments).build());

        cut = new SchemaBuilder(representations);
        assertThat(cut.build(modelIdentifier).build(), is(Json.createObjectBuilder().add("$ref", "#/definitions/Model").build()));

        final JsonObject definitions = cut.getDefinitions();
        assertThat(definitions, is(Json.createObjectBuilder()
            .add("Model", Json.createObjectBuilder().add("description", "Our overall model").add("properties", Json.createObjectBuilder()
                .add("array1", Json.createObjectBuilder().add("type", "array").add("items", type("integer")))
                .add("hello1", type("string"))
                .add("hello2", typeWithDesc("string", "This is a description for hello2"))
                .add("test1", typeWithDesc("integer", "This is a description for test1"))))
            .build()));
    }

    @Test
    public void testMultipleDifferentDefinitions() {
        final Map<String, TypeIdentifier> properties = new HashMap<>();

        properties.put("test1", INT_IDENTIFIER);
        properties.put("hello1", STRING_IDENTIFIER);
        properties.put("array1", INT_LIST_IDENTIFIER);

        representations.put(MODEL_IDENTIFIER, TypeRepresentation.ofConcreteBuilder().identifier(MODEL_IDENTIFIER).properties(properties).build());
        representations.put(INT_LIST_IDENTIFIER, TypeRepresentation.ofCollection(INTEGER_IDENTIFIER, TypeRepresentation.ofConcreteBuilder().identifier(INTEGER_IDENTIFIER).build()));

        cut = new SchemaBuilder(representations);

        assertThat(cut.build(MODEL_IDENTIFIER).build(), is(Json.createObjectBuilder().add("$ref", "#/definitions/Model").build()));
        assertThat(cut.build(TypeIdentifier.ofType(Types.OBJECT)).build(), is(Json.createObjectBuilder().add("type", "object").build()));
        // build with different type identifier instance
        assertThat(cut.build(TypeIdentifier.ofType("Lcom/sebastian_daschner/test/Model;")).build(), is(Json.createObjectBuilder().add("$ref", "#/definitions/Model").build()));

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

        representations.put(INT_LIST_IDENTIFIER, TypeRepresentation.ofCollection(INTEGER_IDENTIFIER, TypeRepresentation.ofConcreteBuilder().identifier(INTEGER_IDENTIFIER).build()));
        representations.put(firstIdentifier, TypeRepresentation.ofConcreteBuilder().identifier(firstIdentifier).properties(firstProperties).build());
        representations.put(secondIdentifier, TypeRepresentation.ofConcreteBuilder().identifier(secondIdentifier).properties(secondProperties).build());

        cut = new SchemaBuilder(representations);

        assertThat(cut.build(firstIdentifier).build(), is(Json.createObjectBuilder().add("$ref", "#/definitions/JsonObject").build()));
        assertThat(cut.build(firstIdentifier).build(), is(Json.createObjectBuilder().add("$ref", "#/definitions/JsonObject").build()));
        assertThat(cut.build(secondIdentifier).build(), is(Json.createObjectBuilder().add("$ref", "#/definitions/JsonObject_2").build()));

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

    @Test
    public void testDifferentDynamicDefinitions() {
        final TypeIdentifier firstIdentifier = TypeIdentifier.ofDynamic();
        final TypeIdentifier secondIdentifier = TypeIdentifier.ofDynamic();
        final TypeIdentifier thirdIdentifier = TypeIdentifier.ofDynamic();

        final Map<String, TypeIdentifier> firstProperties = new HashMap<>();
        firstProperties.put("_links", secondIdentifier);

        final Map<String, TypeIdentifier> secondProperties = new HashMap<>();
        secondProperties.put("self", thirdIdentifier);

        final Map<String, TypeIdentifier> thirdProperties = new HashMap<>();
        thirdProperties.put("href", STRING_IDENTIFIER);

        representations.put(firstIdentifier, TypeRepresentation.ofConcreteBuilder().identifier(firstIdentifier).properties(firstProperties).build());
        representations.put(secondIdentifier, TypeRepresentation.ofConcreteBuilder().identifier(secondIdentifier).properties(secondProperties).build());
        representations.put(thirdIdentifier, TypeRepresentation.ofConcreteBuilder().identifier(thirdIdentifier).properties(thirdProperties).build());

        cut = new SchemaBuilder(representations);

        assertThat(cut.build(firstIdentifier).build(), is(Json.createObjectBuilder().add("$ref", "#/definitions/JsonObject").build()));
        assertThat(cut.build(secondIdentifier).build(), is(Json.createObjectBuilder().add("$ref", "#/definitions/JsonObject_2").build()));
        assertThat(cut.build(thirdIdentifier).build(), is(Json.createObjectBuilder().add("$ref", "#/definitions/JsonObject_3").build()));

        final JsonObject definitions = cut.getDefinitions();
        assertThat(definitions, is(Json.createObjectBuilder()
                .add("JsonObject", Json.createObjectBuilder().add("properties", Json.createObjectBuilder()
                        .add("_links", Json.createObjectBuilder().add("$ref", "#/definitions/JsonObject_2"))))
                .add("JsonObject_2", Json.createObjectBuilder().add("properties", Json.createObjectBuilder()
                        .add("self", Json.createObjectBuilder().add("$ref", "#/definitions/JsonObject_3"))))
                .add("JsonObject_3", Json.createObjectBuilder().add("properties", Json.createObjectBuilder()
                        .add("href", type("string"))))
                .build()));
    }

    @Test
    public void testEnumDefinitions() {
        final TypeIdentifier enumIdentifier = TypeIdentifier.ofType("Lcom/sebastian_daschner/test/Enumeration;");

        final Map<String, TypeIdentifier> modelProperties = new HashMap<>();
        modelProperties.put("foobar", enumIdentifier);
        modelProperties.put("hello1", STRING_IDENTIFIER);

        final Map<String, String> propertyDescriptions = new HashMap<>();
        propertyDescriptions.put("foobar", "foobar is important, so here is its JavaDoc-type comment.");
        propertyDescriptions.put("hello1", "Another comment here.");

        representations.put(MODEL_IDENTIFIER,
            TypeRepresentation.ofConcreteBuilder().identifier(MODEL_IDENTIFIER).properties(modelProperties).propertyDescriptions(propertyDescriptions).build());
        representations.put(enumIdentifier, TypeRepresentation.ofEnum(enumIdentifier, "THIRD", "FIRST", "SECOND"));

        cut = new SchemaBuilder(representations);

        assertThat(cut.build(MODEL_IDENTIFIER).build(), is(Json.createObjectBuilder().add("$ref", "#/definitions/Model").build()));
        assertThat(cut.build(enumIdentifier).build(), is(Json.createObjectBuilder().add("type", "string")
                .add("enum", Json.createArrayBuilder().add("FIRST").add("SECOND").add("THIRD")).build()));

        final JsonObject definitions = cut.getDefinitions();
        assertThat(definitions, is(Json.createObjectBuilder()
                .add("Model", Json.createObjectBuilder().add("properties", Json.createObjectBuilder().add("foobar",
                    Json.createObjectBuilder().add("type", "string").add("enum", Json.createArrayBuilder().add("FIRST").add("SECOND").add("THIRD"))
                        .add("description", "foobar is important, so here is its JavaDoc-type comment."))
                        .add("hello1", typeWithDesc("string", "Another comment here."))))
                .build()));
    }

    private static JsonObject type(final String type) {
        return Json.createObjectBuilder().add("type", type).build();
    }

    private static JsonObject typeWithDesc(final String type, final String description) {
        return Json.createObjectBuilder().add("type", type).add("description", description).build();
    }

}