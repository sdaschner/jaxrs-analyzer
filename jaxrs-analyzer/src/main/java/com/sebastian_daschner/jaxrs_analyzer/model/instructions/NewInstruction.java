package com.sebastian_daschner.jaxrs_analyzer.model.instructions;

import com.sebastian_daschner.jaxrs_analyzer.analysis.utils.StringUtils;

/**
 * Represents a NEW instruction.
 *
 * @author Sebastian Daschner
 */
public class NewInstruction implements Instruction {

    private final String createdType;

    public NewInstruction(final String createdType) {
        StringUtils.requireNonBlank(createdType);

        this.createdType = createdType;
    }

    public String getCreatedType() {
        return createdType;
    }

    @Override
    public int getStackSizeDifference() {
        return 1;
    }

    @Override
    public Type getType() {
        return Type.NEW;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final NewInstruction that = (NewInstruction) o;

        return createdType.equals(that.createdType);
    }

    @Override
    public int hashCode() {
        return createdType.hashCode();
    }

    @Override
    public String toString() {
        return "NewInstruction{" +
                "createdType='" + createdType + '\'' +
                '}';
    }

}
