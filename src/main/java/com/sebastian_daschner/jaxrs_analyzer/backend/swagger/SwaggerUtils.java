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

package com.sebastian_daschner.jaxrs_analyzer.backend.swagger;

import javax.json.JsonValue;

import static com.sebastian_daschner.jaxrs_analyzer.backend.swagger.SwaggerType.*;

/**
 * Contains Swagger JSON type functionality.
 *
 * @author Sebastian Daschner
 */
final class SwaggerUtils {

    private SwaggerUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * Converts the given Java type to the Swagger JSON type.
     *
     * @param type The Java type definition
     * @return The Swagger type
     */
    static SwaggerType toSwaggerType(final String type) {
        switch (type) {
            case "java.lang.Integer":
            case "int":
            case "java.lang.Long":
            case "long":
                return INTEGER;
            case "java.lang.Double":
            case "double":
            case "java.lang.Float":
            case "float":
                return NUMBER;
            case "java.lang.Boolean":
            case "boolean":
                return BOOLEAN;
            case "java.lang.String":
                return STRING;
        }
        if (type.contains("["))
            return ARRAY;
        return OBJECT;
    }

    /**
     * Converts the given JSON value type to the Swagger JSON type.
     *
     * @param type The JSON value type
     * @return The Swagger type
     */
    static SwaggerType toSwaggerType(final JsonValue.ValueType type) {
        switch (type) {
            case ARRAY:
                return ARRAY;
            case OBJECT:
                return OBJECT;
            case STRING:
                return STRING;
            case NUMBER:
                return NUMBER;
            case TRUE:
            case FALSE:
                return BOOLEAN;
            case NULL:
            default:
                return NULL;
        }
    }

}
