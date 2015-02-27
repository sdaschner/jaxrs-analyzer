package com.sebastian_daschner.jaxrs_analyzer.model.instructions;

/**
 * Represents an instruction which is not covered by other implementations of
 * {@link Instruction}.
 *
 * @author Sebastian Daschner
 */
public class DefaultInstruction implements Instruction {

    private final String description;

    public DefaultInstruction(final String description) {
        this.description = description;
    }

    @Override
    public Type getType() {
        return Type.OTHER;
    }

    @Override
    public int getStackSizeDifference() {
        return 0;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final DefaultInstruction that = (DefaultInstruction) o;

        if (getStackSizeDifference() != that.getStackSizeDifference()) return false;
        if (getType() != that.getType()) return false;

        return !(description != null ? !description.equals(that.description) : that.description != null);
    }

    @Override
    public int hashCode() {
        int result = getStackSizeDifference();
        result = 31 * result + (getType() != null ? getType().ordinal() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DefaultInstruction{" +
                "type='" + getType() + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

}
