package com.sebastian_daschner.jaxrs_analyzer.analysis.classes.annotation;

import com.sebastian_daschner.jaxrs_analyzer.model.results.ClassResult;
import com.sebastian_daschner.jaxrs_analyzer.model.results.MethodResult;

import java.util.Map;

/**
 * @author Sebastian Daschner
 */
public class QueryParamAnnotationVisitor extends ParamAnnotationVisitor {

    public QueryParamAnnotationVisitor(final ClassResult classResult, final String signature) {
        super(classResult, signature);
    }

    public QueryParamAnnotationVisitor(final MethodResult methodResult, final String signature) {
        super(methodResult, signature);
    }

    @Override
    Map<String, String> extractParamMap(final ClassResult classResult) {
        return classResult.getClassFields().getQueryParams();
    }

    @Override
    Map<String, String> extractParamMap(final MethodResult methodResult) {
        return methodResult.getMethodParameters().getQueryParams();
    }

}
