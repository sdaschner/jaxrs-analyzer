package com.sebastian_daschner.jaxrs_analyzer.model.rest;

import com.sebastian_daschner.jaxrs_analyzer.model.Types;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.sebastian_daschner.jaxrs_analyzer.analysis.results.TypeUtils.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CollectionTypeRepresentationTest {

    @Test
    public void testContentEqualsConcrete() {
        final TypeRepresentation.ConcreteTypeRepresentation stringRepresentation = (TypeRepresentation.ConcreteTypeRepresentation) TypeRepresentation.ofConcrete(TypeIdentifier.ofType(Types.STRING));
        final TypeRepresentation.ConcreteTypeRepresentation objectRepresentation = (TypeRepresentation.ConcreteTypeRepresentation) TypeRepresentation.ofConcrete(TypeIdentifier.ofType(Types.OBJECT));

        assertTrue(stringRepresentation.contentEquals(objectRepresentation.getProperties()));

        final Map<String, TypeIdentifier> firstProperties = new HashMap<>();
        firstProperties.put("hello", STRING_IDENTIFIER);
        firstProperties.put("world", INT_IDENTIFIER);
        final TypeRepresentation.ConcreteTypeRepresentation firstRepresentation = (TypeRepresentation.ConcreteTypeRepresentation) TypeRepresentation.ofConcrete(OBJECT_IDENTIFIER, firstProperties);

        final Map<String, TypeIdentifier> secondProperties = new HashMap<>();
        secondProperties.put("hello", STRING_IDENTIFIER);
        secondProperties.put("world", INT_IDENTIFIER);

        assertTrue(firstRepresentation.contentEquals(secondProperties));
    }

    @Test
    public void testContentEqualsCollection() {
        final Map<String, TypeIdentifier> firstProperties = new HashMap<>();
        firstProperties.put("hello", STRING_IDENTIFIER);
        firstProperties.put("world", INT_IDENTIFIER);

        final TypeRepresentation firstRepresentation = TypeRepresentation.ofConcrete(OBJECT_IDENTIFIER, firstProperties);
        final TypeRepresentation secondRepresentation = TypeRepresentation.ofConcrete(OBJECT_IDENTIFIER, Collections.emptyMap());
        final TypeRepresentation thirdRepresentation = TypeRepresentation.ofConcrete(TypeIdentifier.ofDynamic(), new HashMap<>(firstProperties));
        final TypeRepresentation fourthRepresentation = TypeRepresentation.ofConcrete(TypeIdentifier.ofDynamic(), new HashMap<>(firstProperties));

        final TypeRepresentation.CollectionTypeRepresentation firstCollection = (TypeRepresentation.CollectionTypeRepresentation) TypeRepresentation.ofCollection(TypeIdentifier.ofDynamic(), firstRepresentation);
        final TypeRepresentation.CollectionTypeRepresentation secondCollection = (TypeRepresentation.CollectionTypeRepresentation) TypeRepresentation.ofCollection(TypeIdentifier.ofDynamic(), secondRepresentation);
        final TypeRepresentation.CollectionTypeRepresentation thirdCollection = (TypeRepresentation.CollectionTypeRepresentation) TypeRepresentation.ofCollection(TypeIdentifier.ofDynamic(), thirdRepresentation);
        final TypeRepresentation.CollectionTypeRepresentation fourthCollection = (TypeRepresentation.CollectionTypeRepresentation) TypeRepresentation.ofCollection(TypeIdentifier.ofDynamic(), fourthRepresentation);

        assertTrue(firstCollection.contentEquals(secondCollection.getRepresentation()));
        assertFalse(firstCollection.contentEquals(thirdCollection.getRepresentation()));
        assertTrue(thirdCollection.contentEquals(fourthCollection.getRepresentation()));
    }

}