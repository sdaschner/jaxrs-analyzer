package com.sebastian_daschner.jaxrs_analyzer.analysis.results;

import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeIdentifier;
import com.sebastian_daschner.jaxrs_analyzer.model.types.Types;

public final class TypeIdentifierUtils {

    public static final TypeIdentifier STRING_IDENTIFIER = TypeIdentifier.ofType(Types.STRING);

    private TypeIdentifierUtils() {
        throw new UnsupportedOperationException();
    }

}
