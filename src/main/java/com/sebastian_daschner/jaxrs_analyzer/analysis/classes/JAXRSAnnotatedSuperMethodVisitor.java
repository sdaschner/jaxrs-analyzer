package com.sebastian_daschner.jaxrs_analyzer.analysis.classes;

import com.sebastian_daschner.jaxrs_analyzer.LogProvider;
import com.sebastian_daschner.jaxrs_analyzer.analysis.classes.annotation.*;
import com.sebastian_daschner.jaxrs_analyzer.model.JavaUtils;
import com.sebastian_daschner.jaxrs_analyzer.model.Types;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.HttpMethod;
import com.sebastian_daschner.jaxrs_analyzer.model.results.MethodResult;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;

import java.util.BitSet;
import java.util.List;

import static org.objectweb.asm.Opcodes.ASM5;

/**
 * @author Sebastian Daschner
 */
class JAXRSAnnotatedSuperMethodVisitor extends MethodVisitor {

    private final MethodResult methodResult;
    private final List<String> parameters;
    private final BitSet annotatedParameters;

    JAXRSAnnotatedSuperMethodVisitor(final MethodResult methodResult) {
        super(ASM5);
        this.methodResult = methodResult;
        parameters = JavaUtils.getParameters(methodResult.getOriginalMethodSignature());
        annotatedParameters = new BitSet(parameters.size());
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        switch (desc) {
            case Types.GET:
                methodResult.setHttpMethod(HttpMethod.GET);
                break;
            case Types.POST:
                methodResult.setHttpMethod(HttpMethod.POST);
                break;
            case Types.PUT:
                methodResult.setHttpMethod(HttpMethod.PUT);
                break;
            case Types.DELETE:
                methodResult.setHttpMethod(HttpMethod.DELETE);
                break;
            case Types.HEAD:
                methodResult.setHttpMethod(HttpMethod.HEAD);
                break;
            case Types.OPTIONS:
                methodResult.setHttpMethod(HttpMethod.OPTIONS);
                break;
            case Types.PATH:
                return new PathAnnotationVisitor(methodResult);
            case Types.CONSUMES:
                return new ConsumesAnnotationVisitor(methodResult);
            case Types.PRODUCES:
                return new ProducesAnnotationVisitor(methodResult);
        }
        return null;
    }

    @Override
    public AnnotationVisitor visitParameterAnnotation(int parameter, String annotationDesc, boolean visible) {
        final String parameterType = parameters.get(parameter);
        annotatedParameters.set(parameter);

        switch (annotationDesc) {
            case Types.PATH_PARAM:
            case Types.QUERY_PARAM:
            case Types.HEADER_PARAM:
            case Types.FORM_PARAM:
            case Types.COOKIE_PARAM:
            case Types.MATRIX_PARAM:
                annotatedParameters.set(parameter);
                return new ParamAnnotationVisitor(methodResult, parameter, annotationDesc, parameterType);
            case Types.DEFAULT_VALUE:
                return new DefaultValueAnnotationVisitor(methodResult, parameter, annotationDesc, parameterType);
            case Types.SUSPENDED:
                LogProvider.debug("Handling of " + annotationDesc + " not yet implemented");
            case Types.CONTEXT:
                annotatedParameters.set(parameter);
            default:
                return null;
        }
    }

    @Override
    public void visitEnd() {
        if (annotatedParameters.cardinality() != parameters.size()) {
            final String requestBodyType = parameters.get(annotatedParameters.nextClearBit(0));
            methodResult.setRequestBodyType(requestBodyType);
        }
    }

}
