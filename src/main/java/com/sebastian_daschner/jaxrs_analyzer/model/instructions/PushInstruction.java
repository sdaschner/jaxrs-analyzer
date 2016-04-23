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

/**
 * Represents any instruction which pushes a constant value to the stack.
 *
 * @author Sebastian Daschner
 */
public class PushInstruction implements Instruction {

    private final Object value;
    private final String valueType;

    public PushInstruction(final Object value, final String valueType) {
        this.value = value;
        this.valueType = valueType;
    }

    public Object getValue() {
        return value;
    }

    public String getValueType() {
        return valueType;
    }

    @Override
    public InstructionType getType() {
        return InstructionType.PUSH;
    }

    @Override
    public int getStackSizeDifference() {
        return 1;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final PushInstruction that = (PushInstruction) o;

        if (value != null ? !value.equals(that.value) : that.value != null) return false;
        return valueType != null ? valueType.equals(that.valueType) : that.valueType == null;
    }

    @Override
    public int hashCode() {
        int result = value != null ? value.hashCode() : 0;
        result = 31 * result + (valueType != null ? valueType.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PushInstruction{" +
                "value=" + value +
                ", valueType='" + valueType + '\'' +
                '}';
    }

}
