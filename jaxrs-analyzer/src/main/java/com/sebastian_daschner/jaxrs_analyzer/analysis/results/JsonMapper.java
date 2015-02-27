package com.sebastian_daschner.jaxrs_analyzer.analysis.results;

import com.sebastian_daschner.jaxrs_analyzer.model.elements.JsonArray;
import com.sebastian_daschner.jaxrs_analyzer.model.elements.JsonValue;
import com.sebastian_daschner.jaxrs_analyzer.model.elements.Element;
import com.sebastian_daschner.jaxrs_analyzer.model.elements.JsonObject;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import java.util.stream.Stream;

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
        switch (value.getType()) {
            case "java.lang.String":
                builder.add("string");
                break;
            case "java.lang.Integer":
            case "int":
            case "java.math.BigInteger":
            case "java.math.BigDecimal":
            case "java.lang.Long":
            case "long":
                builder.add(0);
                break;
            case "java.lang.Double":
            case "double":
                builder.add(0.0);
                break;
            case "java.lang.Boolean":
            case "boolean":
                builder.add(false);
                break;
            case "javax.json.JsonValue":
            case "javax.json.JsonArray":
            case "javax.json.JsonObject":
                value.getPossibleValues().stream().filter(v -> v instanceof JsonValue).findFirst().ifPresent(v -> builder.add(map((JsonValue) v)));
                break;
            default:
                builder.addNull();
        }
    }

    private static javax.json.JsonObject map(final JsonObject jsonObject) {
        final JsonObjectBuilder builder = Json.createObjectBuilder();
        jsonObject.getStructure().entrySet().stream().forEach(e -> addToObject(builder, e.getKey(), e.getValue()));
        return builder.build();
    }

    private static void addToObject(final JsonObjectBuilder builder, final String key, final Element value) {
        // handle nested JSON
        final String jsonTypes[] = {"javax.json.JsonValue", "javax.json.JsonArray", "javax.json.JsonObject"};
        if (Stream.of(jsonTypes).anyMatch(t -> t.equals(value.getType()))) {
            value.getPossibleValues().stream().filter(v -> v instanceof JsonValue).findFirst().ifPresent(v -> builder.add(key, map((JsonValue) v)));
            return;
        }

        addToObject(builder, key, value.getType());
    }

    /**
     * Adds a value to the JSON builder under the given key, depending on the type of the value.
     *
     * @param builder The JSON builder
     * @param key     The key of the field
     * @param type    The type to add
     */
    static void addToObject(final JsonObjectBuilder builder, final String key, final String type) {
        switch (type) {
            case "java.lang.String":
                builder.add(key, "string");
                break;
            case "java.lang.Integer":
            case "int":
            case "java.lang.Long":
            case "long":
            case "java.math.BigInteger":
                builder.add(key, 0);
                break;
            case "java.lang.Double":
            case "double":
            case "java.math.BigDecimal":
                builder.add(key, 0.0);
                break;
            case "java.lang.Boolean":
            case "boolean":
                builder.add(key, false);
                break;
            default:
                builder.addNull(key);
        }
    }

}
