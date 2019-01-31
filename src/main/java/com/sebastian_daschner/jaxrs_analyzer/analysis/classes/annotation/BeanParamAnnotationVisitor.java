package com.sebastian_daschner.jaxrs_analyzer.analysis.classes.annotation;

import com.sebastian_daschner.jaxrs_analyzer.analysis.classes.ContextClassReader;
import com.sebastian_daschner.jaxrs_analyzer.analysis.classes.JAXRSClassVisitor;
import com.sebastian_daschner.jaxrs_analyzer.model.results.ClassResult;
import com.sebastian_daschner.jaxrs_analyzer.model.results.MethodResult;
import java.io.IOException;

import java.util.Objects;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * @author Sebastian Daschner
 */
public class BeanParamAnnotationVisitor extends AnnotationVisitor {

    private final ClassResult classResult;
    private final MethodResult methodResult;
    private final String type;

    public BeanParamAnnotationVisitor(final ClassResult classResult, String type) {
        this(classResult, null, type);
        Objects.requireNonNull(classResult);
    }

    public BeanParamAnnotationVisitor(final MethodResult methodResult, String type) {
        this(null, methodResult, type);
        Objects.requireNonNull(methodResult);
    }

    private BeanParamAnnotationVisitor(final ClassResult classResult, final MethodResult methodResult, String type) {
        super(Opcodes.ASM5);
        this.classResult = classResult;
        this.methodResult = methodResult;
        this.type = type;
        Objects.requireNonNull(type);
    }

    @Override
    public void visitEnd() {
        JAXRSClassVisitor beanClassVisitor;
        ClassResult clsResult;
        if (this.classResult != null) {
            clsResult = classResult;
        } else {
            clsResult = new ClassResult();
        }
        beanClassVisitor = new JAXRSClassVisitor(clsResult);
        final ClassReader classReader;
        try {
            classReader = new ContextClassReader(Type.getType(type).getClassName());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        classReader.accept(beanClassVisitor, ClassReader.EXPAND_FRAMES);

        if (this.classResult == null) {
            methodResult.getMethodParameters().addAll(clsResult.getClassFields());
        }
        super.visitEnd();
    }
}
