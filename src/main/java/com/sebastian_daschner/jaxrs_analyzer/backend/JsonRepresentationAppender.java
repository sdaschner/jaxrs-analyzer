package com.sebastian_daschner.jaxrs_analyzer.backend;

import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeIdentifier;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeRepresentation;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeRepresentationVisitor;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.sebastian_daschner.jaxrs_analyzer.model.Types.*;
import static com.sebastian_daschner.jaxrs_analyzer.backend.ComparatorUtils.mapKeyComparator;

/**
 * Adds the JSON representation of type identifiers to String builders.
 *
 * @author Sebastian Daschner
 */
public class JsonRepresentationAppender implements TypeRepresentationVisitor {

    private final StringBuilder builder;
    private final Map<TypeIdentifier, TypeRepresentation> representations;

    private int collectionDepth = 0;
    private Set<TypeIdentifier> visitedTypes = new HashSet<>();

    public JsonRepresentationAppender(final StringBuilder builder, final Map<TypeIdentifier, TypeRepresentation> representations) {
        this.builder = builder;
        this.representations = representations;
    }

    @Override
    public void visit(TypeRepresentation.ConcreteTypeRepresentation representation) {
        if (representation.getProperties().isEmpty())
            builder.append(toPrimitiveType(representation.getIdentifier()));
        else {
            builder.append('{');
            visitedTypes.add(representation.getIdentifier());
            representation.getProperties().entrySet().stream().sorted(mapKeyComparator()).forEach(e -> {
                builder.append('"').append(e.getKey()).append("\":");
                final TypeRepresentation nestedRepresentation = representations.get(e.getValue());
                if (nestedRepresentation == null)
                    builder.append(toPrimitiveType(e.getValue()));
                else if (visitedTypes.contains(e.getValue()))
                    // prevent infinite loop from recursively nested types
                    builder.append("{}");
                else
                    nestedRepresentation.accept(this);
                builder.append(',');
            });
            visitedTypes.remove(representation.getIdentifier());
            builder.deleteCharAt(builder.length() - 1).append('}');
        }

        if (collectionDepth > 0) {
            builder.append(new String(new char[collectionDepth]).replace('\0', ']'));
            collectionDepth = 0;
        }
    }

    @Override
    public void visit(TypeRepresentation.CollectionTypeRepresentation representation) {
        builder.append('[');
        collectionDepth++;
    }

    private static String toPrimitiveType(final TypeIdentifier value) {
        final String type = value.getType();
        if (STRING.equals(type))
            return "\"string\"";

        if (BOOLEAN.equals(type) || PRIMITIVE_BOOLEAN.equals(type))
            return "false";

        if (INTEGER_TYPES.contains(type))
            return "0";

        if (DOUBLE_TYPES.contains(type))
            return "0.0";

        return "{}";
    }

}
