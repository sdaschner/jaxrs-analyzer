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
import java.util.Objects;

/**
 * Represents a request/response body type.
 *
 * @author Sebastian Daschner
 */
public class TypeRepresentation {

    /**
     * The type name (e.g. Java class name).
     */
    private final String type;

    /**
     * The different media type representations (e.g. application/json -> object representation).
     */
    private final Map<String, Object> representations = new HashMap<>();

    public TypeRepresentation(final String type) {
        Objects.requireNonNull(type);
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public Map<String, Object> getRepresentations() {
        return representations;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final TypeRepresentation that = (TypeRepresentation) o;

        if (!type.equals(that.type)) return false;
        return representations.equals(that.representations);
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + representations.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "TypeRepresentation{" +
                "type='" + type + '\'' +
                ", representations=" + representations +
                '}';
    }

}
