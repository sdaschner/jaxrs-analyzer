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

import java.util.HashSet;
import java.util.Set;

/**
 * @author Sebastian Daschner
 */
public class HttpResponse {

    private final Set<Integer> statuses = new HashSet<>();
    private final Set<String> headers = new HashSet<>();
    private final Set<String> contentTypes = new HashSet<>();
    private final Set<String> entityTypes = new HashSet<>();
    private final Set<JsonValue> inlineEntities = new HashSet<>();

    public Set<Integer> getStatuses() {
        return statuses;
    }

    public Set<String> getHeaders() {
        return headers;
    }

    public Set<String> getContentTypes() {
        return contentTypes;
    }

    public Set<String> getEntityTypes() {
        return entityTypes;
    }

    public Set<JsonValue> getInlineEntities() {
        return inlineEntities;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final HttpResponse that = (HttpResponse) o;

        if (!statuses.equals(that.statuses)) return false;
        if (!headers.equals(that.headers)) return false;
        if (!contentTypes.equals(that.contentTypes)) return false;
        if (!entityTypes.equals(that.entityTypes)) return false;
        return inlineEntities.equals(that.inlineEntities);
    }

    @Override
    public int hashCode() {
        int result = statuses.hashCode();
        result = 31 * result + headers.hashCode();
        result = 31 * result + contentTypes.hashCode();
        result = 31 * result + entityTypes.hashCode();
        result = 31 * result + inlineEntities.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "HttpResponse{" +
                "statuses=" + statuses +
                ", headers=" + headers +
                ", contentTypes=" + contentTypes +
                ", entityTypes=" + entityTypes +
                ", inlineEntities=" + inlineEntities +
                '}';
    }

}
