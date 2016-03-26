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
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeIdentifier;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeRepresentation;

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

    /**
     * The type representation storage where all analyzed types have to be added. This will be created by the caller.
     */
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
        return analyzeInternal(jsonValue);
    }

    private TypeIdentifier analyzeInternal(final JsonValue jsonValue) {
        switch (jsonValue.getValueType()) {
            case ARRAY:
                return analyzeInternal((JsonArray) jsonValue);
            case OBJECT:
                return analyzeInternal((JsonObject) jsonValue);
            case STRING:
                return TypeIdentifier.ofType(Types.STRING);
            case NUMBER:
                return TypeIdentifier.ofType(Types.DOUBLE);
            case TRUE:
            case FALSE:
                return TypeIdentifier.ofType(Types.PRIMITIVE_BOOLEAN);
            case NULL:
                return TypeIdentifier.ofType(Types.OBJECT);
            default:
                throw new IllegalArgumentException("Unknown JSON value type provided");
        }
    }

    private TypeIdentifier analyzeInternal(final JsonArray jsonArray) {
        final TypeIdentifier containedIdentifier = jsonArray.isEmpty() ? TypeIdentifier.ofType(Types.OBJECT) : analyzeInternal(jsonArray.get(0));
        final TypeRepresentation containedRepresentation = typeRepresentations.getOrDefault(containedIdentifier, TypeRepresentation.ofConcrete(containedIdentifier));

        final TypeIdentifier existingCollection = findExistingCollection(containedRepresentation);
        if (existingCollection != null) {
            return existingCollection;
        }

        final TypeIdentifier identifier = TypeIdentifier.ofDynamic();
        typeRepresentations.put(identifier, TypeRepresentation.ofCollection(identifier, containedRepresentation));
        return identifier;
    }

    private TypeIdentifier analyzeInternal(final JsonObject jsonObject) {
        final HashMap<String, TypeIdentifier> properties = jsonObject.entrySet().stream()
                .collect(HashMap::new, (m, v) -> m.put(v.getKey(), analyze(v.getValue())), Map::putAll);

        final TypeIdentifier existing = findExistingType(properties);
        if (existing != null)
            return existing;

        final TypeIdentifier identifier = TypeIdentifier.ofDynamic();
        typeRepresentations.put(identifier, TypeRepresentation.ofConcrete(identifier, properties));
        return identifier;
    }

    private TypeIdentifier findExistingCollection(final TypeRepresentation containedRepresentation) {
        return typeRepresentations.entrySet().stream().filter(e -> e.getValue() instanceof TypeRepresentation.CollectionTypeRepresentation)
                .filter(e -> e.getKey().getType().equals(Types.JSON))
                .filter(e -> ((TypeRepresentation.CollectionTypeRepresentation) e.getValue()).contentEquals(containedRepresentation))
                .map(Map.Entry::getKey).findAny().orElse(null);
    }

    private TypeIdentifier findExistingType(final HashMap<String, TypeIdentifier> properties) {
        return typeRepresentations.entrySet().stream().filter(e -> e.getValue() instanceof TypeRepresentation.ConcreteTypeRepresentation)
                .filter(e -> e.getKey().getType().equals(Types.JSON))
                .filter(e -> ((TypeRepresentation.ConcreteTypeRepresentation) e.getValue()).contentEquals(properties))
                .map(Map.Entry::getKey).findAny().orElse(null);
    }

}
