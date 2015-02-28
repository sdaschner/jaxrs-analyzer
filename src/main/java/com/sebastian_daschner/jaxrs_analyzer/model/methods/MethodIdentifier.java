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

import com.sebastian_daschner.jaxrs_analyzer.analysis.utils.StringUtils;

import java.util.Arrays;

/**
 * The method type signature with which a method can be identified.
 *
 * @author Sebastian Daschner
 */
public class MethodIdentifier {

    private final String className;
    private final String methodName;
    private final String returnType;
    private final boolean staticMethod;
    private final String[] parameterTypes;

    private MethodIdentifier(final String className, final String methodName, final String returnType, final boolean staticMethod, final String[] parameterTypes) {
        StringUtils.requireNonBlank(className);
        StringUtils.requireNonBlank(methodName);
        for (final String parameterType : parameterTypes) {
            StringUtils.requireNonBlank(parameterType);
        }

        this.className = className;
        this.methodName = methodName;
        this.returnType = returnType;
        this.staticMethod = staticMethod;
        this.parameterTypes = parameterTypes;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getReturnType() {
        return returnType;
    }

    public boolean isStaticMethod() {
        return staticMethod;
    }

    public String[] getParameterTypes() {
        return parameterTypes;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final MethodIdentifier that = (MethodIdentifier) o;

        if (!className.equals(that.className)) return false;
        if (!methodName.equals(that.methodName)) return false;
        if (returnType != null ? !returnType.equals(that.returnType) : that.returnType != null) return false;
        if (staticMethod ^ that.staticMethod) return false;

        return Arrays.equals(parameterTypes, that.parameterTypes);
    }

    @Override
    public int hashCode() {
        int result = className.hashCode();
        result = 31 * result + methodName.hashCode();
        result = 31 * result + (returnType != null ? returnType.hashCode() : 0);
        result = 31 * result + (staticMethod ? 1 : 0);
        result = 31 * result + Arrays.hashCode(parameterTypes);
        return result;
    }

    @Override
    public String toString() {
        return "{" +
                "className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                ", returnType='" + returnType + '\'' +
                ", staticMethod='" + staticMethod + '\'' +
                ", parameterTypes=" + Arrays.toString(parameterTypes) +
                '}';
    }

    /**
     * Creates an identifier of the given parameters.
     *
     * @param className      The class name
     * @param methodName     The method name
     * @param returnType     The return type
     * @param staticMethod   If the method is static
     * @param parameterTypes The parameter types
     * @return The method identifier
     */
    public static MethodIdentifier of(final String className, final String methodName, final String returnType,
                                      final boolean staticMethod, final String... parameterTypes) {
        return new MethodIdentifier(className, methodName, returnType, staticMethod, parameterTypes);
    }

    /**
     * Creates an identifier of a non-static method.
     *
     * @param className      The class name
     * @param methodName     The method name
     * @param returnType     The return type
     * @param parameterTypes The parameter types
     * @return The method identifier
     */
    public static MethodIdentifier ofNonStatic(final String className, final String methodName, final String returnType, final String... parameterTypes) {
        return new MethodIdentifier(className, methodName, returnType, false, parameterTypes);
    }

    /**
     * Creates an identifier of a static method.
     *
     * @param className      The class name
     * @param methodName     The method name
     * @param returnType     The return type
     * @param parameterTypes The parameter types
     * @return The method identifier
     */
    public static MethodIdentifier ofStatic(final String className, final String methodName, final String returnType, final String... parameterTypes) {
        return new MethodIdentifier(className, methodName, returnType, true, parameterTypes);
    }

}
