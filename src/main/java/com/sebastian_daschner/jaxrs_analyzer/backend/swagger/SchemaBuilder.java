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

import com.sebastian_daschner.jaxrs_analyzer.utils.Pair;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeIdentifier;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeRepresentation;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeRepresentationVisitor;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.HashMap;
import java.util.Map;

import static com.sebastian_daschner.jaxrs_analyzer.backend.ComparatorUtils.mapKeyComparator;

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

    /**
     * All known representation defined in the REST resources
     */
    private final Map<TypeIdentifier, TypeRepresentation> typeRepresentations;

    SchemaBuilder(final Map<TypeIdentifier, TypeRepresentation> typeRepresentations) {
        this.typeRepresentations = typeRepresentations;
    }

    /**
     * Creates the schema object for the identifier.
     * The actual definitions are retrieved via {@link SchemaBuilder#getDefinitions} after all types have been declared.
     *
     * @param identifier The identifier
     * @return The schema JSON object
     */
    JsonObject build(final TypeIdentifier identifier) {
        final SwaggerType type = SwaggerUtils.toSwaggerType(identifier.getType());
        switch (type) {
            case BOOLEAN:
            case INTEGER:
            case NUMBER:
            case NULL:
            case STRING:
                final JsonObjectBuilder builder = Json.createObjectBuilder();
                addPrimitive(builder, type);
                return builder.build();
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
            public void visit(final TypeRepresentation.CollectionTypeRepresentation representation) {
                builder.add("type", "array");
                inCollection = true;
            }

        };

        final TypeRepresentation representation = typeRepresentations.get(identifier);
        if (representation == null)
            builder.add("type", "object");
        else
            representation.accept(visitor);
        return builder.build();
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
        final SwaggerType type = SwaggerUtils.toSwaggerType(representation.getIdentifier().getType());
        switch (type) {
            case BOOLEAN:
            case INTEGER:
            case NUMBER:
            case NULL:
            case STRING:
                addPrimitive(builder, type);
                return;
        }

        addObject(builder, representation.getIdentifier(), representation.getProperties());
    }

    private void addObject(final JsonObjectBuilder builder, final TypeIdentifier identifier, final Map<String, TypeIdentifier> properties) {
        final String definition = buildDefinition(identifier.getName());

        if (jsonDefinitions.containsKey(definition)) {
            builder.add("$ref", "#/definitions/" + definition).build();
            return;
        }

        // reserve definition
        jsonDefinitions.put(definition, Pair.of(identifier.getName(), Json.createObjectBuilder().build()));

        final JsonObjectBuilder nestedBuilder = Json.createObjectBuilder();

        properties.entrySet().stream().sorted(mapKeyComparator()).forEach(e -> nestedBuilder.add(e.getKey(), build(e.getValue())));
        jsonDefinitions.put(definition, Pair.of(identifier.getName(), Json.createObjectBuilder().add("properties", nestedBuilder).build()));

        builder.add("$ref", "#/definitions/" + definition).build();
    }

    private void addPrimitive(final JsonObjectBuilder builder, final SwaggerType type) {
        builder.add("type", type.toString()).build();
    }

    private String buildDefinition(final String typeName) {
        final String definition = typeName.startsWith(TypeIdentifier.DYNAMIC_TYPE_PREFIX) ? "JsonObject" : typeName.substring(typeName.lastIndexOf('.') + 1);

        final Pair<String, JsonObject> containedEntry = jsonDefinitions.get(definition);
        if (containedEntry == null || containedEntry.getLeft() != null && containedEntry.getLeft().equals(typeName))
            return definition;

        if (!definition.matches("_\\d+$"))
            return definition + "_2";

        final int separatorIndex = definition.lastIndexOf('_');
        final int index = Integer.parseInt(definition.substring(separatorIndex + 1));
        return definition.substring(0, separatorIndex + 1) + (index + 1);
    }

}
