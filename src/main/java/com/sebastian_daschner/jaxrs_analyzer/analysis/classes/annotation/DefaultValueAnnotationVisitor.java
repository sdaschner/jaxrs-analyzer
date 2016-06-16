package com.sebastian_daschner.jaxrs_analyzer.analysis.classes.annotation;

import com.sebastian_daschner.jaxrs_analyzer.model.rest.MethodParameter;

/**
 * Visits the {@link javax.ws.rs.DefaultValue} annotation and sets the configured value.
 *
 * @author Daryl Teo
 * @author Sebastian Daschner
 */
public class DefaultValueAnnotationVisitor extends ValueAnnotationVisitor {

    private final MethodParameter parameter;

    public DefaultValueAnnotationVisitor(final MethodParameter parameter) {
        this.parameter = parameter;
    }

    @Override
    protected void visitValue(String value) {
        parameter.setDefaultValue(value);
    }

}
