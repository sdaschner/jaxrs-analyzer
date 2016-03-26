package com.sebastian_daschner.jaxrs_analyzer.analysis.classes.annotation;

import com.sebastian_daschner.jaxrs_analyzer.model.results.ClassResult;
import com.sebastian_daschner.jaxrs_analyzer.model.results.MethodResult;

/**
 * @author Sebastian Daschner
 */
public class PathParamAnnotationVisitor extends ClassAndMethodAnnotationVisitor<String> {

    private final String signature;

    public PathParamAnnotationVisitor(final ClassResult classResult, final String signature) {
        super(classResult);
        this.signature = signature;
    }

    public PathParamAnnotationVisitor(final MethodResult methodResult, final String signature) {
        super(methodResult);
        this.signature = signature;
    }

    @Override
    protected void visitValue(String value, ClassResult classResult) {
        classResult.getClassFields().getPathParams().put(value, signature);
    }

    @Override
    protected void visitValue(String value, MethodResult methodResult) {
        methodResult.getMethodParameters().getPathParams().put(value, signature);
    }

}
