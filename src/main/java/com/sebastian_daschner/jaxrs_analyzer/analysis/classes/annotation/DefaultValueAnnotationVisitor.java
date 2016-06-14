package com.sebastian_daschner.jaxrs_analyzer.analysis.classes.annotation;

import com.sebastian_daschner.jaxrs_analyzer.model.rest.MethodParameter;
import com.sebastian_daschner.jaxrs_analyzer.model.results.ClassResult;
import com.sebastian_daschner.jaxrs_analyzer.model.results.MethodResult;

/**
 * @author Daryl Teo
 */
public class DefaultValueAnnotationVisitor extends ClassAndMethodAnnotationVisitor<String> {
    private final Integer index;
    private final String fieldName;

    private final String signature;
    private final String annotation;

    public DefaultValueAnnotationVisitor(final ClassResult classResult, final String fieldName, final String annotation, final String signature) {
        super(classResult);
        this.index = null;
        this.fieldName = fieldName;
        this.signature = signature;
        this.annotation = annotation;
    }

    public DefaultValueAnnotationVisitor(final MethodResult methodResult, final Integer index, final String annotation, final String signature) {
        super(methodResult);
        this.index = index;
        this.fieldName = null;
        this.signature = signature;
        this.annotation = annotation;
    }

    @Override
    protected void visitValue(String value, ClassResult classResult) {
        // only support field annotations
        if (fieldName == null) {
            return;
        }

        MethodParameter parameter = classResult.getClassFields().getParameter(fieldName);

        if (parameter == null) {
            parameter = new MethodParameter(null, null, null, false);
        } else {
            parameter = new MethodParameter(parameter.getAnnotation(), parameter.getValue(), parameter.getSignature(), false);
        }

        classResult.getClassFields().setParameter(fieldName, parameter);

        return;
    }

    @Override
    protected void visitValue(String value, MethodResult methodResult) {
        // only support method parameter annotations
        if (this.index == null) {
            return;
        }

        MethodParameter parameter = methodResult.getMethodParameters().getParameter(index);

        if (parameter == null) {
            parameter = new MethodParameter(null, null, null, false);
        } else {
            parameter = new MethodParameter(parameter.getAnnotation(), parameter.getValue(), parameter.getSignature(), false);
        }

        methodResult.getMethodParameters().setParameter(index, parameter);
    }

}
