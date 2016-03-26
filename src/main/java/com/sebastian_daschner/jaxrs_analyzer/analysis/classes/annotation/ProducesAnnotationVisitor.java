package com.sebastian_daschner.jaxrs_analyzer.analysis.classes.annotation;

import com.sebastian_daschner.jaxrs_analyzer.model.results.ClassResult;
import com.sebastian_daschner.jaxrs_analyzer.model.results.MethodResult;

/**
 * @author Sebastian Daschner
 */
public class ProducesAnnotationVisitor extends ClassAndMethodAnnotationVisitor<String> {

    public ProducesAnnotationVisitor(final ClassResult classResult) {
        super(classResult);
    }

    public ProducesAnnotationVisitor(final MethodResult methodResult) {
        super(methodResult);
    }

    @Override
    protected void visitValue(final String value, final ClassResult classResult) {
        classResult.getResponseMediaTypes().add(value);
    }

    @Override
    protected void visitValue(final String value, final MethodResult methodResult) {
        methodResult.getResponseMediaTypes().add(value);
    }

}
