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

package com.sebastian_daschner.jaxrs_analyzer.backend;

import com.sebastian_daschner.jaxrs_analyzer.backend.asciidoc.AsciiDocBackendBuilder;
import com.sebastian_daschner.jaxrs_analyzer.backend.plaintext.PlainTextBackendBuilder;
import com.sebastian_daschner.jaxrs_analyzer.backend.swagger.SwaggerBackendBuilder;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.Project;

/**
 * Renders the analyzed JAX-RS resources into a String representation.
 *
 * @author Sebastian Daschner
 */
public interface Backend {

    /**
     * Renders the REST resources of the given project.
     *
     * @param project The project to render including all information and resources
     * @return The String representation
     */
    String render(Project project);

    /**
     * Returns a human readable name of the actual backend.
     */
    String getName();

    /**
     * Creates a builder for Swagger backend.
     */
    static SwaggerBackendBuilder swagger() {
        return new SwaggerBackendBuilder();
    }

    /**
     * Creates a builder for plain text backend.
     */
    static PlainTextBackendBuilder plainText() {
        return new PlainTextBackendBuilder();
    }

    /**
     * Creates a builder for AsciiDoc backend.
     */
    static AsciiDocBackendBuilder asciiDoc() {
        return new AsciiDocBackendBuilder();
    }

    /**
     * Backend builder pattern.
     */
    interface BackendBuilder {

        /**
         * Constructs the actual backend.
         */
        Backend build();
    }

}
