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

package com.sebastian_daschner.jaxrs_analyzer.analysis.utils;

import com.sebastian_daschner.jaxrs_analyzer.LogProvider;
import com.sebastian_daschner.jaxrs_analyzer.model.methods.MethodIdentifier;
import com.sebastian_daschner.jaxrs_analyzer.model.types.Type;
import com.sebastian_daschner.jaxrs_analyzer.model.types.Types;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMember;
import javassist.bytecode.AccessFlag;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.SignatureAttribute;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    public static Type determineMostSpecificType(final Type... types) {
        switch (types.length) {
            case 0:
                throw new IllegalArgumentException("At lease one type has to be provided");
            case 1:
                return types[0];
            case 2:
                return determineMostSpecific(types[0], types[1]);
            default:
                Type currentMostSpecific = determineMostSpecific(types[0], types[1]);
                for (int i = 2; i < types.length; i++) {
                    currentMostSpecific = determineMostSpecific(currentMostSpecific, types[i]);
                }
                return currentMostSpecific;
        }
    }

    private static Type determineMostSpecific(final Type firstType, final Type secondType) {
        if (Types.OBJECT.equals(secondType) || firstType.equals(secondType)) {
            return firstType;
        }

        if (Types.OBJECT.equals(firstType))
            return secondType;

        final boolean firstTypeParameterized = !firstType.getTypeParameters().isEmpty();
        final boolean secondTypeParameterized = !secondType.getTypeParameters().isEmpty();

        if (firstTypeParameterized || secondTypeParameterized) {
            if (firstTypeParameterized && !secondTypeParameterized) {
                return firstType;
            }

            if (!firstTypeParameterized) {
                return secondType;
            }

            if (firstType.getTypeParameters().size() != secondType.getTypeParameters().size())
                // types parameters are not compatible, no statement can be made
                return firstType;

            for (int i = 0; i < firstType.getTypeParameters().size(); i++) {
                final Type firstInner = firstType.getTypeParameters().get(i);
                final Type secondInner = secondType.getTypeParameters().get(i);

                if (firstInner.equals(secondInner)) continue;

                if (firstInner == determineMostSpecific(firstInner, secondInner))
                    return firstType;
                return secondType;
            }
        }

        // check if one type is inherited from other
        if (firstType.isAssignableTo(secondType)) return firstType;
        if (secondType.isAssignableTo(firstType)) return secondType;

        // TODO handle arrays correctly

        final boolean firstTypeArray = firstType.toString().contains("[");
        final boolean secondTypeArray = secondType.toString().contains("[");

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
     * Returns the method (Javassist {@link CtBehavior}) for the given method or constructor identifier.
     *
     * @param identifier The method identifier
     * @return The Javassist behavior or {@code null} if not found
     */
    public static CtBehavior getMethod(final MethodIdentifier identifier) {
        final CtClass ctClass = identifier.getContainingClass().getCtClass();

        // raw parameter types are taken because of type erasure in bytecode method call
        if (JavaUtils.isInitializerName(identifier.getMethodName())) {
            return Stream.of(ctClass.getDeclaredConstructors()).filter(b -> identifier.getParameters().equals(getRawParameterTypes(b))).findAny().orElse(null);
        }

        return Stream.concat(Stream.of(ctClass.getDeclaredMethods()), Stream.of(ctClass.getMethods()))
                .filter(b -> identifier.getMethodName().equals(b.getName()))
                .filter(b -> identifier.getParameters().equals(getRawParameterTypes(b))).findAny().orElse(null);
    }

    private static List<Type> getRawParameterTypes(final CtBehavior behavior) {
        try {
            return Stream.of(SignatureAttribute.toMethodSignature(behavior.getSignature()).getParameterTypes()).map(Type::new).collect(Collectors.toList());
        } catch (BadBytecode e) {
            // ignore
            return Collections.emptyList();
        }
    }

    /**
     * Returns the parameter types of the given method.
     *
     * @param behavior The method
     * @return The parameter types
     */
    public static List<Type> getParameterTypes(final CtBehavior behavior) {
        try {
            final String sig = behavior.getGenericSignature() == null ? behavior.getSignature() : behavior.getGenericSignature();
            return Stream.of(SignatureAttribute.toMethodSignature(sig).getParameterTypes()).map(Type::new).collect(Collectors.toList());
        } catch (BadBytecode e) {
            // ignore
            return Collections.emptyList();
        }
    }

    /**
     * Returns the type of the given field.
     *
     * @param field The field
     * @return The field type or {@code null} if the field could not be analyzed
     */
    public static Type getFieldType(final CtField field, final Type containingType) {
        try {
            final String sig = field.getGenericSignature() == null ? field.getSignature() : field.getGenericSignature();
            final SignatureAttribute.ClassSignature genericClassSignature = field.getDeclaringClass().getGenericSignature() == null ? null :
                    SignatureAttribute.toClassSignature(field.getDeclaringClass().getGenericSignature());
            return new Type(SignatureAttribute.toTypeSignature(sig), genericClassSignature, containingType);
        } catch (BadBytecode e) {
            // ignore
            LogProvider.error("Could not analyze field: " + field);
            LogProvider.debug(e);
            return null;
        }
    }

    /**
     * Returns the return type of the given method
     *
     * @param behavior The method
     * @return The return type or {@code null} if the method could not be analyzed
     */
    public static Type getReturnType(final CtBehavior behavior, final Type containingType) {
        try {
            final String sig = behavior.getGenericSignature() == null ? behavior.getSignature() : behavior.getGenericSignature();
            final SignatureAttribute.ClassSignature genericClassSignature = behavior.getDeclaringClass().getGenericSignature() == null ? null :
                    SignatureAttribute.toClassSignature(behavior.getDeclaringClass().getGenericSignature());
            return new Type(SignatureAttribute.toMethodSignature(sig).getReturnType(), genericClassSignature, containingType);
        } catch (BadBytecode e) {
            // ignore
            LogProvider.error("Could not analyze method: " + behavior);
            LogProvider.debug(e);
            return null;
        }
    }

    /**
     * Checks if the given member has the Synthetic attribute (JVMS 4.7.8).
     * <p>
     * <b>NOTE: </b> This is currently not returned conveniently by Javassist.
     *
     * @param member The member
     * @return {@code true} if member has Synthetic attribute.
     */
    public static boolean isSynthetic(final CtMember member) {
        return (member.getModifiers() & AccessFlag.SYNTHETIC) != 0;
    }

}
