package com.sebastian_daschner.jaxrs_analyzer.analysis.classes.annotation;

import com.sebastian_daschner.jaxrs_analyzer.model.results.ClassResult;
import com.sebastian_daschner.jaxrs_analyzer.model.results.MethodResult;

import java.util.Map;

/**
 * @author Sebastian Daschner
 */
abstract class ParamAnnotationVisitor extends ClassAndMethodAnnotationVisitor<String> {

    private final String signature;

    ParamAnnotationVisitor(final MethodResult methodResult, final String signature) {
        super(methodResult);
        this.signature = signature;
    }

    ParamAnnotationVisitor(final ClassResult classResult, final String signature) {
        super(classResult);
        this.signature = signature;
    }

    @Override
    protected final void visitValue(final String value, final ClassResult classResult) {
        extractParamMap(classResult).put(value, signature);
    }

    @Override
    protected final void visitValue(final String value, final MethodResult methodResult) {
        extractParamMap(methodResult).put(value, signature);
    }

    abstract Map<String, String> extractParamMap(ClassResult classResult);

    abstract Map<String, String> extractParamMap(MethodResult methodResult);

}
