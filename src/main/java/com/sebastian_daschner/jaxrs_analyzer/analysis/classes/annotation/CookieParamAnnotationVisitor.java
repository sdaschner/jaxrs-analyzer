package com.sebastian_daschner.jaxrs_analyzer.analysis.classes.annotation;

import com.sebastian_daschner.jaxrs_analyzer.model.results.ClassResult;
import com.sebastian_daschner.jaxrs_analyzer.model.results.MethodResult;

import java.util.Map;

/**
 * @author Sebastian Daschner
 */
public class CookieParamAnnotationVisitor extends ParamAnnotationVisitor {

    public CookieParamAnnotationVisitor(final ClassResult classResult, final String signature) {
        super(classResult, signature);
    }

    public CookieParamAnnotationVisitor(final MethodResult methodResult, final String signature) {
        super(methodResult, signature);
    }

    @Override
    Map<String, String> extractParamMap(final ClassResult classResult) {
        return classResult.getClassFields().getCookieParams();
    }

    @Override
    Map<String, String> extractParamMap(final MethodResult methodResult) {
        return methodResult.getMethodParameters().getCookieParams();
    }

}
