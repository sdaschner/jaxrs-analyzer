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


import java.util.HashMap;
import java.util.Map;

/**
 * Represents REST resource method parameters.
 *
 * @author Sebastian Daschner
 */
public class MethodParameters {

    /*
     * The params contain the parameter names -> Java types
     */
    private final Map<String, String> matrixParams = new HashMap<>();
    private final Map<String, String> queryParams = new HashMap<>();
    private final Map<String, String> pathParams = new HashMap<>();
    private final Map<String, String> cookieParams = new HashMap<>();
    private final Map<String, String> headerParams = new HashMap<>();
    private final Map<String, String> formParams = new HashMap<>();

    public Map<String, String> getMatrixParams() {
        return matrixParams;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public Map<String, String> getPathParams() {
        return pathParams;
    }

    public Map<String, String> getCookieParams() {
        return cookieParams;
    }

    public Map<String, String> getHeaderParams() {
        return headerParams;
    }

    public Map<String, String> getFormParams() {
        return formParams;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final MethodParameters that = (MethodParameters) o;

        if (!cookieParams.equals(that.cookieParams)) return false;
        if (!formParams.equals(that.formParams)) return false;
        if (!headerParams.equals(that.headerParams)) return false;
        if (!matrixParams.equals(that.matrixParams)) return false;
        if (!pathParams.equals(that.pathParams)) return false;
        return queryParams.equals(that.queryParams);
    }

    @Override
    public int hashCode() {
        int result = matrixParams.hashCode();
        result = 31 * result + queryParams.hashCode();
        result = 31 * result + pathParams.hashCode();
        result = 31 * result + cookieParams.hashCode();
        result = 31 * result + headerParams.hashCode();
        result = 31 * result + formParams.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "MethodParameters{" +
                "matrixParams=" + matrixParams +
                ", queryParams=" + queryParams +
                ", pathParams=" + pathParams +
                ", cookieParams=" + cookieParams +
                ", headerParams=" + headerParams +
                ", formParams=" + formParams +
                '}';
    }

}
