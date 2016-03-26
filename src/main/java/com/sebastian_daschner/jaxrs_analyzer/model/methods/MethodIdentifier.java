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

import com.sebastian_daschner.jaxrs_analyzer.LogProvider;
import com.sebastian_daschner.jaxrs_analyzer.model.JavaUtils;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * The method type signature with which a method can be identified.
 *
 * @author Sebastian Daschner
 */
public class MethodIdentifier {

    /**
     * The containing class as JVM class name, e.g. {@code java/lang/String}.
     */
    private final String containingClass;
    private final String methodName;

    /**
     * The return type as JVM type descriptor, e.g. {@code Ljava/lang/String;}.
     */
    private final String returnType;
    private final boolean staticMethod;

    /**
     * The method signature, e.g. {@code (Ljava/lang/String;)V}.
     */
    private final String signature;
    private final int parameters;

    private MethodIdentifier(final String containingClass, final String methodName, final String returnType, final boolean staticMethod, final String signature,
                             final int parameters) {
        Objects.requireNonNull(containingClass);
        Objects.requireNonNull(methodName);
        Objects.requireNonNull(returnType);
        Objects.requireNonNull(signature);

        this.containingClass = containingClass;
        this.methodName = methodName;
        this.returnType = returnType;
        this.staticMethod = staticMethod;
        this.signature = signature;
        this.parameters = parameters;
    }

    public String getContainingClass() {
        return containingClass;
    }

    public String getMethodName() {
        return methodName;
    }

    public int getParameters() {
        return parameters;
    }

    public String getReturnType() {
        return returnType;
    }

    public String getSignature() {
        return signature;
    }

    public boolean isStaticMethod() {
        return staticMethod;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MethodIdentifier that = (MethodIdentifier) o;

        if (staticMethod != that.staticMethod) return false;
        if (!containingClass.equals(that.containingClass)) return false;
        if (!methodName.equals(that.methodName)) return false;

        // TODO debug assumption
        if (parameters == that.parameters && returnType.equals(that.returnType) && !signature.equals(that.signature))
            LogProvider.error("warning: name, parameter size and return type matches for " + containingClass + '#' + methodName + " but not signature, desired?: " + signature + " <-> " + that.signature);

        return signature.equals(that.signature);
    }

    @Override
    public int hashCode() {
        int result = containingClass.hashCode();
        result = 31 * result + methodName.hashCode();
        result = 31 * result + returnType.hashCode();
        result = 31 * result + (staticMethod ? 1 : 0);
        result = 31 * result + signature.hashCode();
        result = 31 * result + parameters;
        return result;
    }

    @Override
    public String toString() {
        return "MethodIdentifier{" +
                "containingClass='" + containingClass + '\'' +
                ", methodName='" + methodName + '\'' +
                ", returnType='" + returnType + '\'' +
                ", staticMethod=" + staticMethod +
                ", signature='" + signature + '\'' +
                ", parameters=" + parameters +
                '}';
    }

    /**
     * Creates an identifier of the given parameters.
     *
     * @param containingClass The class name
     * @param methodName      The method name
     * @param signature       The method signature
     * @param staticMethod    If the method is static
     * @return The method identifier
     */
    public static MethodIdentifier of(final String containingClass, final String methodName, final String signature, final boolean staticMethod) {
        final String returnType = JavaUtils.getReturnType(signature);
        final int parameters = JavaUtils.getParameters(signature).size();
        return new MethodIdentifier(containingClass, methodName, returnType, staticMethod, signature, parameters);
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
    public static MethodIdentifier ofNonStatic(final String containingClass, final String methodName, final String returnType, final String... parameterTypes) {
        return of(containingClass, methodName, returnType, false, parameterTypes);
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
    public static MethodIdentifier ofStatic(final String containingClass, final String methodName, final String returnType, final String... parameterTypes) {
        return of(containingClass, methodName, returnType, true, parameterTypes);
    }

    private static MethodIdentifier of(String containingClass, String methodName, String returnType, final boolean staticMethod, String[] parameterTypes) {
        Objects.requireNonNull(parameterTypes);

        final StringBuilder builder = new StringBuilder("(");
        Stream.of(parameterTypes).forEach(builder::append);
        final String signature = builder.append(')').append(returnType).toString();

        final int parameters = parameterTypes.length;

        return new MethodIdentifier(containingClass, methodName, returnType, staticMethod, signature, parameters);
    }

}
