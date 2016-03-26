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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.sebastian_daschner.jaxrs_analyzer.model.JavaUtils.isAssignableTo;
import static com.sebastian_daschner.jaxrs_analyzer.model.Types.COLLECTION;

/**
 * Analyzes a class (usually a POJO) for it's properties and methods.
 * The analysis is used to derive the JSON/XML representations.
 *
 * @author Sebastian Daschner
 */
class JavaTypeAnalyzer {

    private final static String[] NAMES_TO_IGNORE = {"getClass"};

    /**
     * The type representation storage where all analyzed types have to be added. This will be created by the caller.
     */
    private final Map<TypeIdentifier, TypeRepresentation> typeRepresentations;
    private final Set<String> analyzedTypes;

    JavaTypeAnalyzer(final Map<TypeIdentifier, TypeRepresentation> typeRepresentations) {
        this.typeRepresentations = typeRepresentations;
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

    private static boolean isJDKType(final String type) {
        // exclude java, javax, etc. packages
        if (Types.PRIMITIVE_TYPES.contains(type))
            return true;

        return type.startsWith("Ljava/") || type.startsWith("Ljavax/");
    }

    private TypeRepresentation analyzeInternal(final TypeIdentifier identifier, final String type) {
        if (isAssignableTo(type, COLLECTION)) {
            final String containedType = ResponseTypeNormalizer.normalizeCollection(type);
            return TypeRepresentation.ofCollection(identifier, analyzeInternal(TypeIdentifier.ofType(containedType), containedType));
        }

        return TypeRepresentation.ofConcrete(identifier, analyzeClass(type));
    }

    private Map<String, TypeIdentifier> analyzeClass(final String type) {
        // TODO load class -> check
//        final CtClass ctClass = type.getCtClass();
//        if (ctClass.isEnum() || isJDKType(type))
//            return Collections.emptyMap();

//        final XmlAccessType value = getXmlAccessType(ctClass);

        // TODO analyze & test inheritance
//        final List<CtField> relevantFields = Stream.of(ctClass.getDeclaredFields()).filter(f -> isRelevant(f, value)).collect(Collectors.toList());
//        final List<CtMethod> relevantGetters = Stream.of(ctClass.getDeclaredMethods()).filter(m -> isRelevant(m, value)).collect(Collectors.toList());

        final Map<String, TypeIdentifier> properties = new HashMap<>();
//
//        Stream.concat(relevantFields.stream().map(f -> mapField(f, type)), relevantGetters.stream().map(g -> mapGetter(g, type)))
//                .filter(Objects::nonNull).forEach(p -> {
//            properties.put(p.getLeft(), TypeIdentifier.ofType(p.getRight()));
//            analyze(p.getRight());
//        });

        return properties;
    }

//    private XmlAccessType getXmlAccessType(CtClass ctClass) {
//        try {
//            CtClass current = ctClass;
//
//            while (current != null) {
//                if (current.hasAnnotation(XmlAccessorType.class))
//                    return ((XmlAccessorType) current.getAnnotation(XmlAccessorType.class)).value();
//                current = current.getSuperclass();
//            }
//
//        } catch (ClassNotFoundException | NotFoundException e) {
//            LogProvider.error("Could not analyze JAXB annotation of type: " + e.getMessage());
//            LogProvider.debug(e);
//        }
//
//        return XmlAccessType.PUBLIC_MEMBER;
//    }

//    private static boolean isRelevant(final CtField field, final XmlAccessType accessType) {
//        if (JavaUtils.isSynthetic(field))
//            return false;
//
//        if (field.hasAnnotation(XmlElement.class))
//            return true;
//
//        final int modifiers = field.getModifiers();
//        if (accessType == XmlAccessType.FIELD)
//            // always take, unless static or transient
//            return !Modifier.isTransient(modifiers) && !Modifier.isStatic(modifiers) && !field.hasAnnotation(XmlTransient.class);
//        else if (accessType == XmlAccessType.PUBLIC_MEMBER)
//            // only for public, non-static
//            return Modifier.isPublic(modifiers) && !Modifier.isStatic(modifiers) && !field.hasAnnotation(XmlTransient.class);
//
//        return false;
//    }
//
//    /**
//     * Checks if the method is public and non-static and that the method is a Getter. Does not allow methods with ignored names.
//     * Does also not take methods annotated with {@link XmlTransient}
//     *
//     * @param method The method
//     * @return {@code true} if the method should be analyzed further
//     */
//    private static boolean isRelevant(final CtMethod method, final XmlAccessType accessType) {
//        if (JavaUtils.isSynthetic(method) || !isGetter(method))
//            return false;
//
//        if (method.hasAnnotation(XmlElement.class))
//            return true;
//
//        if (accessType == XmlAccessType.PROPERTY)
//            return !method.hasAnnotation(XmlTransient.class);
//        else if (accessType == XmlAccessType.PUBLIC_MEMBER)
//            return Modifier.isPublic(method.getModifiers()) && !method.hasAnnotation(XmlTransient.class);
//
//        return false;
//    }
//
//    private static boolean isGetter(final CtMethod method) {
//        if (Modifier.isStatic(method.getModifiers()))
//            return false;
//
//        final String name = method.getName();
//        if (Stream.of(NAMES_TO_IGNORE).anyMatch(n -> n.equals(name)))
//            return false;
//
//        if (name.startsWith("get") && name.length() > 3)
//            return !method.getSignature().endsWith(")V");
//
//        return name.startsWith("is") && name.length() > 2 && method.getSignature().endsWith(")Z");
//    }
//
//    private static Pair<String, Type> mapField(final CtField field, final Type containedType) {
//        final Type type = JavaUtils.getFieldType(field, containedType);
//        if (type == null)
//            return null;
//
//        return Pair.of(field.getName(), type);
//    }
//
//    private static Pair<String, Type> mapGetter(final CtMethod method, final Type containedType) {
////        final String returnType = JavaUtils.getReturnType(method, containedType);
//        final Type returnType = null;
//        if (returnType == null)
//            return null;
//
//        return Pair.of(normalizeGetter(method.getName()), returnType);
//    }

    /**
     * Converts a getter name to the property name (without the "get" or "is" and lowercase).
     *
     * @param name The name of the method (MUST match "get[A-Z][A-Za-z]*|is[A-Z][A-Za-z]*")
     * @return The name of the property
     */
    private static String normalizeGetter(final String name) {
        final int size = name.startsWith("is") ? 2 : 3;
        final char chars[] = name.substring(size).toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }

}
