package com.sebastian_daschner.jaxrs_analyzer.analysis.classes;

import com.sebastian_daschner.jaxrs_analyzer.model.results.MethodResult;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;

import static org.objectweb.asm.Opcodes.*;

/**
 * @author Sebastian Daschner
 */
class JAXRSAnnotatedSuperMethodClassVisitor extends ClassVisitor {

    private final MethodResult methodResult;
    private final Method method;

    JAXRSAnnotatedSuperMethodClassVisitor(final MethodResult methodResult, final Method method) {
        super(ASM7);
        this.methodResult = methodResult;
        this.method = method;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        final boolean legalModifiers = ((access & ACC_SYNTHETIC) | (access & ACC_STATIC) | (access & ACC_NATIVE)) == 0;

        final String descriptor = Type.getMethodDescriptor(method);
        if (legalModifiers && method.getName().equals(name) && (descriptor.equals(desc) || descriptor.equals(signature)))
            return new JAXRSAnnotatedSuperMethodVisitor(methodResult);

        return null;
    }

}

