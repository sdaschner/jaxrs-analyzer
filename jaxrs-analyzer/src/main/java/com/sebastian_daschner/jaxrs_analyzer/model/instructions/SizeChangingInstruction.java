package com.sebastian_daschner.jaxrs_analyzer.model.instructions;

/**
 * Represents any instruction which changes the stack elements and is not covered
 * by other implementations of {@link Instruction}.
 *
 * @author Sebastian Daschner
 */
public class SizeChangingInstruction extends DefaultInstruction {

    private final int numberOfPushes;
    private final int numberOfPops;

    public SizeChangingInstruction(final String description, final int numberOfPushes, final int numberOfPops) {
        super(description);

        if (numberOfPushes < 0 || numberOfPops < 0)
            throw new IllegalArgumentException("Number of pushes and pops cannot be negative");

        this.numberOfPushes = numberOfPushes;
        this.numberOfPops = numberOfPops;
    }

    public int getNumberOfPushes() {
        return numberOfPushes;
    }

    public int getNumberOfPops() {
        return numberOfPops;
    }

    @Override
    public Type getType() {
        return Type.SIZE_CHANGE;
    }

    @Override
    public int getStackSizeDifference() {
        return numberOfPushes - numberOfPops;
    }

}