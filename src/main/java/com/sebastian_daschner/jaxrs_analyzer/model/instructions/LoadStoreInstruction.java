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

import com.sebastian_daschner.jaxrs_analyzer.utils.StringUtils;

import java.util.Objects;

/**
 * Represents any LOAD or STORE instruction.
 *
 * @author Sebastian Daschner
 */
public abstract class LoadStoreInstruction implements Instruction {

    /**
     * The variable name which is taken, if no information is found in the local variables attribute.
     */
    private static final String UNKNOWN_VARIABLE_NAME_PREFIX = "variable$";

    private final int number;
    private final String variableType;
    private final String name;

    protected LoadStoreInstruction(final int number, final String variableType) {
        Objects.requireNonNull(variableType);

        this.number = number;
        this.variableType = variableType;
        name = UNKNOWN_VARIABLE_NAME_PREFIX + number;
    }

    protected LoadStoreInstruction(final int number, final String variableType, final String name) {
        Objects.requireNonNull(variableType);
        StringUtils.requireNonBlank(name);

        this.number = number;
        this.variableType = variableType;
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public String getVariableType() {
        return variableType;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final LoadStoreInstruction that = (LoadStoreInstruction) o;

        if (number != that.number) return false;
        if (!name.equals(that.name)) return false;
        if (!variableType.equals(that.variableType)) return false;
        if (getStackSizeDifference() != that.getStackSizeDifference()) return false;

        return getType() == that.getType();
    }

    @Override
    public int hashCode() {
        int result = number;
        result = 31 * result + getStackSizeDifference();
        result = 31 * result + (getType() != null ? getType().ordinal() : 0);
        result = 31 * result + variableType.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "LoadStoreInstruction{" +
                "type='" + getType() + '\'' +
                ", number=" + number + '\'' +
                ", variableType=" + variableType + '\'' +
                ", name=" + name + '}';
    }

}
