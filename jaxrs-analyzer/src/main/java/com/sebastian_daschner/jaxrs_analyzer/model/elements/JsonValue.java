package com.sebastian_daschner.jaxrs_analyzer.model.elements;

/**
 * Marker interface for JSON value elements.
 *
 * @author Sebastian Daschner
 */
public interface JsonValue {

    /**
     * Merges the given value to this object.
     *
     * @param jsonValue The value to merge
     * @return This object (needed as BinaryOperator)
     */
    JsonValue merge(JsonValue jsonValue);

}
