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

import com.sebastian_daschner.jaxrs_analyzer.analysis.results.JavaClassAnalyzer.JavaClassAnalysis;
import com.sebastian_daschner.jaxrs_analyzer.model.Types;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeIdentifier;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeRepresentation;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeRepresentation.ConcreteTypeRepresentation;
import org.objectweb.asm.Type;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import static com.sebastian_daschner.jaxrs_analyzer.model.JavaUtils.*;
import static com.sebastian_daschner.jaxrs_analyzer.model.Types.*;

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
	private final Map<TypeIdentifier, TypeIdentifier> typeSwaps;
	private final Set<String> analyzedTypes;

	JavaTypeAnalyzer(final Map<TypeIdentifier, TypeRepresentation> typeRepresentations) {
		this.typeRepresentations = typeRepresentations;
		this.typeSwaps = new HashMap<>();
		this.analyzedTypes = new HashSet<>();
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
		TypeIdentifier identifier = TypeIdentifier.ofType(type);

		if (analyzedTypes.contains(type)) {
			// cool we've already analyzed this type...but was it swapped?
			TypeIdentifier typeIdentifier = typeSwaps.get(identifier);
			if (typeIdentifier != null) {
				identifier = typeIdentifier;
			}
		} else if (isContainer(type) || isOptional(type) || !isJDKType(type)) {
			analyzedTypes.add(type);
			TypeRepresentation typeRepresentation = analyzeInternal(identifier, type);

			// The type may have been swapped.  make sure to keep track of the swaps!
			TypeIdentifier typeIdentifier = typeRepresentation.getIdentifier();
			if (!typeIdentifier.equals(identifier)) {
				// we want the OG identifier to map to the swapped identifier
				typeSwaps.put(identifier, typeIdentifier);

				// If the new type is NOT a simple jdk type then we need to track the type representation
				if (!isJDKType(typeIdentifier.getType()) || isContainer(typeIdentifier.getType())) {
					// now track the swapped identifier with the swapped type rep
					typeRepresentations.put(typeIdentifier, typeRepresentation);
				}

				identifier = typeIdentifier;
			} else {
				// No swapping
				typeRepresentations.put(identifier, typeRepresentation);
			}
		}

		return identifier;
	}

	private static boolean isJDKType(final String type) {
		// exclude java, javax, etc. packages
		return Types.PRIMITIVE_TYPES.contains(type)
				|| type.startsWith("Ljava/")
				|| type.startsWith("Ljavax/");
	}

	private TypeRepresentation analyzeInternal(final TypeIdentifier identifier, final String type) {
		if (isContainer(type)) {
			final String containedType = ResponseTypeNormalizer.normalizeWrapper(type);
			return TypeRepresentation.ofCollection(identifier, analyzeInternal(TypeIdentifier.ofType(containedType), containedType));
		}

		if (isOptional(type)) {
			final String containedType = ResponseTypeNormalizer.normalizeWrapper(type);
			return TypeRepresentation.ofOptional(identifier, analyzeInternal(TypeIdentifier.ofType(containedType), containedType));
		}

		final Class<?> loadedClass = loadClassFromType(type);
		if (loadedClass != null && loadedClass.isEnum())
			return TypeRepresentation.ofEnum(identifier, Stream.of(loadedClass.getEnumConstants()).map(o -> (Enum<?>) o).map(Enum::name).toArray(String[]::new));

		return analyzeClass(identifier, type, loadedClass);
	}

	private TypeRepresentation analyzeClass(final TypeIdentifier identifier, final String type, final Class<?> clazz) {
		if (clazz == null || isJDKType(type))
			return TypeRepresentation.ofConcrete(identifier);

		JavaClassAnalysis result = null;
		for (JavaClassAnalyzer analyzer : PluginRegistry.getInstance().getJavaClassAnalyzers()) {
			result = analyzer.analyze(type, clazz);
			if (result != null) {
				break;
			}
		}

		if (result != null) {
			if (result.replacement != null && !result.replacement.equals(clazz)) {
				final TypeIdentifier replacementId = TypeIdentifier.ofType(result.replacement);
				return analyzeClass(replacementId, replacementId.getType(), result.replacement);

			} else if (result.replacementType != null && !result.replacementType.equals(type)) {
				final TypeIdentifier replacementId = TypeIdentifier.ofType(result.replacementType);
				return analyzeInternal(replacementId, result.replacementType);

			} else {
				final Map<String, TypeIdentifier> properties = new HashMap<>();

				final Stream<Class<?>> allSuperTypes = Stream.concat(Stream.of(clazz.getInterfaces()), Stream.of(clazz.getSuperclass()));
				allSuperTypes.filter(Objects::nonNull).map(Type::getDescriptor)
						.map(t -> analyzeClass(TypeIdentifier.ofType(t), t, loadClassFromType(t)))
						.filter(t -> t instanceof ConcreteTypeRepresentation)
						.map(t -> (ConcreteTypeRepresentation)t)
						.map(ConcreteTypeRepresentation::getProperties)
						.forEach(properties::putAll);

				for (Entry<String, TypeIdentifier> kv : result.properties.entrySet()) {
					properties.put(kv.getKey(), analyze(kv.getValue().getType()));
				}

				return TypeRepresentation.ofConcrete(identifier, properties);
			}
		}

		return TypeRepresentation.ofConcrete(identifier);
	}
}
