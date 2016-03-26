package com.sebastian_daschner.jaxrs_analyzer.analysis.results;

import com.sebastian_daschner.jaxrs_analyzer.model.Types;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeIdentifier;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeRepresentation;

public final class TypeUtils {

    public static final TypeIdentifier OBJECT_IDENTIFIER = TypeIdentifier.ofType(Types.OBJECT);
    public static final TypeIdentifier STRING_IDENTIFIER = TypeIdentifier.ofType(Types.STRING);
    public static final TypeIdentifier INT_IDENTIFIER = TypeIdentifier.ofType(Types.PRIMITIVE_INT);
    public static final TypeIdentifier STRING_LIST_IDENTIFIER = TypeIdentifier.ofType("java.util.List<java.lang.String>");

    private TypeUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * Checks if the first representation fully equals to the second, i.e. the identifier has to be the same as well as the contained element
     * (for collections) or the properties (for concrete types).
     */
    public static boolean equals(final TypeRepresentation first, final TypeRepresentation second) {
        if (!first.equals(second))
            return false;

        final boolean firstCollection = first instanceof TypeRepresentation.CollectionTypeRepresentation;
        final boolean secondCollection = second instanceof TypeRepresentation.CollectionTypeRepresentation;

        if (firstCollection ^ secondCollection)
            return false;

        if (firstCollection)
            return ((TypeRepresentation.CollectionTypeRepresentation) first).contentEquals(((TypeRepresentation.CollectionTypeRepresentation) second).getRepresentation());

        return ((TypeRepresentation.ConcreteTypeRepresentation) first).contentEquals(((TypeRepresentation.ConcreteTypeRepresentation) second).getProperties());
    }

}
