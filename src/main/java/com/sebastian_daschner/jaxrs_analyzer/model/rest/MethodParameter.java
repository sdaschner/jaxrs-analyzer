package com.sebastian_daschner.jaxrs_analyzer.model.rest;

/**
 * @author Daryl Teo
 */
public class MethodParameter {
    private String annotation;
    private String value;

    private String signature;

    private Boolean required;

    public MethodParameter(final String annotation, final String value, final String signature, final Boolean required) {
        this.annotation = annotation;
        this.value = value;
        this.signature = signature;
        this.required = required;
    }

    public String getAnnotation() {
        return this.annotation;
    }

    public String getValue() {
        return value;
    }

    public String getSignature() {
        return signature;
    }

    public Boolean isRequired() {
        return required;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MethodParameter that = (MethodParameter) o;

        if (annotation != null ? !annotation.equals(that.annotation) : that.annotation != null) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;
        if (signature != null ? !signature.equals(that.signature) : that.signature != null) return false;
        return required != null ? required.equals(that.required) : that.required == null;

    }

    @Override
    public int hashCode() {
        int result = annotation != null ? annotation.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (signature != null ? signature.hashCode() : 0);
        result = 31 * result + (required != null ? required.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MethodParameter{" +
            "annotation='" + annotation + '\'' +
            ", value='" + value + '\'' +
            ", signature='" + signature + '\'' +
            ", required=" + required +
            '}';
    }
}
