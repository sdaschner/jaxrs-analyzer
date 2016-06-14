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

package com.sebastian_daschner.jaxrs_analyzer.model.rest;


import com.sebastian_daschner.jaxrs_analyzer.model.Types;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents REST resource method parameters.
 *
 * @author Sebastian Daschner
 */
public class MethodParameters {

    /*
     * The params contain the field name or parameter index -> MethodParameter
     */
    private final Map<Object, MethodParameter> parameters = new HashMap<>();

    /**
     * Adds all the given parameters to the various parameters of this instance.
     * Replaces previous identical parameter names.
     */
    public void merge(final MethodParameters methodParameters) {
        parameters.putAll(methodParameters.parameters);
    }

    public Stream<MethodParameter> getParamsByAnnotation(final String type) {
        return this.parameters.entrySet().stream()
            .filter(e -> e.getValue().getAnnotation().equals(type))
            .map(e -> e.getValue());
    }

    public Map<String, MethodParameter> getMatrixParams() {
        return this.getParamsByAnnotation(Types.MATRIX_PARAM)
            .collect(Collectors.toMap(
                p -> p.getValue(),
                p -> p
            ));
    }

    public Map<String, MethodParameter> getQueryParams() {
        return this.getParamsByAnnotation(Types.QUERY_PARAM)
            .collect(Collectors.toMap(
                p -> p.getValue(),
                p -> p
            ));
    }

    public Map<String, MethodParameter> getPathParams() {
        return this.getParamsByAnnotation(Types.PATH_PARAM)
            .collect(Collectors.toMap(
                p -> p.getValue(),
                p -> p
            ));
    }

    public Map<String, MethodParameter> getCookieParams() {
        return this.getParamsByAnnotation(Types.COOKIE_PARAM)
            .collect(Collectors.toMap(
                p -> p.getValue(),
                p -> p
            ));
    }

    public Map<String, MethodParameter> getHeaderParams() {
        return this.getParamsByAnnotation(Types.HEADER_PARAM)
            .collect(Collectors.toMap(
                p -> p.getValue(),
                p -> p
            ));
    }

    public Map<String, MethodParameter> getFormParams() {
        return this.getParamsByAnnotation(Types.FORM_PARAM)
            .collect(Collectors.toMap(
                p -> p.getValue(),
                p -> p
            ));
    }

    public MethodParameter getParameter(final Object index) {
        return parameters.get(index);
    }

    public void setParameter(final Object index, final MethodParameter parameter) {
        parameters.put(index, parameter);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final MethodParameters that = (MethodParameters) o;

        if (!getCookieParams().equals(that.getCookieParams())) return false;
        if (!getFormParams().equals(that.getFormParams())) return false;
        if (!getHeaderParams().equals(that.getHeaderParams())) return false;
        if (!getMatrixParams().equals(that.getMatrixParams())) return false;
        if (!getPathParams().equals(that.getPathParams())) return false;
        return getQueryParams().equals(that.getQueryParams());
    }

    @Override
    public int hashCode() {
        int result = getMatrixParams().hashCode();
        result = 31 * result + getQueryParams().hashCode();
        result = 31 * result + getPathParams().hashCode();
        result = 31 * result + getCookieParams().hashCode();
        result = 31 * result + getHeaderParams().hashCode();
        result = 31 * result + getFormParams().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "MethodParameters{" +
            "matrixParams=" + getMatrixParams()+
            ", queryParams=" + getQueryParams()+
            ", pathParams=" + getPathParams()+
            ", cookieParams=" + getCookieParams()+
            ", headerParams=" + getHeaderParams()+
            ", formParams=" + getFormParams()+
            '}';
    }
}
