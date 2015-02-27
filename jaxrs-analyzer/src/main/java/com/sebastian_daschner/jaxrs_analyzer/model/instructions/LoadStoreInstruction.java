package com.sebastian_daschner.jaxrs_analyzer.model.instructions;

import com.sebastian_daschner.jaxrs_analyzer.analysis.utils.StringUtils;

/**
 * Represents any LOAD or STORE instruction.
 *
 * @author Sebastian Daschner
 */
public abstract class LoadStoreInstruction implements Instruction {

    private final int number;
    private final String variableType;
    private final String name;

    protected LoadStoreInstruction(final int number, final String variableType, final String name) {
        StringUtils.requireNonBlank(variableType);
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
