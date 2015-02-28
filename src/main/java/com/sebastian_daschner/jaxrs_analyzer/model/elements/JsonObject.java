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
