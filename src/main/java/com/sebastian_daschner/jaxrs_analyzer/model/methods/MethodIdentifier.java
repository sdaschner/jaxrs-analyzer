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

package com.sebastian_daschner.jaxrs_analyzer.model.methods;

import com.sebastian_daschner.jaxrs_analyzer.model.types.Type;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * The method type signature with which a method can be identified.
 *
 * @author Sebastian Daschner
 */
public class MethodIdentifier {

    private final Type containingClass;
    private final String methodName;
    private final Type returnType;
    private final boolean staticMethod;
    private final List<Type> parameters;

    private MethodIdentifier(final Type containingClass, final String methodName, final Type returnType, final boolean staticMethod,
                             final List<Type> parameters) {
        Objects.requireNonNull(containingClass);
        Objects.requireNonNull(methodName);
        Objects.requireNonNull(returnType);
        for (final Type parameterType : parameters) {
            Objects.requireNonNull(parameterType);
        }

        this.containingClass = containingClass;
        this.methodName = methodName;
        this.returnType = returnType;
        this.staticMethod = staticMethod;
        this.parameters = parameters;
    }

    public Type getContainingClass() {
        return containingClass;
    }

    public String getMethodName() {
        return methodName;
    }

    public Type getReturnType() {
        return returnType;
    }

    public boolean isStaticMethod() {
        return staticMethod;
    }

    public List<Type> getParameters() {
        return parameters;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final MethodIdentifier that = (MethodIdentifier) o;

        if (staticMethod != that.staticMethod) return false;
        if (!containingClass.equals(that.containingClass)) return false;
        if (!methodName.equals(that.methodName)) return false;
        if (!returnType.equals(that.returnType)) return false;
        return parameters.equals(that.parameters);
    }

    @Override
    public int hashCode() {
        int result = containingClass.hashCode();
        result = 31 * result + methodName.hashCode();
        result = 31 * result + returnType.hashCode();
        result = 31 * result + (staticMethod ? 1 : 0);
        result = 31 * result + parameters.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "MethodIdentifier{" +
                "containingClass=" + containingClass +
                ", methodName='" + methodName + '\'' +
                ", returnType=" + returnType +
                ", staticMethod=" + staticMethod +
                ", parameters=" + parameters +
                '}';
    }

    /**
     * Creates an identifier of the given parameters.
     *
     * @param containingClass The class name
     * @param methodName      The method name
     * @param returnType      The return type
     * @param staticMethod    If the method is static
     * @param parameterTypes  The parameter types
     * @return The method identifier
     */
    public static MethodIdentifier of(final Type containingClass, final String methodName, final Type returnType,
                                      final boolean staticMethod, final Type... parameterTypes) {
        final List<Type> parameters = new LinkedList<>();
        if (parameterTypes != null && parameterTypes.length > 0)
            Stream.of(parameterTypes).forEach(parameters::add);
        return new MethodIdentifier(containingClass, methodName, returnType, staticMethod, parameters);
    }

    /**
     * Creates an identifier of a non-static method.
     *
     * @param containingClass The class name
     * @param methodName      The method name
     * @param returnType      The return type
     * @param parameterTypes  The parameter types
     * @return The method identifier
     */
    public static MethodIdentifier ofNonStatic(final Type containingClass, final String methodName, final Type returnType, final Type... parameterTypes) {
        final List<Type> parameters = new LinkedList<>();
        if (parameterTypes != null && parameterTypes.length > 0)
            Stream.of(parameterTypes).forEach(parameters::add);
        return new MethodIdentifier(containingClass, methodName, returnType, false, parameters);
    }

    /**
     * Creates an identifier of a static method.
     *
     * @param containingClass The class name
     * @param methodName      The method name
     * @param returnType      The return type
     * @param parameterTypes  The parameter types
     * @return The method identifier
     */
    public static MethodIdentifier ofStatic(final Type containingClass, final String methodName, final Type returnType, final Type... parameterTypes) {
        final List<Type> parameters = new LinkedList<>();
        if (parameterTypes != null && parameterTypes.length > 0)
            Stream.of(parameterTypes).forEach(parameters::add);
        return new MethodIdentifier(containingClass, methodName, returnType, true, parameters);
    }

}
