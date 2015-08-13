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

import com.sebastian_daschner.jaxrs_analyzer.LogProvider;
import com.sebastian_daschner.jaxrs_analyzer.analysis.utils.JavaUtils;
import com.sebastian_daschner.jaxrs_analyzer.analysis.utils.Pair;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeRepresentation;
import com.sebastian_daschner.jaxrs_analyzer.model.types.Type;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;

import javax.json.*;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.sebastian_daschner.jaxrs_analyzer.model.types.Types.COLLECTION;

/**
 * Analyzes a class (usually a POJO) for it's properties and methods.
 * The analysis is used to derive the JSON/XML representations. This class is thread-safe.
 *
 * @author Sebastian Daschner
 */
class TypeAnalyzer {

    private final static String[] NAMES_TO_IGNORE = {"getClass"};
    private static final JsonString EMPTY_JSON_STRING = new JsonString() {
        private static final String TYPE = "string";

        @Override
        public ValueType getValueType() {
            return ValueType.STRING;
        }

        @Override
        public String getString() {
            return TYPE;
        }

        @Override
        public CharSequence getChars() {
            return TYPE;
        }
    };

    private final Lock lock = new ReentrantLock();
    private final List<Type> typesPath = new LinkedList<>();
    private Type type;

    /**
     * Analyzes the given type. Resolves known generics and creates a representation of the contained class.
     *
     * @param type The type to analyze
     * @return The type representation of the class (currently just for application/json)
     */
    TypeRepresentation analyze(final Type type) {
        lock.lock();
        try {
            typesPath.clear();
            this.type = ResponseTypeNormalizer.normalizeResponseWrapper(type);
            final boolean collection = this.type.isAssignableTo(COLLECTION);

            if (!collection && isJDKType())
                return new TypeRepresentation(this.type);

            // TODO analyze XML as well
            final TypeRepresentation representation = new TypeRepresentation(ResponseTypeNormalizer.normalize(this.type));
            representation.getRepresentations().put(MediaType.APPLICATION_JSON, analyzeInternal(this.type));
            return representation;
        } finally {
            lock.unlock();
        }
    }

    private boolean isJDKType() {
        // exclude java, javax, etc. packages
        return type.toString().startsWith("java");
    }

    private JsonValue analyzeInternal(final Type type) {
        // break recursion if type has already been included in that execution
        if (typesPath.contains(type))
            return Json.createObjectBuilder().build();

        typesPath.add(type);

        if (type.isAssignableTo(COLLECTION)) {
            final JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
            JsonMapper.addToArray(arrayBuilder, ResponseTypeNormalizer.normalizeCollection(type), this::analyzeInternal);
            return arrayBuilder.build();
        }

        try {
            return analyzeClass(type);
        } catch (Exception e) {
            LogProvider.error("Could not analyze class for type analysis: " + e.getMessage());
            LogProvider.debug(e);
            return Json.createObjectBuilder().build();
        }
    }

    private JsonValue analyzeClass(final Type type) throws ClassNotFoundException {
        final CtClass ctClass = type.getCtClass();
        if (ctClass.isEnum())
            return EMPTY_JSON_STRING;

        // TODO analyze & test inheritance

        final XmlAccessType value;
        if (ctClass.hasAnnotation(XmlAccessorType.class))
            value = ((XmlAccessorType) ctClass.getAnnotation(XmlAccessorType.class)).value();
        else
            value = XmlAccessType.PUBLIC_MEMBER;

        final List<CtField> relevantFields = Stream.of(ctClass.getDeclaredFields()).filter(f -> isRelevant(f, value)).collect(Collectors.toList());
        final List<CtMethod> relevantGetters = Stream.of(ctClass.getDeclaredMethods()).filter(m -> isRelevant(m, value)).collect(Collectors.toList());

        final JsonObjectBuilder builder = Json.createObjectBuilder();

        relevantFields.stream().map(TypeAnalyzer::mapField).filter(Objects::nonNull)
                .forEach(p -> JsonMapper.addToObject(builder, p.getLeft(), p.getRight(), this::analyzeInternal));

        relevantGetters.stream().map(TypeAnalyzer::mapGetter).filter(Objects::nonNull)
                .forEach(p -> JsonMapper.addToObject(builder, p.getLeft(), p.getRight(), this::analyzeInternal));

        return builder.build();
    }

    private static boolean isRelevant(final CtField field, final XmlAccessType accessType) {
        if (JavaUtils.isSynthetic(field))
            return false;

        if (field.hasAnnotation(XmlElement.class))
            return true;

        final int modifiers = field.getModifiers();
        if (accessType == XmlAccessType.FIELD)
            // always take, unless static or transient
            return !Modifier.isTransient(modifiers) && !Modifier.isStatic(modifiers) && !field.hasAnnotation(XmlTransient.class);
        else if (accessType == XmlAccessType.PUBLIC_MEMBER)
            // only for public, non-static
            return Modifier.isPublic(modifiers) && !Modifier.isStatic(modifiers) && !field.hasAnnotation(XmlTransient.class);

        return false;
    }

    /**
     * Checks if the method is public and non-static and that the method is a Getter. Does not allow methods with ignored names.
     * Does also not take methods annotated with {@link XmlTransient}
     *
     * @param method The method
     * @return {@code true} if the method should be analyzed further
     */
    private static boolean isRelevant(final CtMethod method, final XmlAccessType accessType) {
        if (JavaUtils.isSynthetic(method) || !isGetter(method))
            return false;

        if (method.hasAnnotation(XmlElement.class))
            return true;

        if (accessType == XmlAccessType.PROPERTY)
            return !method.hasAnnotation(XmlTransient.class);
        else if (accessType == XmlAccessType.PUBLIC_MEMBER)
            return Modifier.isPublic(method.getModifiers()) && !method.hasAnnotation(XmlTransient.class);

        return false;
    }

    private static boolean isGetter(final CtMethod method) {
        if (Modifier.isStatic(method.getModifiers()))
            return false;

        final String name = method.getName();
        if (Stream.of(NAMES_TO_IGNORE).anyMatch(n -> n.equals(name)))
            return false;

        if (name.startsWith("get") && name.length() > 3)
            return !method.getSignature().endsWith(")V");

        return name.startsWith("is") && name.length() > 2 && method.getSignature().endsWith(")Z");
    }

    private static Pair<String, Type> mapField(final CtField field) {
        final Type type = JavaUtils.getFieldType(field);
        if (type == null)
            return null;

        return Pair.of(field.getName(), type);
    }

    private static Pair<String, Type> mapGetter(final CtMethod method) {
        final Type returnType = JavaUtils.getReturnType(method);
        if (returnType == null)
            return null;

        return Pair.of(normalizeGetter(method.getName()), returnType);
    }

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
