package com.sebastian_daschner.jaxrs_analyzer.analysis.classes;

import com.sebastian_daschner.jaxrs_analyzer.analysis.classes.annotation.ApplicationPathAnnotationVisitor;
import com.sebastian_daschner.jaxrs_analyzer.analysis.classes.annotation.ConsumesAnnotationVisitor;
import com.sebastian_daschner.jaxrs_analyzer.analysis.classes.annotation.PathAnnotationVisitor;
import com.sebastian_daschner.jaxrs_analyzer.analysis.classes.annotation.ProducesAnnotationVisitor;
import com.sebastian_daschner.jaxrs_analyzer.model.Types;
import com.sebastian_daschner.jaxrs_analyzer.model.results.ClassResult;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

/**
 * @author Sebastian Daschner
 */
public class JAXRSClassVisitor extends ClassVisitor {

    private final ClassResult classResult;

    public JAXRSClassVisitor(final ClassResult classResult) {
        super(ASM5);
        this.classResult = classResult;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        classResult.setOriginalClass(name);
        // TODO see superclasses / interfaces for potential annotations later
    }

    @Override
    public void visitSource(String source, String debug) {
        // TODO can be used for JavaDoc parsing
        System.out.println("visitSource: source = [" + source + "], debug = [" + debug + "]");
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        switch (desc) {
            case Types.PATH:
                return new PathAnnotationVisitor(classResult);
            case Types.APPLICATION_PATH:
                return new ApplicationPathAnnotationVisitor(classResult);
            case Types.CONSUMES:
                return new ConsumesAnnotationVisitor(classResult);
            case Types.PRODUCES:
                return new ProducesAnnotationVisitor(classResult);
            default:
                return null;
        }
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        if ((access & ACC_STATIC) == 0)
            return new JAXRSFieldVisitor(classResult, desc, signature);
        return null;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if (((access & ACC_SYNTHETIC) | (access & ACC_PUBLIC) | (access & ACC_NATIVE)) != 0 && !"<init>".equals(name))
            return new JAXRSMethodVisitor(classResult, desc, signature);
        return null;
    }

}

