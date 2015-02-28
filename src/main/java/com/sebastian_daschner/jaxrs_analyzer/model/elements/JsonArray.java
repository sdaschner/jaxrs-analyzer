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
