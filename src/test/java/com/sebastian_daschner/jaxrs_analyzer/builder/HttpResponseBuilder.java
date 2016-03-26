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

package com.sebastian_daschner.jaxrs_analyzer.builder;

import com.sebastian_daschner.jaxrs_analyzer.model.elements.HttpResponse;
import com.sebastian_daschner.jaxrs_analyzer.model.elements.JsonValue;

import java.util.stream.Stream;

public class HttpResponseBuilder {

    private final HttpResponse httpResponse = new HttpResponse();

    private HttpResponseBuilder() {
        // prevent other instances
    }

    public static HttpResponseBuilder newBuilder() {
        return new HttpResponseBuilder();
    }

    public static HttpResponseBuilder withStatues(final Integer... statues) {
        final HttpResponseBuilder builder = new HttpResponseBuilder();
        Stream.of(statues).forEach(builder.httpResponse.getStatuses()::add);
        return builder;
    }

    public HttpResponseBuilder andInlineEntities(final JsonValue... jsonValues) {
        Stream.of(jsonValues).forEach(httpResponse.getInlineEntities()::add);
        return this;
    }

    public HttpResponseBuilder andHeaders(final String... headers) {
        Stream.of(headers).forEach(httpResponse.getHeaders()::add);
        return this;
    }

    public HttpResponseBuilder andContentTypes(final String... contentTypes) {
        Stream.of(contentTypes).forEach(httpResponse.getContentTypes()::add);
        return this;
    }

    public HttpResponseBuilder andEntityTypes(final String... entityTypes) {
        Stream.of(entityTypes).forEach(httpResponse.getEntityTypes()::add);
        return this;
    }

    public HttpResponse build() {
        return httpResponse;
    }

}

