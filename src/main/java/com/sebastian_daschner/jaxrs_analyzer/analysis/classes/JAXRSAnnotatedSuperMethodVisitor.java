package com.sebastian_daschner.jaxrs_analyzer.analysis.classes;

import com.sebastian_daschner.jaxrs_analyzer.analysis.classes.annotation.ConsumesAnnotationVisitor;
import com.sebastian_daschner.jaxrs_analyzer.analysis.classes.annotation.PathAnnotationVisitor;
import com.sebastian_daschner.jaxrs_analyzer.analysis.classes.annotation.ProducesAnnotationVisitor;
import com.sebastian_daschner.jaxrs_analyzer.model.Types;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.HttpMethod;
import com.sebastian_daschner.jaxrs_analyzer.model.results.MethodResult;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.ASM5;

/**
 * @author Sebastian Daschner
 */
class JAXRSAnnotatedSuperMethodVisitor extends MethodVisitor {

    private final MethodResult methodResult;

    JAXRSAnnotatedSuperMethodVisitor(final MethodResult methodResult) {
        super(ASM5);
        this.methodResult = methodResult;
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

}
