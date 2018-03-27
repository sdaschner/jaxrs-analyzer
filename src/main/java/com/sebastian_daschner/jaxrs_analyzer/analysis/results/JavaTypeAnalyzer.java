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

import static com.sebastian_daschner.jaxrs_analyzer.model.JavaUtils.isAssignableTo;
import static com.sebastian_daschner.jaxrs_analyzer.model.JavaUtils.loadClassFromType;
import static com.sebastian_daschner.jaxrs_analyzer.model.Types.COLLECTION;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import com.sebastian_daschner.jaxrs_analyzer.model.Types;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeIdentifier;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeRepresentation;

/**
 * Analyzes a class (usually a POJO) for it's properties and methods.
 * The analysis is used to derive the JSON/XML representations.
 *
 * @author Sebastian Daschner
 */
class JavaTypeAnalyzer {

    /**
     * The type representation storage where all analyzed types have to be added. This will be created by the caller.
     */
    private final Map<TypeIdentifier, TypeRepresentation> typeRepresentations;
    private final Set<String> analyzedTypes;
    private final NormalizedTypeAnalyzerFactory normalizedTypeAnalyzerFactory;
    
    JavaTypeAnalyzer(final Map<TypeIdentifier, TypeRepresentation> typeRepresentations, NormalizedTypeAnalyzerFactory normalizedTypeAnalyzerFactory) {
        this.typeRepresentations = typeRepresentations;
        this.normalizedTypeAnalyzerFactory = normalizedTypeAnalyzerFactory;
        analyzedTypes = new HashSet<>();
    }

    /**
     * Analyzes the given type. Resolves known generics and creates a representation of the contained class, all contained properties
     * and nested types recursively.
     *
     * @param rootType The type to analyze
     * @return The (root) type identifier
     */
    // TODO consider arrays
    TypeIdentifier analyze(final String rootType) {
        final String type = ResponseTypeNormalizer.normalizeResponseWrapper(rootType);
        final TypeIdentifier identifier = TypeIdentifier.ofType(type);

        if (!analyzedTypes.contains(type) && (isAssignableTo(type, COLLECTION) || !isJDKType(type))) {
            analyzedTypes.add(type);
            typeRepresentations.put(identifier, analyzeInternal(identifier, type));
        }

        return identifier;
    }

    static boolean isJDKType(final String type) {
        // exclude java, javax, etc. packages
        return Types.PRIMITIVE_TYPES.contains(type) || type.startsWith("Ljava/") || type.startsWith("Ljavax/");
    }

    
    private TypeRepresentation analyzeInternal(final TypeIdentifier identifier, final String type) {
        if (isAssignableTo(type, COLLECTION)) {
            final String containedType = ResponseTypeNormalizer.normalizeCollection(type);
            return TypeRepresentation.ofCollection(identifier, analyzeInternal(TypeIdentifier.ofType(containedType), containedType));
        }

        final Class<?> loadedClass = loadClassFromType(type);
        if (loadedClass != null && loadedClass.isEnum())
            return TypeRepresentation.ofEnum(identifier, Stream.of(loadedClass.getEnumConstants()).map(o -> (Enum<?>) o).map(Enum::name).toArray(String[]::new));
        return TypeRepresentation.ofConcrete(identifier, normalizedTypeAnalyzerFactory.create(this).analyzeClass(type, loadedClass));
    }


}
