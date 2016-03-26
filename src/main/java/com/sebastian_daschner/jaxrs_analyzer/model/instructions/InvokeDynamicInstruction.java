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

package com.sebastian_daschner.jaxrs_analyzer.model.instructions;

import com.sebastian_daschner.jaxrs_analyzer.model.methods.MethodIdentifier;

import java.util.Objects;

/**
 * Represents an INVOKE_DYNAMIC instruction which will push a method handle on the stack.
 *
 * @author Sebastian Daschner
 */
public class InvokeDynamicInstruction extends InvokeInstruction {

    private final MethodIdentifier dynamicIdentifier;

    public InvokeDynamicInstruction(final MethodIdentifier methodHandleIdentifier, final MethodIdentifier dynamicIdentifier) {
        super(methodHandleIdentifier);
        Objects.requireNonNull(dynamicIdentifier);
        this.dynamicIdentifier = dynamicIdentifier;
    }

    @Override
    public int getStackSizeDifference() {
        // the method handle will be pushed on the stack
        return 1 - dynamicIdentifier.getParameters();
    }

    public MethodIdentifier getDynamicIdentifier() {
        return dynamicIdentifier;
    }

    @Override
    public InstructionType getType() {
        return InstructionType.METHOD_HANDLE;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final InvokeDynamicInstruction that = (InvokeDynamicInstruction) o;

        return dynamicIdentifier.equals(that.dynamicIdentifier);
    }

    @Override
    public int hashCode() {
        return dynamicIdentifier.hashCode();
    }

    @Override
    public String toString() {
        return "InvokeDynamicInstruction{" +
                "dynamicIdentifier='" + dynamicIdentifier + '\'' +
                ", identifier='" + getIdentifier() + '\'' +
                '}';
    }

}
