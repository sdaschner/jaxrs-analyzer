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

import com.sebastian_daschner.jaxrs_analyzer.analysis.utils.JavaUtils;

import javax.ws.rs.core.GenericEntity;

/**
 * Normalizes the request/response body Java types.
 *
 * @author Sebastian Daschner
 */
final class ResponseTypeNormalizer {

    private final static String GENERIC_ENTITY = GenericEntity.class.getName();
    private ResponseTypeNormalizer() {
        throw new UnsupportedOperationException();
    }

    /**
     * Normalizes all known nested types.
     *
     * @param type The type
     * @return The fully normalized type
     */
    public static String normalize(final String type) {
        String currentType = type;
        String lastType;
        do {
            lastType = currentType;
            currentType = JavaUtils.trimCollection(currentType);
            currentType = normalizeWrapper(currentType);
        } while (!lastType.equals(currentType));

        return currentType;
    }

    /**
     * Normalizes the body type (e.g. removes nested {@link GenericEntity}s).
     *
     * @param type The type
     * @return The normalized type
     */
    public static String normalizeWrapper(final String type) {
        if (type.startsWith(GENERIC_ENTITY + '<'))
            return type.substring(GENERIC_ENTITY.length() + 1, type.length() - 1);

        return type;
    }



}
