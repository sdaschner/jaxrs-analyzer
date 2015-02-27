package com.sebastian_daschner.jaxrs_analyzer.model.elements;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a JSON object element.
 *
 * @author Sebastian Daschner
 */
public class JsonObject implements JsonValue {

    private final Map<String, Element> structure = new HashMap<>();

    @Override
    public JsonValue merge(final JsonValue element) {
        structure.putAll(((JsonObject) element).structure);
        return this;
    }

    public Map<String, Element> getStructure() {
        return structure;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final JsonObject that = (JsonObject) o;

        return structure.equals(that.structure);
    }

    @Override
    public int hashCode() {
        return structure.hashCode();
    }

    @Override
    public String toString() {
        return "JsonObject{" +
                "structure=" + structure +
                '}';
    }

}
