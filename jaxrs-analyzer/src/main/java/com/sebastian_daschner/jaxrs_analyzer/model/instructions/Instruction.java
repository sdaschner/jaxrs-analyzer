package com.sebastian_daschner.jaxrs_analyzer.model.instructions;

/**
 * Represents a byte code instruction.
 *
 * @author Sebastian Daschner
 */
public interface Instruction {

    /**
     * Returns the difference of the runtime stack size when this instruction is executed.
     *
     * @return The stack size difference
     */
    public abstract int getStackSizeDifference();

    /**
     * Returns the instruction type.
     *
     * @return The type
     */
    public abstract Type getType();

    /**
     * Represents the available types of {@link Instruction}s.
     */
    public enum Type {

        PUSH, LOAD, STORE, INVOKE, RETURN, SIZE_CHANGE, GET_FIELD, GET_STATIC, NEW, DUP, THROW, METHOD_HANDLE, OTHER

    }

}
