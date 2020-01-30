/*
 * Copyright (C) 2015 Sebastian Daschner, sebastian-daschner.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sebastian_daschner.jaxrs_analyzer.backend.swagger;

import static com.sebastian_daschner.jaxrs_analyzer.backend.ComparatorUtils.mapKeyComparator;
import static com.sebastian_daschner.jaxrs_analyzer.model.Types.BOOLEAN;
import static com.sebastian_daschner.jaxrs_analyzer.model.Types.DOUBLE_TYPES;
import static com.sebastian_daschner.jaxrs_analyzer.model.Types.INTEGER_TYPES;
import static com.sebastian_daschner.jaxrs_analyzer.model.Types.PRIMITIVE_BOOLEAN;
import static com.sebastian_daschner.jaxrs_analyzer.model.Types.STRING;

import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeIdentifier;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeRepresentation;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeRepresentationVisitor;
import com.sebastian_daschner.jaxrs_analyzer.utils.Pair;

import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 * Creates Swagger schema type definitions.
 *
 * @author Sebastian Daschner
 */
class SchemaBuilder {

    /**
     * The fully-qualified class name together with the JSON definitions identified by the definition names.
     */
    private final Map<String, Pair<String, JsonObject>> jsonDefinitions = new HashMap<>();
    private final DefinitionNameBuilder definitionNameBuilder = new DefinitionNameBuilder();

    /**
     * All known representation defined in the REST resources
     */
    private final Map<TypeIdentifier, TypeRepresentation> typeRepresentations;

    SchemaBuilder(final Map<TypeIdentifier, TypeRepresentation> typeRepresentations) {
        this.typeRepresentations = typeRepresentations;
    }

    /**
     * Creates the schema object builder for the identifier.
     * The actual definitions are retrieved via {@link SchemaBuilder#getDefinitions} after all types have been declared.
     *
     * @param identifier The identifier
     * @return The schema JSON object builder with the needed properties
     */
    JsonObjectBuilder build(final TypeIdentifier identifier) {
        return this.build(identifier, null);
    }

    /**
     * Creates the schema object builder for the identifier.
     * The actual definitions are retrieved via {@link SchemaBuilder#getDefinitions} after all types have been declared.
     *
     * @param identifier The identifier
     * @param description A description for this field, for example a JavaDoc.
     * @return The schema JSON object builder with the needed properties
     */
    JsonObjectBuilder build(final TypeIdentifier identifier, final String description) {
        final SwaggerType type = toSwaggerType(identifier.getType());
        switch (type) {
            case BOOLEAN:
            case INTEGER:
            case NUMBER:
            case NULL:
            case STRING:
                final JsonObjectBuilder builder = Json.createObjectBuilder();
                addPrimitive(builder, type);
                addDescriptionIfAvailable(builder, description);
                return builder;
        }

        final JsonObjectBuilder builder = Json.createObjectBuilder();

        final TypeRepresentationVisitor visitor = new TypeRepresentationVisitor() {

            private boolean inCollection = false;

            @Override
            public void visit(final TypeRepresentation.ConcreteTypeRepresentation representation) {
                final JsonObjectBuilder nestedBuilder = inCollection ? Json.createObjectBuilder() : builder;
                add(nestedBuilder, representation);

                if (inCollection) {
                    builder.add("items", nestedBuilder.build());
                }
            }

            @Override
            public void visitStart(final TypeRepresentation.CollectionTypeRepresentation representation) {
                builder.add("type", "array");
                inCollection = true;
            }

            @Override
            public void visitEnd(final TypeRepresentation.CollectionTypeRepresentation representation) {
                builder.add("type", "array");
                inCollection = true;
            }

            @Override
            public void visit(final TypeRepresentation.EnumTypeRepresentation representation) {
                builder.add("type", "string");
                if (!representation.getEnumValues().isEmpty()) {
                    final JsonArrayBuilder array = representation.getEnumValues().stream().sorted().collect(Json::createArrayBuilder, JsonArrayBuilder::add, JsonArrayBuilder::add);
                    if (inCollection) {
                        builder.add("items", Json.createObjectBuilder().add("type", "string").add("enum", array).build());
                    } else {
                        builder.add("enum", array);
                    }

                }
            }

        };

        final TypeRepresentation representation = typeRepresentations.get(identifier);
        if (representation == null) {
            builder.add("type", "object");
        }
        else {
            representation.accept(visitor);
        }
        addDescriptionIfAvailable(builder, description);
        return builder;
    }

    /**
     * Returns the stored schema definitions. This has to be called after all calls of {@link SchemaBuilder#build(TypeIdentifier)}.
     *
     * @return The schema JSON definitions
     */
    JsonObject getDefinitions() {
        final JsonObjectBuilder builder = Json.createObjectBuilder();
        jsonDefinitions.entrySet().stream().sorted(mapKeyComparator()).forEach(e -> builder.add(e.getKey(), e.getValue().getRight()));
        return builder.build();
    }

    private void add(final JsonObjectBuilder builder, final TypeRepresentation.ConcreteTypeRepresentation representation) {
        final SwaggerType type = toSwaggerType(representation.getIdentifier().getType());
        switch (type) {
            case BOOLEAN:
            case INTEGER:
            case NUMBER:
            case NULL:
            case STRING:
                addPrimitive(builder, type);
                return;
        }

        addObject(builder, representation, representation.getProperties(), representation.getPropertyDescriptions());
    }

    private void addObject(final JsonObjectBuilder builder, final TypeRepresentation representation, final Map<String, TypeIdentifier> properties,
        final Map<String, String> propertyDescriptions) {

        final TypeIdentifier identifier = representation.getIdentifier();
        final String definition = definitionNameBuilder.buildDefinitionName(identifier.getName(), jsonDefinitions);

        if (jsonDefinitions.containsKey(definition)) {
            builder.add("$ref", "#/definitions/" + definition);
            return;
        }

        // reserve definition
        final JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
        jsonDefinitions.put(definition, Pair.of(identifier.getName(), objectBuilder.build()));

        // if we have a comment/description, include this
        representation.getDescription().ifPresent(desc -> objectBuilder.add("description", desc));

        // build - and include - the inner properties (e.g. the class' fields)
        final JsonObjectBuilder nestedBuilder = Json.createObjectBuilder();
        properties.entrySet().stream().sorted(mapKeyComparator()).forEach(e -> {
            if (propertyDescriptions.containsKey(e.getKey())) {
                nestedBuilder.add(e.getKey(), build(e.getValue(), propertyDescriptions.get(e.getKey())));
            } else {
                nestedBuilder.add(e.getKey(), build(e.getValue()));
            }
        });
        objectBuilder.add("properties", nestedBuilder);

        jsonDefinitions.put(definition, Pair.of(identifier.getName(), objectBuilder.build()));

        builder.add("$ref", "#/definitions/" + definition);
    }

    private void addPrimitive(final JsonObjectBuilder builder, final SwaggerType type) {
        builder.add("type", type.toString());
    }

    private void addDescriptionIfAvailable(JsonObjectBuilder builder, String description) {
        if (description != null && !description.isEmpty()) {
            builder.add("description", description);
        }
    }

    /**
     * Converts the given Java type to the Swagger JSON type.
     *
     * @param type The Java type definition
     * @return The Swagger type
     */
    private static SwaggerType toSwaggerType(final String type) {
        if (INTEGER_TYPES.contains(type))
            return SwaggerType.INTEGER;

        if (DOUBLE_TYPES.contains(type))
            return SwaggerType.NUMBER;

        if (BOOLEAN.equals(type) || PRIMITIVE_BOOLEAN.equals(type))
            return SwaggerType.BOOLEAN;

        if (STRING.equals(type))
            return SwaggerType.STRING;

        return SwaggerType.OBJECT;
    }

    /**
     * Represents the different Swagger types.
     *
     * @author Sebastian Daschner
     */
    private enum SwaggerType {

        ARRAY, BOOLEAN, INTEGER, NULL, NUMBER, OBJECT, STRING;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

}
