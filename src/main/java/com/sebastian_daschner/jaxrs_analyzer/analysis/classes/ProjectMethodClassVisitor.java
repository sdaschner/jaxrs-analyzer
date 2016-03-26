package com.sebastian_daschner.jaxrs_analyzer.analysis.classes;

import com.sebastian_daschner.jaxrs_analyzer.model.methods.MethodIdentifier;
import com.sebastian_daschner.jaxrs_analyzer.model.results.MethodResult;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

/**
 * @author Sebastian Daschner
 */
public class ProjectMethodClassVisitor extends ClassVisitor {

    private final MethodResult methodResult;
    private final MethodIdentifier identifier;

    public ProjectMethodClassVisitor(final MethodResult methodResult, final MethodIdentifier identifier) {
        super(ASM5);
        this.methodResult = methodResult;
        this.identifier = identifier;
    }

    @Override
    public void visitSource(String source, String debug) {
        // TODO can be used for JavaDoc parsing
        System.out.println("visitSource for project method: source = [" + source + "], debug = [" + debug + "]");
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        final boolean legalModifiers = ((access & ACC_SYNTHETIC) | (access & ACC_PUBLIC) | (access & ACC_NATIVE)) != 0;

        if (legalModifiers && identifier.getMethodName().equals(name) && (identifier.getSignature().equals(desc) || identifier.getSignature().equals(signature)))
            return new ProjectMethodVisitor(methodResult);

        return null;
    }

}

