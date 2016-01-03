package com.sebastian_daschner.jaxrs_analyzer.analysis.results;

import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeIdentifier;
import com.sebastian_daschner.jaxrs_analyzer.model.types.Type;
import com.sebastian_daschner.jaxrs_analyzer.model.types.Types;

public final class TypeIdentifierUtils {

    public static final TypeIdentifier OBJECT_IDENTIFIER = TypeIdentifier.ofType(Types.OBJECT);
    public static final TypeIdentifier STRING_IDENTIFIER = TypeIdentifier.ofType(Types.STRING);
    public static final TypeIdentifier INT_IDENTIFIER = TypeIdentifier.ofType(Types.PRIMITIVE_INT);
    public static final TypeIdentifier STRING_LIST_IDENTIFIER = TypeIdentifier.ofType(new Type("java.util.List<java.lang.String>"));

    private TypeIdentifierUtils() {
        throw new UnsupportedOperationException();
    }

}
