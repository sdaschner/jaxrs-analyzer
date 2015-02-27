package com.sebastian_daschner.jaxrs_analyzer.model.instructions;

/**
 * Represents any instruction which pushes a constant value to the stack.
 *
 * @author Sebastian Daschner
 */
public class PushInstruction implements Instruction {

    private final Object value;

    public PushInstruction(final Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public Type getType() {
        return Type.PUSH;
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

        return !(value != null ? !value.equals(that.value) : that.value != null);
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "PushInstruction{" +
                "value=" + value +
                '}';
    }

}
