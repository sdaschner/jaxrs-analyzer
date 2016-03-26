package com.sebastian_daschner.jaxrs_analyzer.model.instructions;

import org.objectweb.asm.Label;

import java.util.Objects;

/**
 * @author Sebastian Daschner
 */
public class LoadStoreInstructionPlaceholder implements Instruction {

    private final InstructionType type;
    private final int number;
    private final Label label;

    public LoadStoreInstructionPlaceholder(final InstructionType type, final int number, final Label label) {
        Objects.requireNonNull(label);
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LoadStoreInstructionPlaceholder that = (LoadStoreInstructionPlaceholder) o;

        if (number != that.number) return false;
        return label.equals(that.label);
    }

    @Override
    public int hashCode() {
        int result = number;
        result = 31 * result + label.hashCode();
        return result;
    }
}
