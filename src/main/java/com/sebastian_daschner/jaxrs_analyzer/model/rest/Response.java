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

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a response containing meta information which is sent for a specific status code.
 *
 * @author Sebastian Daschner
 */
public class Response {

    private final Set<String> headers = new HashSet<>();
    private final TypeIdentifier responseBody;
    private final String description;

    public Response() {
        this(null,null);
    }

    public Response(final TypeIdentifier responseBody) {
        this.responseBody = responseBody;
        this.description = null;
    }

    public Response(TypeIdentifier responseBody, String description) {
        this.responseBody = responseBody;
        this.description = description;
    }

    public Set<String> getHeaders() {
        return headers;
    }

    public TypeIdentifier getResponseBody() {
        return responseBody;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Response response = (Response) o;

        if (!headers.equals(response.headers)) return false;
        return !(responseBody != null ? !responseBody.equals(response.responseBody) : response.responseBody != null);
    }

    @Override
    public int hashCode() {
        int result = headers.hashCode();
        result = 31 * result + (responseBody != null ? responseBody.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Response{" +
                "headers=" + headers +
                ", responseBody=" + responseBody +
                '}';
    }

}
