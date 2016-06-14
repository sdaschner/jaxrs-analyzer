package com.sebastian_daschner.jaxrs_analyzer.analysis.classes;

import com.sebastian_daschner.jaxrs_analyzer.LogProvider;
import com.sebastian_daschner.jaxrs_analyzer.analysis.classes.annotation.*;
import com.sebastian_daschner.jaxrs_analyzer.model.JavaUtils;
import com.sebastian_daschner.jaxrs_analyzer.model.Types;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.HttpMethod;
import com.sebastian_daschner.jaxrs_analyzer.model.results.ClassResult;
import com.sebastian_daschner.jaxrs_analyzer.model.results.MethodResult;
import org.objectweb.asm.AnnotationVisitor;

import java.util.BitSet;
import java.util.List;

/**
 * @author Sebastian Daschner
 */
class JAXRSMethodVisitor extends ProjectMethodVisitor {

    private final String signature;
    private final List<String> parameters;
    private final BitSet annotatedParameters;
    private final boolean methodAnnotated;

    JAXRSMethodVisitor(final ClassResult classResult, final String className, final String desc, final String signature, final MethodResult methodResult,
                       final boolean methodAnnotated) {
        super(methodResult, className);
        this.signature = signature == null ? desc : signature;
        this.methodAnnotated = methodAnnotated;
        parameters = JavaUtils.getParameters(this.signature);
        methodResult.setOriginalMethodSignature(this.signature);
        classResult.add(methodResult);
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
        super.visitEnd();
        // determine request body parameter
        if (methodAnnotated && annotatedParameters.cardinality() != parameters.size()) {
            final String requestBodyType = parameters.get(annotatedParameters.nextClearBit(0));
            methodResult.setRequestBodyType(requestBodyType);
        }

        // TODO determine potential super methods which are annotated with JAX-RS annotations.
        if (methodResult.getHttpMethod() == null) {
            // method is a sub resource locator
            final ClassResult classResult = new ClassResult();
            methodResult.setSubResource(classResult);
        }
    }

}
