package com.sebastian_daschner.jaxrs_analyzer.model.instructions;

import org.objectweb.asm.Label;

/**
 * @author Sebastian Daschner
 */
public class LoadStoreInstructionPlaceholder implements Instruction {

    private final InstructionType type;
    private final int number;
    private final Label label;

    public LoadStoreInstructionPlaceholder(final InstructionType type, final int number, final Label label) {
        if (!(type == InstructionType.LOAD_PLACEHOLDER || type == InstructionType.STORE_PLACEHOLDER))
            throw new IllegalArgumentException("Only LOAD and STORE placeholders allowed!");

        this.type = type;
        this.number = number;
        this.label = label;
    }

    public int getNumber() {
        return number;
    }

    public Label getLabel() {
        return label;
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
        if (type != that.type) return false;
        return label != null ? label.equals(that.label) : that.label == null;
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + number;
        result = 31 * result + (label != null ? label.hashCode() : 0);
        return result;
    }

}
