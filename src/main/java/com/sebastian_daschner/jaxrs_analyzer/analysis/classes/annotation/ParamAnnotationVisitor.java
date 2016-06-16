package com.sebastian_daschner.jaxrs_analyzer.analysis.classes.annotation;

import com.sebastian_daschner.jaxrs_analyzer.model.rest.MethodParameter;

/**
 * @author Sebastian Daschner
 */
public class ParamAnnotationVisitor extends ValueAnnotationVisitor {

    private final MethodParameter parameter;

    public ParamAnnotationVisitor(final MethodParameter parameter) {
        this.parameter = parameter;
    }

    @Override
    protected void visitValue(final String value) {
        parameter.setName(value);
    }

}
