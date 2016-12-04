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

import org.objectweb.asm.Label;

/**
 * Represents an instruction which is not covered by other implementations of
 * {@link Instruction}.
 *
 * @author Sebastian Daschner
 */
public class DefaultInstruction extends Instruction {

    private final String description;

    public DefaultInstruction(final String description, final Label label) {
        super(label);
        this.description = description;
    }

    @Override
    public InstructionType getType() {
        return InstructionType.OTHER;
    }

    @Override
    public int getStackSizeDifference() {
        return 0;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final DefaultInstruction that = (DefaultInstruction) o;

        if (getStackSizeDifference() != that.getStackSizeDifference()) return false;
        if (getType() != that.getType()) return false;

        return !(description != null ? !description.equals(that.description) : that.description != null);
    }

    @Override
    public int hashCode() {
        int result = getStackSizeDifference();
        result = 31 * result + (getType() != null ? getType().ordinal() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DefaultInstruction{" +
                "type='" + getType() + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

}
