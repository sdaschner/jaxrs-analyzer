package com.sebastian_daschner.jaxrs_analyzer.model.instructions;

/**
 * Represents a GET_STATIC instruction.
 *
 * @author Sebastian Daschner
 */
public class GetStaticInstruction extends GetPropertyInstruction {

    public GetStaticInstruction(final String className, final String fieldName, final String fieldType) {
        super(className, fieldName, fieldType);
    }

    @Override
    public Type getType() {
        return Type.GET_STATIC;
    }

    @Override
    public int getStackSizeDifference() {
        return 1;
    }

}
