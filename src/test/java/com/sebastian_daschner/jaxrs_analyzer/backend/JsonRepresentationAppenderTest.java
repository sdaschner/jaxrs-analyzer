package com.sebastian_daschner.jaxrs_analyzer.backend;

import com.sebastian_daschner.jaxrs_analyzer.model.Types;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeIdentifier;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeRepresentation;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeRepresentationVisitor;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class JsonRepresentationAppenderTest {

    private static final TypeIdentifier STRING_LIST_IDENTIFIER = TypeIdentifier.ofType("java.util.List<java.lang.String>");
    private static final TypeIdentifier STRING_IDENTIFIER = TypeIdentifier.ofType(Types.STRING);
    private static final TypeIdentifier INT_IDENTIFIER = TypeIdentifier.ofType(Types.PRIMITIVE_INT);

    private TypeRepresentationVisitor cut;
    private Map<TypeIdentifier, TypeRepresentation> representations;
    private StringBuilder builder;

    @Before
    public void setup() {
        representations = new HashMap<>();
        builder = new StringBuilder();
        cut = new JsonRepresentationAppender(builder, representations);
    }

    @Test
    public void testVisitPrimitive() {
        TypeRepresentation.ofConcrete(TypeIdentifier.ofType(Types.STRING)).accept(cut);
        assertThat(builder.toString(), is("\"string\""));
    }

    @Test
    public void testMissingRepresentation() {
        TypeRepresentation.ofCollection(STRING_LIST_IDENTIFIER, TypeRepresentation.ofConcrete(STRING_IDENTIFIER)).accept(cut);
        assertThat(builder.toString(), is("[\"string\"]"));
    }

    @Test
    public void testVisitSimpleList() {
        final TypeRepresentation stringRepresentation = TypeRepresentation.ofConcrete(STRING_IDENTIFIER);
        final TypeRepresentation listRepresentation = TypeRepresentation.ofCollection(STRING_LIST_IDENTIFIER, stringRepresentation);
        representations.put(STRING_LIST_IDENTIFIER, listRepresentation);

        listRepresentation.accept(cut);
        assertThat(builder.toString(), is("[\"string\"]"));

        clear(builder);
        listRepresentation.accept(cut);
        assertThat(builder.toString(), is("[\"string\"]"));

        clear(builder);
        listRepresentation.accept(cut);
        assertThat(builder.toString(), is("[\"string\"]"));

        clear(builder);
        stringRepresentation.accept(cut);
        assertThat(builder.toString(), is("\"string\""));
    }

    @Test
    public void testVisitMultipleList() {
        final TypeIdentifier identifier = TypeIdentifier.ofType("java.util.List<java.util.List<java.lang.String>>");
        final TypeRepresentation representation = TypeRepresentation.ofCollection(identifier, TypeRepresentation.ofCollection(STRING_LIST_IDENTIFIER,
                TypeRepresentation.ofConcrete(STRING_IDENTIFIER)));
        representations.put(identifier, representation);

        representation.accept(cut);
        assertThat(builder.toString(), is("[[\"string\"]]"));

        clear(builder);
        representation.accept(cut);
        assertThat(builder.toString(), is("[[\"string\"]]"));

        clear(builder);
        representation.accept(cut);
        assertThat(builder.toString(), is("[[\"string\"]]"));
    }

    @Test
    public void testVisitDynamic() {
        TypeRepresentation.ofConcrete(TypeIdentifier.ofDynamic()).accept(cut);
        assertThat(builder.toString(), is("{}"));

        clear(builder);
        TypeRepresentation.ofCollection(TypeIdentifier.ofDynamic(), TypeRepresentation.ofConcrete(TypeIdentifier.ofDynamic())).accept(cut);
        assertThat(builder.toString(), is("[{}]"));
    }

    @Test
    public void testVisitConcrete() {
        final TypeIdentifier identifier = TypeIdentifier.ofType("com.sebastian_daschner.test.Model");
        Map<String, TypeIdentifier> properties = new HashMap<>();
        properties.put("world", INT_IDENTIFIER);
        properties.put("hello", STRING_IDENTIFIER);
        properties.put("abc", STRING_IDENTIFIER);
        final TypeRepresentation representation = TypeRepresentation.ofConcrete(identifier, properties);

        representations.put(identifier, representation);
        representation.accept(cut);
        assertThat(builder.toString(), is("{\"abc\":\"string\",\"hello\":\"string\",\"world\":0}"));
    }

    @Test
    public void testVisitConcreteWithNested() {
        final TypeIdentifier identifier = TypeIdentifier.ofType("com.sebastian_daschner.test.Model");
        Map<String, TypeIdentifier> properties = new HashMap<>();
        properties.put("world", INT_IDENTIFIER);
        properties.put("hello", STRING_IDENTIFIER);
        properties.put("abc", STRING_IDENTIFIER);
        properties.put("model", identifier);
        final TypeRepresentation representation = TypeRepresentation.ofConcrete(identifier, properties);

        representations.put(identifier, representation);
        representation.accept(cut);
        assertThat(builder.toString(), is("{\"abc\":\"string\",\"hello\":\"string\",\"model\":{},\"world\":0}"));
    }

    @Test
    public void testVisitConcreteWithNestedPreventSuppression() {
        final TypeIdentifier dateIdentifier = TypeIdentifier.ofType(Types.DATE);
        final TypeIdentifier modelIdentifier = TypeIdentifier.ofType("com.sebastian_daschner.test.Model");
        final TypeIdentifier objectIdentifier = TypeIdentifier.ofType(Types.OBJECT);

        Map<String, TypeIdentifier> properties = new HashMap<>();
        properties.put("world", INT_IDENTIFIER);
        properties.put("abc", STRING_IDENTIFIER);
        properties.put("model", modelIdentifier);
        final TypeRepresentation dateRepresentation = TypeRepresentation.ofConcrete(dateIdentifier, properties);
        representations.put(dateIdentifier, dateRepresentation);

        properties = new HashMap<>();
        properties.put("world", INT_IDENTIFIER);
        properties.put("hello", STRING_IDENTIFIER);
        properties.put("abc", STRING_IDENTIFIER);
        properties.put("model", modelIdentifier);
        final TypeRepresentation objectRepresentation = TypeRepresentation.ofConcrete(objectIdentifier, properties);
        representations.put(objectIdentifier, objectRepresentation);

        properties = new HashMap<>();
        properties.put("world", INT_IDENTIFIER);
        properties.put("hello", STRING_IDENTIFIER);
        properties.put("abc", STRING_IDENTIFIER);
        properties.put("date", dateIdentifier);
        properties.put("object", objectIdentifier);
        final TypeRepresentation modelRepresentation = TypeRepresentation.ofConcrete(modelIdentifier, properties);
        representations.put(modelIdentifier, modelRepresentation);

        // date
        // {\"abc\":\"string\",\"model\":{},\"world\":0}
        // object
        // {\"abc\":\"string\",\"hello\":\"string\",\"model\":{},\"world\":0}
        // model
        // {\"abc\":\"string\",\"date\":{},\"hello\":\"string\",\"object\":{},\"world\":0}

        dateRepresentation.accept(cut);
        assertThat(builder.toString(), is("{\"abc\":\"string\",\"model\":{\"abc\":\"string\",\"date\":{},\"hello\":\"string\",\"object\":{\"abc\":\"string\",\"hello\":\"string\",\"model\":{},\"world\":0},\"world\":0},\"world\":0}"));

        clear(builder);
        objectRepresentation.accept(cut);
        assertThat(builder.toString(), is("{\"abc\":\"string\",\"hello\":\"string\",\"model\":{\"abc\":\"string\",\"date\":{\"abc\":\"string\",\"model\":{},\"world\":0},\"hello\":\"string\",\"object\":{},\"world\":0},\"world\":0}"));

        clear(builder);
        modelRepresentation.accept(cut);
        assertThat(builder.toString(), is("{\"abc\":\"string\",\"date\":{\"abc\":\"string\",\"model\":{},\"world\":0},\"hello\":\"string\",\"object\":{\"abc\":\"string\",\"hello\":\"string\",\"model\":{},\"world\":0},\"world\":0}"));

        clear(builder);
        dateRepresentation.accept(cut);
        assertThat(builder.toString(), is("{\"abc\":\"string\",\"model\":{\"abc\":\"string\",\"date\":{},\"hello\":\"string\",\"object\":{\"abc\":\"string\",\"hello\":\"string\",\"model\":{},\"world\":0},\"world\":0},\"world\":0}"));

        clear(builder);
        objectRepresentation.accept(cut);
        assertThat(builder.toString(), is("{\"abc\":\"string\",\"hello\":\"string\",\"model\":{\"abc\":\"string\",\"date\":{\"abc\":\"string\",\"model\":{},\"world\":0},\"hello\":\"string\",\"object\":{},\"world\":0},\"world\":0}"));

        clear(builder);
        modelRepresentation.accept(cut);
        assertThat(builder.toString(), is("{\"abc\":\"string\",\"date\":{\"abc\":\"string\",\"model\":{},\"world\":0},\"hello\":\"string\",\"object\":{\"abc\":\"string\",\"hello\":\"string\",\"model\":{},\"world\":0},\"world\":0}"));
    }

    private static void clear(final StringBuilder builder) {
        builder.delete(0, builder.length());
    }

}