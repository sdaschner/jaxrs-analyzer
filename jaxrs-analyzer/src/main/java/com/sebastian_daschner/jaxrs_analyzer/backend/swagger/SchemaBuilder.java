package com.sebastian_daschner.jaxrs_analyzer.backend.swagger;

import com.sebastian_daschner.jaxrs_analyzer.LogProvider;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeRepresentation;

import javax.json.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Creates Swagger schema type definitions.
 *
 * @author Sebastian Daschner
 */
class SchemaBuilder {

    private final Map<String, JsonObject> jsonDefinitions = new HashMap<>();
    private final AtomicInteger nextDefinition = new AtomicInteger(0);

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
        return build(json);
    }

    /**
     * Returns the stored schema definitions. This has to be called after all calls of {@link SchemaBuilder#build(TypeRepresentation)}.
     *
     * @return The schema JSON definitions
     */
    public JsonObject getDefinitions() {
        final JsonObjectBuilder builder = Json.createObjectBuilder();
        jsonDefinitions.entrySet().forEach(e -> builder.add(e.getKey(), e.getValue()));
        return builder.build();
    }

    private JsonObject build(final JsonValue value) {
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
                return buildForObject((JsonObject) value);
            default:
                LogProvider.getLogger().accept("Unknown Swagger type occurred: " + type);
                return Json.createObjectBuilder().build();
        }
    }

    private JsonObject buildForArray(final JsonArray jsonArray) {
        final JsonObjectBuilder builder = Json.createObjectBuilder().add("type", "array");
        if (!jsonArray.isEmpty())
            // reduces all entries to one optional or an empty optional
            if (jsonArray.size() > 1 && !jsonArray.stream().collect(EqualTester::new, EqualTester::add, EqualTester::add).allEqual())
                builder.add("items", jsonArray.stream().map(this::build).collect(Json::createArrayBuilder, JsonArrayBuilder::add, JsonArrayBuilder::add));
            else
                builder.add("items", build(jsonArray.get(0)));

        return builder.build();
    }

    private JsonObject buildForPrimitive(final SwaggerType type) {
        return Json.createObjectBuilder().add("type", type.toString()).build();
    }

    private JsonObject buildForObject(final JsonObject value) {
        final String definition = nextDefinition();

        final JsonObjectBuilder properties = Json.createObjectBuilder();

        value.entrySet().forEach(e -> properties.add(e.getKey(), build(e.getValue())));
        jsonDefinitions.put(definition, Json.createObjectBuilder().add("properties", properties).build());

        return Json.createObjectBuilder().add("$ref", definition).build();
    }

    private String nextDefinition() {
        return "definition#" + nextDefinition.incrementAndGet();
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
