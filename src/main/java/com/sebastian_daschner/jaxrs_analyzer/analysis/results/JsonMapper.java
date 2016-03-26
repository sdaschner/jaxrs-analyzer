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

package com.sebastian_daschner.jaxrs_analyzer.analysis.results;

import com.sebastian_daschner.jaxrs_analyzer.model.elements.Element;
import com.sebastian_daschner.jaxrs_analyzer.model.elements.JsonArray;
import com.sebastian_daschner.jaxrs_analyzer.model.elements.JsonObject;
import com.sebastian_daschner.jaxrs_analyzer.model.elements.JsonValue;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import java.util.Set;
import java.util.function.Function;

import static com.sebastian_daschner.jaxrs_analyzer.model.Types.*;

/**
 * Creates JSON-P Json objects from the internal {@link JsonValue}s and maps JSON types.
 * The possible values of the {@link Element}s get lost.
 *
 * @author Sebastian Daschner
 */
final class JsonMapper {

    private JsonMapper() {
        throw new UnsupportedOperationException();
    }

    /**
     * Creates a JSON-P JsonValue from the internal {@link JsonValue}.
     *
     * @param jsonValue The JSON value to map
     * @return A JSON-P value
     */
    static javax.json.JsonValue map(final JsonValue jsonValue) {
        if (jsonValue instanceof JsonArray)
            return map((JsonArray) jsonValue);
        return map((JsonObject) jsonValue);
    }

    private static javax.json.JsonArray map(final JsonArray jsonArray) {
        final JsonArrayBuilder builder = Json.createArrayBuilder();
        jsonArray.getElements().stream().forEach(e -> addToArray(builder, e));
        return builder.build();
    }

    private static void addToArray(final JsonArrayBuilder builder, final Element value) {
        if (value.getTypes().contains(STRING))
            builder.add("string");

        if (value.getTypes().stream().anyMatch(INTEGER_TYPES::contains))
            builder.add(0);

        if (value.getTypes().stream().anyMatch(DOUBLE_TYPES::contains))
            builder.add(0.0);

        if (value.getTypes().contains(BOOLEAN) || value.getTypes().contains(PRIMITIVE_BOOLEAN))
            builder.add(false);

        if (value.getTypes().stream().anyMatch(JSON_TYPES::contains))
            value.getPossibleValues().stream().filter(v -> v instanceof JsonValue).findFirst().ifPresent(v -> builder.add(map((JsonValue) v)));
    }

    private static javax.json.JsonObject map(final JsonObject jsonObject) {
        final JsonObjectBuilder builder = Json.createObjectBuilder();
        jsonObject.getStructure().entrySet().stream().forEach(e -> addToObject(builder, e.getKey(), e.getValue()));
        return builder.build();
    }

    private static void addToObject(final JsonObjectBuilder builder, final String key, final Element value) {
        // handle nested JSON
        if (value.getTypes().stream().anyMatch(JSON_TYPES::contains)) {
            value.getPossibleValues().stream().filter(v -> v instanceof JsonValue).findFirst().ifPresent(v -> builder.add(key, map((JsonValue) v)));
            return;
        }

        addToObject(builder, key, value.getTypes());
    }

    private static void addToObject(final JsonObjectBuilder builder, final String key, final Set<String> types) {
        if (types.contains(STRING))
            builder.add(key, "string");

        if (types.stream().anyMatch(INTEGER_TYPES::contains))
            builder.add(key, 0);

        if (types.stream().anyMatch(DOUBLE_TYPES::contains))
            builder.add(key, 0.0);

        if (types.contains(BOOLEAN) || types.contains(PRIMITIVE_BOOLEAN))
            builder.add(key, false);
    }

    // TODO remove unused code, refactor & test types (e.g. Date, JSR-310)
    static void addToObject(final JsonObjectBuilder builder, final String key, final String type, final Function<String, javax.json.JsonValue> defaultBehavior) {
        if (STRING.equals(type)) {
            builder.add(key, "string");
            return;
        }

        if (BOOLEAN.equals(type) || PRIMITIVE_BOOLEAN.equals(type)) {
            builder.add(key, false);
            return;
        }

        // TODO
//        if (INTEGER_TYPES.contains(type)) {
//            builder.add(key, 0);
//            return;
//        }
//
//        if (DOUBLE_TYPES.contains(type)) {
//            builder.add(key, 0.0);
//            return;
//        }
//
//        // plain-old date and JSR-310
//        if (type.isAssignableTo(DATE) || type.isAssignableTo(TEMPORAL_ACCESSOR)) {
//            builder.add(key, "date");
//            return;
//        }
//
//        if (type.isAssignableTo(MAP)) {
//            builder.add(key, Json.createObjectBuilder().build());
//            return;
//        }

        // fall-back
        builder.add(key, defaultBehavior.apply(type));
    }

    static void addToArray(final JsonArrayBuilder builder, final String type, final Function<String, javax.json.JsonValue> defaultBehavior) {
        if (STRING.equals(type)) {
            builder.add("string");
            return;
        }

        if (BOOLEAN.equals(type) || PRIMITIVE_BOOLEAN.equals(type)) {
            builder.add(false);
            return;
        }

//        if (INTEGER_TYPES.contains(type)) {
//            builder.add(0);
//            return;
//        }
//
//        if (DOUBLE_TYPES.contains(type)) {
//            builder.add(0.0);
//            return;
//        }

        builder.add(defaultBehavior.apply(type));
    }

}
