package com.sebastian_daschner.jaxrs_analyzer.model.rest;

/**
 * Represents a single resource method parameter (like query parameter, header field, path parameter, etc.).
 *
 * @author Daryl Teo
 * @author Sebastian Daschner
 */
public class MethodParameter {

    private String type;
    private ParameterType parameterType;
    private String name;
    private String defaultValue;

    public MethodParameter(final String type) {
        this.type = type;
    }

    public MethodParameter(final String type, final ParameterType parameterType) {
        this.type = type;
        this.parameterType = parameterType;
    }

    public MethodParameter(final String type, final ParameterType parameterType, final String name, final String defaultValue) {
        this.type = type;
        this.parameterType = parameterType;
        this.name = name;
        this.defaultValue = defaultValue;
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

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
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

        if (parameterType != that.parameterType) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        return defaultValue != null ? defaultValue.equals(that.defaultValue) : that.defaultValue == null;

    }

    @Override
    public int hashCode() {
        int result = parameterType != null ? parameterType.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (defaultValue != null ? defaultValue.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MethodParameter{" +
                "defaultValue='" + defaultValue + '\'' +
                ", type='" + type + '\'' +
                ", parameterType=" + parameterType +
                ", name='" + name + '\'' +
                '}';
    }

}
