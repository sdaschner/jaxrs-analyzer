package com.sebastian_daschner.jaxrs_analyzer.analysis.classes.annotation;

import com.sebastian_daschner.jaxrs_analyzer.model.results.ClassResult;
import com.sebastian_daschner.jaxrs_analyzer.model.results.MethodResult;

/**
 * @author Sebastian Daschner
 */
public class PathAnnotationVisitor extends ClassAndMethodAnnotationVisitor<String> {

    public PathAnnotationVisitor(final ClassResult classResult) {
        super(classResult);
    }

    public PathAnnotationVisitor(final MethodResult methodResult) {
        super(methodResult);
    }

    @Override
    protected void visitValue(final String value, final ClassResult classResult) {
        classResult.setResourcePath(value);
    }

    @Override
    protected void visitValue(final String value, final MethodResult methodResult) {
        methodResult.setPath(value);
    }

}
