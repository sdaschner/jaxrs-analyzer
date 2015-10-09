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

import com.sebastian_daschner.jaxrs_analyzer.LogProvider;
import com.sebastian_daschner.jaxrs_analyzer.model.Pair;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeRepresentation;

import javax.json.*;
import java.util.HashMap;
import java.util.Map;

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
     * Creates the schema object for the representation. Stores the definition for later use for more complex objects.
     * The definitions are retrieved via {@link SchemaBuilder#getDefinitions} after all types have been built.
     *
     * @param representation The representation
     * @return The schema JSON object
     */
    JsonObject build(final TypeRepresentation representation) {
        // TODO support XML as well

        final SwaggerType type = SwaggerUtils.toSwaggerType(representation.getType());
        switch (type) {
            case BOOLEAN:
            case INTEGER:
            case NUMBER:
            case NULL:
            case STRING:
                return buildForPrimitive(type);
        }

        if (representation.getRepresentations().isEmpty())
            return Json.createObjectBuilder().build();

        final JsonValue json = (JsonValue) representation.getRepresentations().values().iterator().next();
        return build(json, representation.getType().getCtClass().getName());
    }

    /**
     * Returns the stored schema definitions. This has to be called after all calls of {@link SchemaBuilder#build(TypeRepresentation)}.
     *
     * @return The schema JSON definitions
     */
    JsonObject getDefinitions() {
        final JsonObjectBuilder builder = Json.createObjectBuilder();
        jsonDefinitions.entrySet().forEach(e -> builder.add(e.getKey(), e.getValue().getRight()));
        return builder.build();
    }

    private JsonObject build(final JsonValue value, final String typeName) {
        final SwaggerType type = SwaggerUtils.toSwaggerType(value.getValueType());
        switch (type) {
            case ARRAY:
                return buildForArray((JsonArray) value);
            case BOOLEAN:
            case INTEGER:
            case NUMBER:
            case NULL:
            case STRING:
                return buildForPrimitive(type);
            case OBJECT:
                return buildForObject((JsonObject) value, typeName);
            default:
                LogProvider.error("Unknown Swagger type occurred: " + type);
                return Json.createObjectBuilder().build();
        }
    }

    private JsonObject buildForArray(final JsonArray jsonArray) {
        final JsonObjectBuilder builder = Json.createObjectBuilder().add("type", "array");
        if (!jsonArray.isEmpty())
            // reduces all entries to one optional or an empty optional
            if (jsonArray.size() > 1 && !jsonArray.stream().collect(EqualTester::new, EqualTester::add, EqualTester::add).allEqual())
                builder.add("items", jsonArray.stream().map(v -> build(v, null)).collect(Json::createArrayBuilder, JsonArrayBuilder::add, JsonArrayBuilder::add));
            else
                builder.add("items", build(jsonArray.get(0), null));

        return builder.build();
    }

    private JsonObject buildForPrimitive(final SwaggerType type) {
        return Json.createObjectBuilder().add("type", type.toString()).build();
    }

    private JsonObject buildForObject(final JsonObject value, final String typeName) {
        final String definition = buildDefinition(typeName);

        if (jsonDefinitions.containsKey(definition))
            return Json.createObjectBuilder().add("$ref", "#/definitions/" + definition).build();

        final JsonObjectBuilder properties = Json.createObjectBuilder();

        value.entrySet().forEach(e -> properties.add(e.getKey(), build(e.getValue(), null)));
        jsonDefinitions.put(definition, Pair.of(typeName, Json.createObjectBuilder().add("properties", properties).build()));

        return Json.createObjectBuilder().add("$ref", "#/definitions/" + definition).build();
    }

    private String buildDefinition(final String typeName) {
        final String type = typeName == null ? "NestedType" : typeName;
        final String definition = type.substring(type.lastIndexOf('.') + 1);

        final Pair<String, JsonObject> containedEntry = jsonDefinitions.get(definition);
        if (containedEntry == null || containedEntry.getLeft() != null && containedEntry.getLeft().equals(type))
            return definition;

        if (!definition.matches("_\\d+$"))
            return definition + "_2";

        final int separatorIndex = definition.lastIndexOf('_');
        final int index = Integer.parseInt(definition.substring(separatorIndex + 1));
        return definition.substring(0, separatorIndex + 1) + (index + 1);
    }

    /**
     * Used as Stream collection to test, if all objects are equal.
     */
    private class EqualTester {

        private JsonValue first;
        private boolean equal = true;

        public void add(final JsonValue value) {
            if (first == null)
                first = value;
            else
                equal = equal && first.equals(value);
        }

        public void add(final EqualTester other) {
            if (first != null && other.first != null)
                equal = equal && other.equal && first.equals(other.first);
        }

        public boolean allEqual() {
            return equal;
        }
    }
}
