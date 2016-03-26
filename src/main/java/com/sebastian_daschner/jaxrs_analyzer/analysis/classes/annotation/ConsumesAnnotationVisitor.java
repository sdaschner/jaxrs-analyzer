package com.sebastian_daschner.jaxrs_analyzer.analysis.classes.annotation;

import com.sebastian_daschner.jaxrs_analyzer.model.results.ClassResult;
import com.sebastian_daschner.jaxrs_analyzer.model.results.MethodResult;

/**
 * @author Sebastian Daschner
 */
public class ConsumesAnnotationVisitor extends ClassAndMethodAnnotationVisitor<String> {

    public ConsumesAnnotationVisitor(final ClassResult classResult) {
        super(classResult);
    }

    public ConsumesAnnotationVisitor(final MethodResult methodResult) {
        super(methodResult);
    }

    @Override
    protected void visitValue(final String value, final ClassResult classResult) {
        classResult.getRequestMediaTypes().add(value);
    }

    @Override
    protected void visitValue(final String value, final MethodResult methodResult) {
        methodResult.getRequestMediaTypes().add(value);
    }

}
