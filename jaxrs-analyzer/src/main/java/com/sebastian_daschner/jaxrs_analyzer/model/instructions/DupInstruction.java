package com.sebastian_daschner.jaxrs_analyzer.model.instructions;

/**
 * Represents a DUP instruction.
 *
 * @author Sebastian Daschner
 */
public class DupInstruction implements Instruction {

    @Override
    public int getStackSizeDifference() {
        return 1;
    }

    @Override
    public Type getType() {
        return Type.DUP;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        return o != null && getClass() == o.getClass();

    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public String toString() {
        return "DupInstruction{}";
    }

}
