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

import com.sebastian_daschner.jaxrs_analyzer.model.JavaUtils;

import java.util.List;
import java.util.Objects;

import static java.util.Arrays.asList;

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
    private final List<String> parameters;

    private MethodIdentifier(final String containingClass, final String methodName, final List<String> parameters, final String returnType, final boolean staticMethod) {
        Objects.requireNonNull(containingClass);
        Objects.requireNonNull(methodName);
        Objects.requireNonNull(returnType);

        this.containingClass = containingClass;
        this.methodName = methodName;
        this.returnType = returnType;
        this.staticMethod = staticMethod;
        this.parameters = parameters;
    }

    /**
     * Returns the method signature, e.g. {@code (Ljava/lang/String;)V}.
     */
    public String getSignature() {
        final StringBuilder builder = new StringBuilder("(");
        parameters.forEach(builder::append);
        return builder.append(')').append(returnType).toString();
    }

    public String getContainingClass() {
        return containingClass;
    }

    public String getMethodName() {
        return methodName;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public String getReturnType() {
        return returnType;
    }

    public boolean isStaticMethod() {
        return staticMethod;
    }

    @Override
    @SuppressWarnings({"squid:S00122", "squid:S1067"})
    // generated code so it's fine to have more than one statement in one line and more than 3 conditional operators
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final MethodIdentifier that = (MethodIdentifier) o;
        return staticMethod == that.staticMethod &&
                Objects.equals(containingClass, that.containingClass) &&
                Objects.equals(methodName, that.methodName) &&
                Objects.equals(returnType, that.returnType) &&
                Objects.equals(parameters, that.parameters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(containingClass, methodName, returnType, staticMethod, parameters);
    }

    @Override
    public String toString() {
        return "MethodIdentifier{" +
                "containingClass='" + containingClass + '\'' +
                ", methodName='" + methodName + '\'' +
                ", returnType='" + returnType + '\'' +
                ", staticMethod=" + staticMethod +
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
        final List<String> parameters = JavaUtils.getParameters(signature);
        return new MethodIdentifier(containingClass, methodName, parameters, returnType, staticMethod);
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

        return new MethodIdentifier(containingClass, methodName, asList(parameterTypes), returnType, staticMethod);
    }

}
