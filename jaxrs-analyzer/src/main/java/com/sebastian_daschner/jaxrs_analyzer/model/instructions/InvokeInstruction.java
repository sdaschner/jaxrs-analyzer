package com.sebastian_daschner.jaxrs_analyzer.model.instructions;

import com.sebastian_daschner.jaxrs_analyzer.model.methods.MethodIdentifier;

/**
 * Represents an INVOKE_XYZ instruction.
 *
 * @author Sebastian Daschner
 */
public class InvokeInstruction implements Instruction {

    private final MethodIdentifier identifier;

    public InvokeInstruction(final MethodIdentifier identifier) {
        this.identifier = identifier;
    }

    @Override
    public int getStackSizeDifference() {
        int difference = identifier.getReturnType() == null ? -1 : 0;

        if (identifier.isStaticMethod())
            difference++;

        for (final String parameterType : identifier.getParameterTypes()) {
            difference--;
        }

        return difference;
    }

    @Override
    public Type getType() {
        return Type.INVOKE;
    }

    public MethodIdentifier getIdentifier() {
        return identifier;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final InvokeInstruction that = (InvokeInstruction) o;

        return identifier.equals(that.identifier);
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }

    @Override
    public String toString() {
        return "InvokeInstruction{" +
                "identifier=" + identifier + '}';
    }

}
