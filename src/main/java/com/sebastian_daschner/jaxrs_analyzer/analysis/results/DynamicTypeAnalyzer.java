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

import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeIdentifier;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeRepresentation;
import com.sebastian_daschner.jaxrs_analyzer.model.types.Types;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import java.util.HashMap;
import java.util.Map;

/**
 * Analyzes {@code JsonValue}s to derive the actual JSON representations.
 * Equal JSON structures (i.e. objects or arrays) will result in the same dynamically identified representation.
 *
 * @author Sebastian Daschner
 */
class DynamicTypeAnalyzer {

    private final Map<TypeIdentifier, TypeRepresentation> typeRepresentations;

    DynamicTypeAnalyzer(final Map<TypeIdentifier, TypeRepresentation> typeRepresentations) {
        this.typeRepresentations = typeRepresentations;
    }

    /**
     * Analyzes the given JSON value.
     *
     * @param jsonValue The JSON value to analyze
     * @return The type identifier
     */
    TypeIdentifier analyze(final JsonValue jsonValue) {
        return analyzeInternal(jsonValue).getIdentifier();
    }

    private TypeRepresentation analyzeInternal(final JsonValue jsonValue) {

        switch (jsonValue.getValueType()) {
            case ARRAY:
                return analyzeInternal((JsonArray) jsonValue);
            case OBJECT:
                return analyzeInternal((JsonObject) jsonValue);
            case STRING:
                return TypeRepresentation.ofConcrete(TypeIdentifier.ofType(Types.STRING));
            case NUMBER:
                return TypeRepresentation.ofConcrete(TypeIdentifier.ofType(Types.DOUBLE));
            case TRUE:
            case FALSE:
                return TypeRepresentation.ofConcrete(TypeIdentifier.ofType(Types.PRIMITIVE_BOOLEAN));
            case NULL:
                return TypeRepresentation.ofConcrete(TypeIdentifier.ofType(Types.OBJECT));
            default:
                throw new IllegalArgumentException("Unknown JSON value type provided");
        }
    }

    private TypeRepresentation analyzeInternal(final JsonArray jsonArray) {
        final TypeRepresentation containedRepresentation = jsonArray.isEmpty() ? TypeRepresentation.ofConcrete(TypeIdentifier.ofType(Types.OBJECT))
                : analyzeInternal(jsonArray.get(0));

        final TypeIdentifier identifier = TypeIdentifier.ofDynamic();
        final TypeRepresentation representation = TypeRepresentation.ofCollection(identifier, containedRepresentation);
        typeRepresentations.put(identifier, representation);

        return representation;
    }

    private TypeRepresentation analyzeInternal(final JsonObject jsonObject) {
        final TypeIdentifier identifier = TypeIdentifier.ofDynamic();
        final TypeRepresentation representation = TypeRepresentation.ofConcrete(identifier, jsonObject.entrySet().stream()
                .collect(HashMap::new, (m, v) -> m.put(v.getKey(), analyze(v.getValue())), Map::putAll));

        typeRepresentations.put(identifier, representation);

        return representation;
    }

}
