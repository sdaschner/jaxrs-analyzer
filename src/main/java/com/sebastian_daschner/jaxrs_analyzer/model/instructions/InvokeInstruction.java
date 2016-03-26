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

import com.sebastian_daschner.jaxrs_analyzer.model.Types;
import com.sebastian_daschner.jaxrs_analyzer.model.methods.MethodIdentifier;

/**
 * Represents an INVOKE_XYZ instruction.
 *
 * @author Sebastian Daschner
 */
public class InvokeInstruction implements Instruction {

    private final MethodIdentifier identifier;

    public InvokeInstruction(final MethodIdentifier identifier) {
        this.identifier = identifier;
    }

    @Override
    public int getStackSizeDifference() {
        int difference = Types.PRIMITIVE_VOID.equals(identifier.getReturnType()) ? -1 : 0;

        if (identifier.isStaticMethod())
            difference++;

        difference -= identifier.getParameters();

        return difference;
    }

    @Override
    public InstructionType getType() {
        return InstructionType.INVOKE;
    }

    public MethodIdentifier getIdentifier() {
        return identifier;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final InvokeInstruction that = (InvokeInstruction) o;

        return identifier.equals(that.identifier);
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }

    @Override
    public String toString() {
        return "InvokeInstruction{" +
                "identifier=" + identifier + '}';
    }

}
