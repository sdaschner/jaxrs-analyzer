package com.sebastian_daschner.jaxrs_analyzer.analysis.classes;

import com.sebastian_daschner.jaxrs_analyzer.LogProvider;
import com.sebastian_daschner.jaxrs_analyzer.model.Types;
import com.sebastian_daschner.jaxrs_analyzer.model.methods.MethodIdentifier;
import com.sebastian_daschner.jaxrs_analyzer.model.results.MethodResult;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import java.io.IOException;

import static org.objectweb.asm.Opcodes.*;

/**
 * @author Sebastian Daschner
 */
public class ProjectMethodClassVisitor extends ClassVisitor {

    private final MethodResult methodResult;
    private final MethodIdentifier identifier;
    private boolean methodFound;
    private String superName;

    public ProjectMethodClassVisitor(final MethodResult methodResult, final MethodIdentifier identifier) {
        super(ASM7);
        this.methodResult = methodResult;
        this.identifier = identifier;
    }

    @Override
    public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {
        this.superName = superName;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        // TODO allow abstract?
        final boolean legalModifiers = (access & ACC_ABSTRACT | access & ACC_NATIVE) == 0;

        final String methodSignature = identifier.getSignature();
        if (legalModifiers && identifier.getMethodName().equals(name) && (methodSignature.equals(desc) || methodSignature.equals(signature))) {
            methodFound = true;
            return new ProjectMethodVisitor(methodResult, identifier.getContainingClass());
        }

        return null;
    }

    @Override
    public void visitEnd() {
        // if method hasn't been found it may be on a super class (invoke_virtual)
        if (!methodFound && !superName.equals(Types.CLASS_OBJECT)) {
            try {
                final ClassReader classReader = new ContextClassReader(superName);
                final ClassVisitor visitor = new ProjectMethodClassVisitor(methodResult, identifier);

                classReader.accept(visitor, ClassReader.EXPAND_FRAMES);
            } catch (IOException e) {
                LogProvider.error("Could not analyze project method " + superName + "#" + identifier.getMethodName());
                LogProvider.debug(e);
            }
        }
    }

}
