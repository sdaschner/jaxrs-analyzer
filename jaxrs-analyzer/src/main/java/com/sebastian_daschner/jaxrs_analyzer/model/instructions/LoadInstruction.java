package com.sebastian_daschner.jaxrs_analyzer.model.instructions;

/**
 * Represents a XZY_LOAD_X instruction (for any number).
 *
 * @author Sebastian Daschner
 */
public class LoadInstruction extends LoadStoreInstruction {

    public LoadInstruction(final int number, final String type, final String name) {
        super(number, type, name);
    }

    @Override
    public int getStackSizeDifference() {
        return 1;
    }

    @Override
    public Type getType() {
        return Type.LOAD;
    }

}
