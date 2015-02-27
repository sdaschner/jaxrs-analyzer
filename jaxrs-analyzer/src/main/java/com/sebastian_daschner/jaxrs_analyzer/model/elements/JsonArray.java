package com.sebastian_daschner.jaxrs_analyzer.model.elements;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents a JSON array element.
 *
 * @author Sebastian Daschner
 */
public class JsonArray implements JsonValue {

    private final List<Element> elements = new LinkedList<>();

    @Override
    public JsonValue merge(final JsonValue element) {
        elements.addAll(((JsonArray) element).elements);
        return this;
    }

    public List<Element> getElements() {
        return elements;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final JsonArray jsonArray = (JsonArray) o;

        return elements.equals(jsonArray.elements);
    }

    @Override
    public int hashCode() {
        return elements.hashCode();
    }

    @Override
    public String toString() {
        return "JsonArray{" +
                "elements=" + elements +
                '}';
    }

}
