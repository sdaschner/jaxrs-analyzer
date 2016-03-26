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

import jdk.internal.org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.sebastian_daschner.jaxrs_analyzer.model.Types.OBJECT;

/**
 * Contains Java and Javassist utility functionality.
 *
 * @author Sebastian Daschner
 */
public final class JavaUtils {

    public static final String INITIALIZER_NAME = "<init>";
    public static final String BOOTSTRAP_ATTRIBUTE_NAME = "BootstrapMethods";

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
     * Determines the type which is most "specific" (i. e. parameterized types are more "specific" than generic types,
     * types which are not {@link Object} are less specific). If no exact statement can be made, the first type is chosen.
     *
     * @param types The types
     * @return The most "specific" type
     */
    public static String determineMostSpecificType(final String... types) {
        switch (types.length) {
            case 0:
                throw new IllegalArgumentException("At lease one type has to be provided");
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

        final boolean firstTypeParameterized = !getTypeParameters(firstType).isEmpty();
        final boolean secondTypeParameterized = !getTypeParameters(secondType).isEmpty();

        if (firstTypeParameterized || secondTypeParameterized) {
            if (firstTypeParameterized && !secondTypeParameterized) {
                return firstType;
            }

            if (!firstTypeParameterized) {
                return secondType;
            }

            if (getTypeParameters(firstType).size() != getTypeParameters(secondType).size())
                // types parameters are not compatible, no statement can be made
                return firstType;

            for (int i = 0; i < getTypeParameters(firstType).size(); i++) {
                final String firstInner = getTypeParameters(firstType).get(i);
                final String secondInner = getTypeParameters(secondType).get(i);

                if (firstInner.equals(secondInner)) continue;

                if (firstInner == determineMostSpecific(firstInner, secondInner))
                    return firstType;
                return secondType;
            }
        }

        // check if one type is inherited from other
        if (isAssignableTo(firstType, secondType)) return firstType;
        if (isAssignableTo(secondType, firstType)) return secondType;

        // TODO handle arrays correctly

        final boolean firstTypeArray = firstType.contains("[");
        final boolean secondTypeArray = secondType.contains("[");

        if (firstTypeArray || secondTypeArray) {
            if (firstTypeArray && !secondTypeArray) {
                return firstType;
            }

            if (!firstTypeArray) {
                return secondType;
            }
        }

        return firstType;
    }

    /**
     * Checks if the left type is assignable to the right type, i.e. the right type is of the same or a sub-type.
     */
    public static boolean isAssignableTo(final String leftType, final String rightType) {
        // TODO implement
        if (leftType.equals(rightType))
            return true;

//        try {
//            final CtClass superclass = ctClass.getSuperclass();
//            if (superclass != null && !Types.OBJECT.ctClass.equals(superclass) && new Type(superclass).isAssignableTo(type)) {
//                return true;
//            }
//
//            return Stream.of(ctClass.getInterfaces()).anyMatch(i -> new Type(i).isAssignableTo(type));
//        } catch (NotFoundException e) {
//            LogProvider.error("Could not analyze superclass of: " + ctClass.getName() + ", reason: " + e.getMessage());
//            LogProvider.debug(e);
//            return false;
//        }
        return false;
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
            case 'L':
                final int typeParamStart = type.indexOf('<');
                final int endIndex = typeParamStart >= 0 ? typeParamStart : type.indexOf(';');
                return type.substring(1, endIndex);
            case '[':
                return toClassName(type.substring(1));
            default:
                throw new IllegalArgumentException("Not an object or array type signature: " + type);
        }
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
        return Collections.emptyList();
    }

    /**
     * Returns the return type of the given method signature. Parametrized types are supported.
     */
    public static String getReturnType(final String methodSignature) {
        return methodSignature.substring(methodSignature.lastIndexOf(')') + 1);
    }

    /**
     * Returns the parameter types of the given method signature. Parametrized types are supported.
     */
    public static List<String> getParameters(final String methodDesc) {
        final char[] buffer = methodDesc.toCharArray();
        final List<String> args = new ArrayList<>();

        int offset = 1;
        while (buffer[offset] != ')') {
            final String type = getNextType(buffer, offset);
            args.add(type);
            offset += type.length();// + (type.charAt(0) == 'L' ? 2 : 0);
        }
        return args;
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
                return getNextType(buf, off, 0);
            default:
                throw new IllegalArgumentException("Illegal signature provided: " + new String(buf));
        }
    }

    private static String getNextType(char[] buf, int off, int len) {
        int depth = 0;
        if (buf[off + len] == 'L')
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
