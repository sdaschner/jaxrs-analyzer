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

package com.sebastian_daschner.jaxrs_analyzer.model;

import com.sebastian_daschner.jaxrs_analyzer.LogProvider;
import com.sebastian_daschner.jaxrs_analyzer.analysis.classes.ContextClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.util.TraceSignatureVisitor;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.lang.invoke.VarHandle;

import static com.sebastian_daschner.jaxrs_analyzer.model.Types.*;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;

/**
 * Contains Java reflection utility functionality.
 *
 * @author Sebastian Daschner
 */
public final class JavaUtils {

    public static final String INITIALIZER_NAME = "<init>";
    
    private static final VarHandle SIGNATURE;
    private static final VarHandle METHOD_SIGNATURE;

    static {
        try {
            var lookup = MethodHandles.privateLookupIn(Field.class, MethodHandles.lookup());
            SIGNATURE = lookup.findVarHandle(Field.class, "signature", String.class);
        } catch (IllegalAccessException | NoSuchFieldException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    static {
        try {
            var lookup = MethodHandles.privateLookupIn(Method.class, MethodHandles.lookup());
            METHOD_SIGNATURE = lookup.findVarHandle(Method.class, "signature", String.class);
        } catch (IllegalAccessException | NoSuchFieldException ex) {
            throw new RuntimeException(ex);
        }
    }

    private JavaUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * Checks if the given method name is a Java initializer.
     *
     * @param name The method name
     * @return {@code true} if name is an initializer
     */
    public static boolean isInitializerName(final String name) {
        return INITIALIZER_NAME.equals(name);
    }

    /**
     * Returns the annotation or {@code null} if the element is not annotated with that type.
     * <b>Note:</b> This step is necessary due to issues with external class loaders (e.g. Maven).
     * The classes may not be identical and are therefore compared by FQ class name.
     */
    public static <A extends Annotation> A getAnnotation(final AnnotatedElement annotatedElement, final Class<A> annotationClass) {
        final Optional<Annotation> annotation = Stream.of(annotatedElement.getAnnotations())
                .filter(a -> a.annotationType().getName().equals(annotationClass.getName()))
                .findAny();
        return (A) annotation.orElse(null);
    }

    /**
     * Checks if the annotation is present on the annotated element.
     * <b>Note:</b> This step is necessary due to issues with external class loaders (e.g. Maven).
     * The classes may not be identical and are therefore compared by FQ class name.
     */
    public static boolean isAnnotationPresent(final AnnotatedElement annotatedElement, final Class<?> annotationClass) {
        return Stream.of(annotatedElement.getAnnotations()).map(Annotation::annotationType).map(Class::getName).anyMatch(n -> n.equals(annotationClass.getName()));
    }

    /**
     * Determines the type which is most "specific" (i. e. parametrized types are more "specific" than generic types,
     * types which are not {@link Object} are less specific). If no exact statement can be made, the first type is chosen.
     *
     * @param types The types
     * @return The most "specific" type
     */
    public static String determineMostSpecificType(final String... types) {
        switch (types.length) {
            case 0:
                throw new IllegalArgumentException("At least one type has to be provided");
            case 1:
                return types[0];
            case 2:
                return determineMostSpecific(types[0], types[1]);
            default:
                String currentMostSpecific = determineMostSpecific(types[0], types[1]);
                for (int i = 2; i < types.length; i++) {
                    currentMostSpecific = determineMostSpecific(currentMostSpecific, types[i]);
                }
                return currentMostSpecific;
        }
    }

    private static String determineMostSpecific(final String firstType, final String secondType) {
        if (OBJECT.equals(secondType) || firstType.equals(secondType)) {
            return firstType;
        }

        if (OBJECT.equals(firstType))
            return secondType;

        final List<String> firstTypeParameters = getTypeParameters(firstType);
        final List<String> secondTypeParameters = getTypeParameters(secondType);
        final boolean firstTypeParameterized = !firstTypeParameters.isEmpty();
        final boolean secondTypeParameterized = !secondTypeParameters.isEmpty();

        if (firstTypeParameterized || secondTypeParameterized) {
            if (firstTypeParameterized && !secondTypeParameterized) {
                return firstType;
            }

            if (!firstTypeParameterized) {
                return secondType;
            }

            if (firstTypeParameters.size() != secondTypeParameters.size())
                // types parameters are not compatible, no statement can be made
                return firstType;

            for (int i = 0; i < firstTypeParameters.size(); i++) {
                final String firstInner = firstTypeParameters.get(i);
                final String secondInner = secondTypeParameters.get(i);

                if (firstInner.equals(secondInner)) continue;

                // desired to test against identity, i.e. which object was taken by comparison
                if (firstInner == determineMostSpecific(firstInner, secondInner))
                    return firstType;
                return secondType;
            }
        }

        final boolean firstTypeArray = firstType.charAt(0) == '[';
        final boolean secondTypeArray = secondType.charAt(0) == '[';

        if (firstTypeArray || secondTypeArray) {
            if (firstTypeArray && !secondTypeArray) {
                return firstType;
            }

            if (!firstTypeArray) {
                return secondType;
            }
        }

        // check if one type is inherited from other
        if (isAssignableTo(firstType, secondType)) return firstType;
        if (isAssignableTo(secondType, firstType)) return secondType;

        return firstType;
    }

    /**
     * Determines the type which is least "specific" (i. e. parametrized types are more "specific" than generic types,
     * types which are not {@link Object} are less specific). If no exact statement can be made, the second type is chosen.
     *
     * @param types The types
     * @return The most "specific" type
     * @see #determineMostSpecificType(String...)
     */
    public static String determineLeastSpecificType(final String... types) {
        switch (types.length) {
            case 0:
                throw new IllegalArgumentException("At least one type has to be provided");
            case 1:
                return types[0];
            case 2:
                return determineLeastSpecific(types[0], types[1]);
            default:
                String currentLeastSpecific = determineLeastSpecific(types[0], types[1]);
                for (int i = 2; i < types.length; i++) {
                    currentLeastSpecific = determineLeastSpecific(currentLeastSpecific, types[i]);
                }
                return currentLeastSpecific;
        }
    }

    private static String determineLeastSpecific(final String firstType, final String secondType) {
        final String mostSpecificType = determineMostSpecificType(firstType, secondType);
        // has to compare identity to see which String object was taken
        if (mostSpecificType == firstType)
            return secondType;
        return firstType;
    }

    /**
     * Checks if the left type is assignable to the right type, i.e. the right type is of the same or a sub-type.
     */
    public static boolean isAssignableTo(final String leftType, final String rightType) {
        if (leftType.equals(rightType))
            return true;

        final boolean firstTypeArray = leftType.charAt(0) == '[';
        if (firstTypeArray ^ rightType.charAt(0) == '[') {
            return false;
        }

        final Class<?> leftClass = loadClassFromType(leftType);
        final Class<?> rightClass = loadClassFromType(rightType);
        if (leftClass == null || rightClass == null)
            return false;

        final boolean bothTypesParameterized = hasTypeParameters(leftType) && hasTypeParameters(rightType);
        return rightClass.isAssignableFrom(leftClass) && (firstTypeArray || !bothTypesParameterized || getTypeParameters(leftType).equals(getTypeParameters(rightType)));
    }

    private static boolean hasTypeParameters(final String type) {
        return type.indexOf('<') >= 0;
    }

    /**
     * Converts the given JVM object type signature to a class name. Erasures parametrized types.
     * <p>
     * Example: {@code Ljava/util/List<Ljava/lang/String;>; -> java/util/List}
     *
     * @throws IllegalArgumentException If the type is not a reference or array type.
     */
    public static String toClassName(final String type) {
        switch (type.charAt(0)) {
            case 'V':
                return CLASS_PRIMITIVE_VOID;
            case 'Z':
                return CLASS_PRIMITIVE_BOOLEAN;
            case 'C':
                return CLASS_PRIMITIVE_CHAR;
            case 'B':
                return CLASS_PRIMITIVE_BYTE;
            case 'S':
                return CLASS_PRIMITIVE_SHORT;
            case 'I':
                return CLASS_PRIMITIVE_INT;
            case 'F':
                return CLASS_PRIMITIVE_FLOAT;
            case 'J':
                return CLASS_PRIMITIVE_LONG;
            case 'D':
                return CLASS_PRIMITIVE_DOUBLE;
            case 'L':
                final int typeParamStart = type.indexOf('<');
                final int endIndex = typeParamStart >= 0 ? typeParamStart : type.indexOf(';');
                return type.substring(1, endIndex);
            case '[':
            case '+':
            case '-':
                return toClassName(type.substring(1));
            case 'T':
                // TODO handle type variables
                return CLASS_OBJECT;
            default:
                throw new IllegalArgumentException("Not a type signature: " + type);
        }
    }

    /**
     * Converts the given JVM class name to a type signature.
     * <p>
     * Example: {@code java/util/List -> Ljava/util/List;}
     */
    public static String toType(final String className) {
        return 'L' + className + ';';
    }

    /**
     * Converts the given type signature to a human readable type string.
     * <p>
     * Example: {@code Ljava/util/Map<Ljava/lang/String;Ljava/lang/String;>; -> java.util.Map<java.lang.String, java.lang.String>}
     */
    public static String toReadableType(final String type) {
        final SignatureReader reader = new SignatureReader(type);
        final TraceSignatureVisitor visitor = new TraceSignatureVisitor(0);
        reader.acceptType(visitor);
        return visitor.getDeclaration();
    }

    /**
     * Returns the JVM type signature of the given object.
     */
    public static String getType(final Object value) {
        return Type.getDescriptor(value.getClass());
    }

    /**
     * Returns the type parameters of the given type. Will be an empty list if the type is not parametrized.
     */
    public static List<String> getTypeParameters(final String type) {
        if (type.charAt(0) != 'L')
            return emptyList();

        int lastStart = type.indexOf('<') + 1;
        final List<String> parameters = new ArrayList<>();

        if (lastStart > 0) {
            int depth = 0;
            for (int i = lastStart; i < type.length() - 2; i++) {
                final char c = type.charAt(i);
                if (c == '<')
                    depth++;
                else if (c == '>')
                    depth--;
                else if (c == ';' && depth == 0) {
                    parameters.add(type.substring(lastStart, i + 1));
                    lastStart = i + 1;
                }
            }
        }
        return parameters;
    }

    /**
     * Returns the return type of the given method signature. Parametrized types are supported.
     */
    public static String getReturnType(final String methodSignature) {
        return getReturnType(methodSignature, null);
    }

    public static String getReturnType(final String methodSignature, final String containedType) {
        final String type = methodSignature.substring(methodSignature.lastIndexOf(')') + 1);
        return resolvePotentialTypeVariables(type, containedType);
    }

    private static Map<String, String> getTypeVariables(final String type) {
        if (type == null)
            return emptyMap();
        final Map<String, String> variables = new HashMap<>();
        final List<String> actualTypeParameters = getTypeParameters(type);
        final Class<?> loadedClass = loadClassFromType(type);
        if (loadedClass == null) {
            LogProvider.debug("could not load class for type " + type);
            return emptyMap();
        }

        final TypeVariable<? extends Class<?>>[] typeParameters = loadedClass.getTypeParameters();
        for (int i = 0; i < actualTypeParameters.size(); i++) {
            variables.put(typeParameters[i].getName(), actualTypeParameters.get(i));
        }
        return variables;
    }

    public static Class<?> loadClassFromName(final String className) {
        switch (className) {
            case CLASS_PRIMITIVE_VOID:
                return int.class;
            case CLASS_PRIMITIVE_BOOLEAN:
                return boolean.class;
            case CLASS_PRIMITIVE_CHAR:
                return char.class;
            case CLASS_PRIMITIVE_BYTE:
                return byte.class;
            case CLASS_PRIMITIVE_SHORT:
                return short.class;
            case CLASS_PRIMITIVE_INT:
                return int.class;
            case CLASS_PRIMITIVE_FLOAT:
                return float.class;
            case CLASS_PRIMITIVE_LONG:
                return long.class;
            case CLASS_PRIMITIVE_DOUBLE:
                return double.class;
        }

        // TODO test for variable types

        try {
            return ContextClassReader.getClassLoader().loadClass(className.replace('/', '.'));
        } catch (ClassNotFoundException e) {
            LogProvider.error("Could not load class " + className);
            LogProvider.debug(e);
            return null;
        }
    }

    public static Class<?> loadClassFromType(final String type) {
        return loadClassFromName(toClassName(type));
    }

    public static Method findMethod(final String className, final String methodName, final String signature) {
        final Class<?> loadedClass = loadClassFromName(className);
        if (loadedClass == null)
            return null;

        return findMethod(loadedClass, methodName, signature);
    }

    public static Method findMethod(final Class<?> loadedClass, final String methodName, final String signature) {
        final List<String> parameters = getParameters(signature);
        return Stream.of(loadedClass.getDeclaredMethods()).filter(m -> m.getName().equals(methodName)
                && m.getParameterCount() == parameters.size()
                // return types are not taken into account (could be overloaded method w/ different return type)
                && Objects.equals(getParameters(getMethodSignature(m)), parameters)
        ).findAny().orElse(null);
    }

    public static String getMethodSignature(final String returnType, final String... parameterTypes) {
        final String parameters = Stream.of(parameterTypes).collect(Collectors.joining());
        return '(' + parameters + ')' + returnType;
    }

    public static String getMethodSignature(final Method method) {
    	String signature = (String) METHOD_SIGNATURE.get(method);

        if (signature != null) {
            return signature;
        }

        return Type.getMethodDescriptor(method);
    }

    public static String getFieldDescriptor(final Field field, final String containedType) {
    	String signature = (String) SIGNATURE.get(field);

    	if (signature != null) {
              return resolvePotentialTypeVariables(signature, containedType);
        }

    	return Type.getDescriptor(field.getType());
    }

    private static String resolvePotentialTypeVariables(final String signature, final String containedType) {
        // resolve type variables immediately
        if (signature.charAt(0) == 'T' || signature.contains("<T") || signature.contains(";T") || signature.contains(")T")) {
            // TODO test
            final Map<String, String> typeVariables = getTypeVariables(containedType);
            StringBuilder builder = new StringBuilder(signature);
            boolean startType = true;

            for (int i = 0; i < builder.length(); i++) {
                if (startType && builder.charAt(i) == 'T') {
                    final int end = builder.indexOf(";", i);
                    final String identifier = builder.substring(i + 1, end);
                    final String resolvedVariableType = typeVariables.getOrDefault(identifier, OBJECT);
                    builder.replace(i, end + 1, resolvedVariableType);
                    i = end;
                    continue;
                }
                startType = builder.charAt(i) == '<' || builder.charAt(i) == ';';
            }
            return builder.toString();
        }
        return signature;
    }

    /**
     * Returns the parameter types of the given method signature. Parametrized types are supported.
     */
    public static List<String> getParameters(final String methodDesc) {
//        final String[] types = resolveMethodSignature(methodDesc);
//        return IntStream.range(0, types.length).mapToObj(i -> types[i]).collect(Collectors.toList());
        if (methodDesc == null)
            return emptyList();

        final char[] buffer = methodDesc.toCharArray();
        final List<String> args = new ArrayList<>();

        // TODO resolve type parameters correctly -> information useful? -> maybe use ASM's SignatureReader/Visitor
        int offset = methodDesc.indexOf('(') + 1;
        while (buffer[offset] != ')') {
            final String type = getNextType(buffer, offset);
            args.add(type);
            offset += type.length();// + (type.charAt(0) == 'L' ? 2 : 0);
        }

        // TODO change, see type parameters
        // prevent type parameter identifiers
        final ListIterator<String> iterator = args.listIterator();
        while (iterator.hasNext()) {
            final String arg = iterator.next();
            if (arg.charAt(0) == 'T')
                iterator.set(OBJECT);
        }

        return args;
    }

    /**
     * Resolves the given method signatures to an array of (self-contained) Java type descriptions.
     *
     * @param methodDesc The method description signature (can contain type parameters and generics)
     * @return The types as an array with the method parameter types first and the return type as index {@code array.length - 1}
     */
    private static String[] resolveMethodSignature(final String methodDesc) {

//         if starts with '<' -> resolve type parameters
//        final Map<String, String> typeParameters = null;
//        if (methodDesc.charAt(0) == '<') {
//            typeParameters = resolveTypeParameters(methodDesc);
//        }

        return null;
    }

    private static Map<String, String> resolveTypeParameters(final String methodDesc) {
//        boolean identifierMode = true;
//        int identifierStart = 1;
//        String currentIdentifier = null;
//
//        for (int i = 1; methodDesc.charAt(i) != '>'; i++) {
//            switch (methodDesc.charAt(i)) {
//                case ':':
//                    if (identifierMode) {
//                        identifierMode = false;
//                        currentIdentifier = methodDesc.substring(identifierStart, i);
//                    } else {
//
//                    }
//            }
//        }
        return null;
    }

    private static String getNextType(final char[] buf, final int off) {
        switch (buf[off]) {
            case 'V':
            case 'Z':
            case 'C':
            case 'B':
            case 'S':
            case 'I':
            case 'F':
            case 'J':
            case 'D':
                return String.valueOf(buf[off]);
            case '[':
                int len = 1;
                while (buf[off + len] == '[') {
                    len++;
                }
                return getNextType(buf, off, len);
            case 'L':
                // TODO resolve type variables
            case 'T':
                return getNextType(buf, off, 0);
            default:
                throw new IllegalArgumentException("Illegal signature provided: " + new String(buf));
        }
    }

    private static String getNextType(char[] buf, int off, int len) {
        int depth = 0;
        if (buf[off + len] == 'L' || buf[off + len] == 'T')
            while (buf[off + len] != ';' || depth != 0) {
                if (buf[off + len] == '<')
                    depth++;
                else if (buf[off + len] == '>')
                    depth--;
                len++;
            }
        return new String(buf, off, len + 1);
    }
}
