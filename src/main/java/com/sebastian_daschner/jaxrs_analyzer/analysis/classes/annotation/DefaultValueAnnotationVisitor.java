package com.sebastian_daschner.jaxrs_analyzer.analysis.classes.annotation;

import com.sebastian_daschner.jaxrs_analyzer.model.results.ClassResult;
import com.sebastian_daschner.jaxrs_analyzer.model.results.MethodResult;

/**
 * @author Daryl Teo
 */
public class DefaultValueAnnotationVisitor extends ClassAndMethodAnnotationVisitor<String> {
    private final Integer parameterIndex;

    public DefaultValueAnnotationVisitor(final ClassResult classResult, final Integer parameterIndex) {
        super(classResult);
        throw new UnsupportedOperationException("DefaultValue is not supported on Classes");
    }

    public DefaultValueAnnotationVisitor(final MethodResult methodResult, final Integer parameterIndex) {
        super(methodResult);
        this.parameterIndex = parameterIndex;
    }

    @Override
    protected void visitValue(String value, ClassResult classResult) {
        throw new UnsupportedOperationException("DefaultValue is not supported on Classes");
    }

    @Override
    protected void visitValue(String value, MethodResult methodResult) {
        methodResult.getMethodParameters().getDefaultValues().put(parameterIndex, value);
    }

}
