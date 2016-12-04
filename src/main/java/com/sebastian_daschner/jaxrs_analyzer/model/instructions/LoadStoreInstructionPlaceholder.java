package com.sebastian_daschner.jaxrs_analyzer.model.instructions;

import org.objectweb.asm.Label;

/**
 * @author Sebastian Daschner
 */
public class LoadStoreInstructionPlaceholder extends Instruction {

    private final InstructionType type;
    private final int number;

    public LoadStoreInstructionPlaceholder(final InstructionType type, final int number, final Label label) {
        super(label);
        if (!(type == InstructionType.LOAD_PLACEHOLDER || type == InstructionType.STORE_PLACEHOLDER))
            throw new IllegalArgumentException("Only LOAD and STORE placeholders allowed!");

        this.type = type;
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    @Override
    public int getStackSizeDifference() {
        throw new UnsupportedOperationException();
    }

    @Override
    public InstructionType getType() {
        return type;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final LoadStoreInstructionPlaceholder that = (LoadStoreInstructionPlaceholder) o;

        if (number != that.number) return false;
        return type == that.type;
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + number;
        return result;
    }

}
