package com.sebastian_daschner.jaxrs_analyzer.backend.swagger;

/**
 * Represents the different Swagger types.
 *
 * @author Sebastian Daschner
 */
public enum SwaggerType {

    ARRAY, BOOLEAN, INTEGER, NULL, NUMBER, OBJECT, STRING;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }

}
