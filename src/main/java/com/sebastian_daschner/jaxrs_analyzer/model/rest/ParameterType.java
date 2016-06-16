package com.sebastian_daschner.jaxrs_analyzer.model.rest;

/**
 * The available parameter types. Needed for identification in {@link MethodParameters}.
 *
 * @author Sebastian Daschner
 */
public enum ParameterType {

    QUERY, PATH, HEADER, FORM, MATRIX, COOKIE

}
