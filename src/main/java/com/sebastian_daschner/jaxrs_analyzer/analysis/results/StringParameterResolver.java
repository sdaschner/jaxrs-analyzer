package com.sebastian_daschner.jaxrs_analyzer.analysis.results;

import com.sebastian_daschner.jaxrs_analyzer.model.Types;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.MethodParameter;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeIdentifier;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeRepresentation;

import java.util.Map;
import java.util.Set;

/**
 * Replaces all method parameter types which are not Strings, primitives, enums or collections of these with the String type.
 * JSR 339 requires the types to be serializable as String (in fact to have a String constructor or a {@code valueOf} method, respectively).
 * Therefore the types are assumed to be represented as String when exposed via REST.
 */
class StringParameterResolver {

    private final Map<TypeIdentifier, TypeRepresentation> typeRepresentations;
    private final JavaTypeAnalyzer javaTypeAnalyzer;

    StringParameterResolver(final Map<TypeIdentifier, TypeRepresentation> typeRepresentations, final JavaTypeAnalyzer javaTypeAnalyzer) {
        this.typeRepresentations = typeRepresentations;
        this.javaTypeAnalyzer = javaTypeAnalyzer;
    }

    void replaceParametersTypes(final Set<MethodParameter> parameters) {
        parameters.forEach(p -> {
            if (isStringOrPrimitive(p.getType()))
                return;

            final TypeIdentifier identifier = javaTypeAnalyzer.analyze(p.getType().getType());
            final TypeRepresentation typeRepresentation = typeRepresentations.get(identifier);

            if (isEnum(typeRepresentation))
                return;

            if (isCollection(typeRepresentation)) {
                final TypeIdentifier componentType = typeRepresentation.getComponentType();
                if (isStringOrPrimitive(componentType) || isEnum(typeRepresentations.get(componentType)))
                    return;

                p.setType(javaTypeAnalyzer.analyze("Ljava/util/List<Ljava/lang/String;>;"));
                return;
            }

            p.setType(TypeIdentifier.ofType(Types.STRING));
        });
    }

    private boolean isStringOrPrimitive(final TypeIdentifier componentType) {
        return componentType.getType().equals(Types.STRING) || Types.PRIMITIVE_TYPES_ALL.contains(componentType.getType());
    }

    private boolean isEnum(final TypeRepresentation typeRepresentation) {
        return typeRepresentation instanceof TypeRepresentation.EnumTypeRepresentation;
    }

    private boolean isCollection(final TypeRepresentation typeRepresentation) {
        return typeRepresentation instanceof TypeRepresentation.CollectionTypeRepresentation;
    }

}
