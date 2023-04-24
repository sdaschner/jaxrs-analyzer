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

package com.sebastian_daschner.jaxrs_analyzer.analysis.results;

import com.sebastian_daschner.jaxrs_analyzer.model.Types;

import javax.ws.rs.core.GenericEntity;
import java.util.List;

import static com.sebastian_daschner.jaxrs_analyzer.model.JavaUtils.*;

/**
 * Normalizes the request/response body Java types.
 *
 * @author Sebastian Daschner
 */
final class ResponseTypeNormalizer {

    private ResponseTypeNormalizer() {
        throw new UnsupportedOperationException();
    }

    /**
     * Normalizes the contained collection type.
     *
     * @param type The type
     * @return The normalized type
     */
    static String normalizeWrapper(final String type) {
        if (isAssignableTo(type, Types.COLLECTION)
                || isAssignableTo(type, Types.ITERABLE)
                || isAssignableTo(type, Types.OPTIONAL)
                || isAssignableTo(type, Types.STREAM)
                || isAssignableTo(type, Types.STREAMING)
                || isArray(type)) {
            List<String> typeParameters = getTypeParameters(type);
            if (!typeParameters.isEmpty()) {
                return typeParameters.get(0);
            }
            return Types.OBJECT;
        }
        return type;
    }

    /**
     * Normalizes the body type (e.g. removes nested {@link GenericEntity}s).
     *
     * @param type The type
     * @return The normalized type
     */
    static String normalizeResponseWrapper(final String type) {
        if (isAssignableTo(type, Types.GENERIC_ENTITY)) {
            List<String> typeParameters = getTypeParameters(type);
            if (!typeParameters.isEmpty()) {
                return typeParameters.get(0);
            }
        }
        return type;
    }

}
