package com.sebastian_daschner.jaxrs_analyzer.model.instructions;

import com.sebastian_daschner.jaxrs_analyzer.model.methods.MethodIdentifier;

import java.util.Objects;

/**
 * Represents an INVOKE_DYNAMIC instruction which will push a method handle on the stack.
 *
 * @author Sebastian Daschner
 */
public class InvokeDynamicInstruction extends InvokeInstruction {

    private final MethodIdentifier dynamicIdentifier;

    public InvokeDynamicInstruction(final MethodIdentifier methodHandleIdentifier, final MethodIdentifier dynamicIdentifier) {
        super(methodHandleIdentifier);
        Objects.requireNonNull(dynamicIdentifier);
        this.dynamicIdentifier = dynamicIdentifier;
    }

    @Override
    public int getStackSizeDifference() {
        // the method handle will be pushed on the stack
        int difference = 1;

        for (final String parameterType : dynamicIdentifier.getParameterTypes())
            difference--;

        return difference;
    }

    public MethodIdentifier getDynamicIdentifier() {
        return dynamicIdentifier;
    }

    @Override
    public Type getType() {
        return Type.METHOD_HANDLE;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final InvokeDynamicInstruction that = (InvokeDynamicInstruction) o;

        return dynamicIdentifier.equals(that.dynamicIdentifier);
    }

    @Override
    public int hashCode() {
        return dynamicIdentifier.hashCode();
    }

    @Override
    public String toString() {
        return "InvokeDynamicInstruction{" +
                "dynamicIdentifier='" + dynamicIdentifier + '\'' +
                ", identifier='" + getIdentifier() + '\'' +
                '}';
    }

}
