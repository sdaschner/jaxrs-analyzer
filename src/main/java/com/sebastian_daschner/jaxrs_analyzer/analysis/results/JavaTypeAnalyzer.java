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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.sebastian_daschner.jaxrs_analyzer.model.Types;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeIdentifier;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeRepresentation;
import com.sebastian_daschner.jaxrs_analyzer.utils.Pair;
import org.objectweb.asm.Type;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.sebastian_daschner.jaxrs_analyzer.model.JavaUtils.*;
import static com.sebastian_daschner.jaxrs_analyzer.model.Types.COLLECTION;

/**
 * Analyzes a class (usually a POJO) for it's properties and methods.
 * The analysis is used to derive the JSON/XML representations.
 *
 * @author Sebastian Daschner
 */
class JavaTypeAnalyzer {

    private final static String[] NAMES_TO_IGNORE = {"getClass"};
    private static Set<String> ignoredFieldNames = new HashSet<>();

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

        return TypeRepresentation.ofConcrete(identifier, analyzeClass(type, loadedClass));
    }

    private Map<String, TypeIdentifier> analyzeClass(final String type, final Class<?> clazz) {
        if (clazz == null || isJDKType(type))
            return Collections.emptyMap();

        final XmlAccessType value = getXmlAccessType(clazz);

        // TODO analyze & test annotation inheritance
        ignoredFieldNames.clear();
        final List<Field> relevantFields = Stream.of(clazz.getDeclaredFields()).filter(f -> isRelevant(f, value)).collect(Collectors.toList());
        final List<Method> relevantGetters = Stream.of(clazz.getDeclaredMethods()).filter(m -> isRelevant(m, value)).collect(Collectors.toList());

        final Map<String, TypeIdentifier> properties = new HashMap<>();

        final Stream<Class<?>> allSuperTypes = Stream.concat(Stream.of(clazz.getInterfaces()), Stream.of(clazz.getSuperclass()));
        allSuperTypes.filter(Objects::nonNull).map(Type::getDescriptor).map(t -> analyzeClass(t, loadClassFromType(t))).forEach(properties::putAll);

        Stream.concat(relevantFields.stream().map(f -> mapField(f, type)), relevantGetters.stream().map(g -> mapGetter(g, type)))
                .filter(Objects::nonNull).forEach(p -> {
            properties.put(p.getLeft(), TypeIdentifier.ofType(p.getRight()));
            analyze(p.getRight());
        });

        return properties;
    }

    private XmlAccessType getXmlAccessType(final Class<?> clazz) {
        Class<?> current = clazz;

        while (current != null) {
            if (isAnnotationPresent(current, XmlAccessorType.class))
                return getAnnotation(current, XmlAccessorType.class).value();
            current = current.getSuperclass();
        }

        return XmlAccessType.PUBLIC_MEMBER;
    }

    private static boolean isRelevant(final Field field, final XmlAccessType accessType) {
        if (field.isSynthetic())
            return false;

        if (hasIgnoreAnnotation(field) || isTypeIgnored(field.getType())) {
            ignoredFieldNames.add(field.getName());
            return false;
        }

        if (isAnnotationPresent(field, XmlElement.class))
            return true;

        final int modifiers = field.getModifiers();
        if (accessType == XmlAccessType.FIELD)
            // always take, unless static or transient
            return !Modifier.isTransient(modifiers) && !Modifier.isStatic(modifiers) && !isAnnotationPresent(field, XmlTransient.class);
        else if (accessType == XmlAccessType.PUBLIC_MEMBER)
            // only for public, non-static
            return Modifier.isPublic(modifiers) && !Modifier.isStatic(modifiers) && !isAnnotationPresent(field, XmlTransient.class);

        return false;
    }

    private static <T extends AccessibleObject & Member> boolean hasIgnoreAnnotation(final T member) {
        return isAnnotationPresent(member, JsonIgnore.class) || isTypeIgnored(member.getDeclaringClass());
    }

    private static boolean isTypeIgnored(final Class<?> declaringClass) {
        return isAnnotationPresent(declaringClass, JsonIgnoreType.class);
    }

    /**
     * Checks if the method is public and non-static and that the method is a Getter.
     * Does not allow methods with ignored names.
     * Does also not take methods annotated with {@link XmlTransient}.
     *
     * @param method The method
     * @return {@code true} if the method should be analyzed further
     */
    private static boolean isRelevant(final Method method, final XmlAccessType accessType) {
        if (method.isSynthetic() || !isGetter(method))
            return false;

        final boolean propertyIgnored = ignoredFieldNames.contains(extractPropertyName(method.getName()));
        if (propertyIgnored || hasIgnoreAnnotation(method) || isTypeIgnored(method.getReturnType())) {
            return false;
        }

        if (isAnnotationPresent(method, XmlElement.class))
            return true;

        if (accessType == XmlAccessType.PROPERTY)
            return !isAnnotationPresent(method, XmlTransient.class);
        else if (accessType == XmlAccessType.PUBLIC_MEMBER)
            return Modifier.isPublic(method.getModifiers()) && !isAnnotationPresent(method, XmlTransient.class);

        return false;
    }

    /**
     * Converts a getter name to the property name (without the "get" or "is" and lowercase).
     *
     * @param name The name of the method (MUST match "get[A-Z][A-Za-z]*|is[A-Z][A-Za-z]*")
     * @return The name of the property
     */
    private static String extractPropertyName(final String name) {
        final int size = name.startsWith("is") ? 2 : 3;
        final char chars[] = name.substring(size).toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }

    private static boolean isGetter(final Method method) {
        if (Modifier.isStatic(method.getModifiers()))
            return false;

        final String name = method.getName();
        if (Stream.of(NAMES_TO_IGNORE).anyMatch(n -> n.equals(name)))
            return false;

        if (name.startsWith("get") && name.length() > 3)
            return method.getReturnType() != void.class;

        return name.startsWith("is") && name.length() > 2 && method.getReturnType() == boolean.class;
    }

    private static Pair<String, String> mapField(final Field field, final String containedType) {
        final String type = getFieldDescriptor(field, containedType);
        if (type == null)
            return null;

        return Pair.of(field.getName(), type);
    }

    private static Pair<String, String> mapGetter(final Method method, final String containedType) {
        final String returnType = getReturnType(getMethodSignature(method), containedType);
        if (returnType == null)
            return null;

        return Pair.of(extractPropertyName(method.getName()), returnType);
    }

}
