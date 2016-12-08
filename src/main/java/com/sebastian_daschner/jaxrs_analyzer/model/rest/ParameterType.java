package com.sebastian_daschner.jaxrs_analyzer.model.rest;

/**
 * The available parameter types. Needed for identification in {@link MethodParameter}.
 *
 * @author Sebastian Daschner
 */
public enum ParameterType {

    PATH, QUERY, HEADER, FORM, MATRIX, COOKIE

}
