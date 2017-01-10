package com.sebastian_daschner.jaxrs_analyzer.analysis.classes;

import com.sebastian_daschner.jaxrs_analyzer.LogProvider;
import com.sebastian_daschner.jaxrs_analyzer.analysis.classes.annotation.*;
import com.sebastian_daschner.jaxrs_analyzer.model.Types;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.HttpMethod;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.MethodParameter;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.ParameterType;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeIdentifier;
import com.sebastian_daschner.jaxrs_analyzer.model.results.MethodResult;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;

import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.objectweb.asm.Opcodes.ASM5;

/**
 * @author Sebastian Daschner
 */
class JAXRSAnnotatedSuperMethodVisitor extends MethodVisitor {

    private final MethodResult methodResult;
    private final List<String> parameterTypes;
    private final Map<Integer, MethodParameter> methodParameters;
    private final BitSet annotatedParameters;

    JAXRSAnnotatedSuperMethodVisitor(final MethodResult methodResult) {
        super(ASM5);
        this.methodResult = methodResult;
        parameterTypes = methodResult.getOriginalMethodSignature().getParameters();
        annotatedParameters = new BitSet(parameterTypes.size());
        methodParameters = new HashMap<>();
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
            case Types.DEPRECATED:
                methodResult.setDeprecated(true);
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
    public AnnotationVisitor visitParameterAnnotation(final int index, final String annotationDesc, final boolean visible) {
        switch (annotationDesc) {
            case Types.PATH_PARAM:
                return paramAnnotationVisitor(index, ParameterType.PATH);
            case Types.QUERY_PARAM:
                return paramAnnotationVisitor(index, ParameterType.QUERY);
            case Types.HEADER_PARAM:
                return paramAnnotationVisitor(index, ParameterType.HEADER);
            case Types.FORM_PARAM:
                return paramAnnotationVisitor(index, ParameterType.FORM);
            case Types.COOKIE_PARAM:
                return paramAnnotationVisitor(index, ParameterType.COOKIE);
            case Types.MATRIX_PARAM:
                return paramAnnotationVisitor(index, ParameterType.MATRIX);
            case Types.DEFAULT_VALUE:
                return defaultAnnotationVisitor(index);
            case Types.SUSPENDED:
                LogProvider.debug("Handling of " + annotationDesc + " not yet implemented");
            case Types.CONTEXT:
                annotatedParameters.set(index);
            default:
                return null;
        }
    }

    private AnnotationVisitor paramAnnotationVisitor(final int index, final ParameterType parameterType) {
        annotatedParameters.set(index);
        final String type = parameterTypes.get(index);

        MethodParameter methodParameter = methodParameters.get(index);
        if (methodParameter == null) {
            methodParameter = new MethodParameter(TypeIdentifier.ofType(type), parameterType);
            methodParameters.put(index, methodParameter);
        } else {
            methodParameter.setParameterType(parameterType);
        }

        return new ParamAnnotationVisitor(methodParameter);
    }

    private AnnotationVisitor defaultAnnotationVisitor(final int index) {
        final String type = parameterTypes.get(index);

        MethodParameter methodParameter = methodParameters.get(index);
        if (methodParameter == null) {
            methodParameter = new MethodParameter(TypeIdentifier.ofType(type));
            methodParameters.put(index, methodParameter);
        }

        return new DefaultValueAnnotationVisitor(methodParameter);
    }

    @Override
    public void visitEnd() {
        if (annotatedParameters.cardinality() != parameterTypes.size()) {
            final String requestBodyType = parameterTypes.get(annotatedParameters.nextClearBit(0));
            methodResult.setRequestBodyType(requestBodyType);
        }
        methodResult.getMethodParameters().addAll(methodParameters.values());
    }

}
