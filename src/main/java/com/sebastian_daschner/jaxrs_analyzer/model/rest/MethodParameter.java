package com.sebastian_daschner.jaxrs_analyzer.model.rest;

/**
 * Represents a single resource method parameter (like query parameter, header field, path parameter, etc.).
 *
 * @author Daryl Teo
 * @author Sebastian Daschner
 */
public class MethodParameter {

    /**
     * The type of the Java property (field or parameter).
     * Only String, primitive types, enums or collections of these (no nested levels) are allowed.
     */
    private TypeIdentifier type;
    private ParameterType parameterType;
    private String name;
    private String description;
    private String defaultValue;

    public MethodParameter(final TypeIdentifier type) {
        this.type = type;
    }

    public MethodParameter(final TypeIdentifier type, final ParameterType parameterType) {
        this.type = type;
        this.parameterType = parameterType;
    }

    public TypeIdentifier getType() {
        return type;
    }

    public void setType(final TypeIdentifier type) {
        this.type = type;
    }

    public ParameterType getParameterType() {
        return parameterType;
    }

    public void setParameterType(final ParameterType parameterType) {
        this.parameterType = parameterType;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(final String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final MethodParameter that = (MethodParameter) o;

        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (parameterType != that.parameterType) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        return defaultValue != null ? defaultValue.equals(that.defaultValue) : that.defaultValue == null;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (parameterType != null ? parameterType.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (defaultValue != null ? defaultValue.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MethodParameter{" +
                "type=" + type +
                ", parameterType=" + parameterType +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", defaultValue='" + defaultValue + '\'' +
                '}';
    }

}
