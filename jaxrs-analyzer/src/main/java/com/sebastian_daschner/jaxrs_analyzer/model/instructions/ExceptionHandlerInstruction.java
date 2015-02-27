package com.sebastian_daschner.jaxrs_analyzer.model.instructions;

/**
 * Represents a dummy instruction which acts as an exception handler.
 *
 * @author Sebastian Daschner
 */
public class ExceptionHandlerInstruction extends SizeChangingInstruction {

    private static final String DESCRIPTION = "pseudoExceptionHandler";

    public ExceptionHandlerInstruction() {
        super(DESCRIPTION, 1, 0);
    }

    @Override
    public String toString() {
        return "ExceptionHandlerInstruction{}";
    }

}
