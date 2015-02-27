package com.sebastian_daschner.jaxrs_analyzer.model.instructions;

/**
 * Represents a GET_FIELD instruction.
 *
 * @author Sebastian Daschner
 */
public class GetFieldInstruction extends GetPropertyInstruction {

    public GetFieldInstruction(final String className, final String fieldName, final String fieldType) {
        super(className, fieldName, fieldType);
    }

    @Override
    public Type getType() {
        return Type.GET_FIELD;
    }

    @Override
    public int getStackSizeDifference() {
        return 0;
    }

}
