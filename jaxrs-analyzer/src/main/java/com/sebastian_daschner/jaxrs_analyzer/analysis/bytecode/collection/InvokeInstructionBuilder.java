package com.sebastian_daschner.jaxrs_analyzer.analysis.bytecode.collection;

import com.sebastian_daschner.jaxrs_analyzer.analysis.utils.JavaUtils;
import com.sebastian_daschner.jaxrs_analyzer.model.instructions.InvokeDynamicInstruction;
import com.sebastian_daschner.jaxrs_analyzer.model.instructions.InvokeInstruction;
import com.sebastian_daschner.jaxrs_analyzer.model.methods.MethodIdentifier;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.*;

/**
 * Creates the {@link InvokeInstruction} for a given byte code position.
 *
 * @author Sebastian Daschner
 */
class InvokeInstructionBuilder {

    private final CodeIterator codeIterator;
    private final ConstPool pool;

    InvokeInstructionBuilder(final CodeIterator codeIterator, final ConstPool pool) {
        this.codeIterator = codeIterator;
        this.pool = pool;
    }

    /**
     * Creates an {@link InvokeInstruction} for an INVOKE_VIRTUAL/SPECIAL opcode.
     *
     * @param position The bytecode position
     * @return The invoke instruction
     * @throws BadBytecode If the bytecode could not be analyzed
     */
    InvokeInstruction build(final int position) throws BadBytecode {
        final int methodRefIndex = codeIterator.u16bitAt(position + 1);
        return buildInvokeInstruction(methodRefIndex, false);
    }

    /**
     * Creates an {@link InvokeInstruction} for an INVOKE_STATIC opcode.
     *
     * @param position The bytecode position
     * @return The invoke instruction
     * @throws BadBytecode If the bytecode could not be analyzed
     */
    InvokeInstruction buildStatic(final int position) throws BadBytecode {
        final int methodRefIndex = codeIterator.u16bitAt(position + 1);
        return buildInvokeInstruction(methodRefIndex, true);
    }

    /**
     * Creates a {@link InvokeInstruction} for an INVOKE_DYNAMIC opcode.
     *
     * @param position The bytecode position
     * @return The invoke instruction
     * @throws BadBytecode If the bytecode could not be analyzed
     */
    InvokeInstruction buildDynamic(final int position) throws BadBytecode {
        final int index = codeIterator.u16bitAt(position + 1);
        final int bootstrapIndex = pool.getInvokeDynamicBootstrap(index);

        final int lambdaIndex = pool.getInvokeDynamicNameAndType(index);
        final String lambdaSignature = pool.getUtf8Info(pool.getNameAndTypeDescriptor(lambdaIndex));
        final String lambdaMethodName = pool.getUtf8Info(pool.getNameAndTypeName(lambdaIndex));
        final SignatureAttribute.MethodSignature methodSignature = SignatureAttribute.toMethodSignature(lambdaSignature);
        final String lambdaReturnType = JavaUtils.getMethodReturnType(methodSignature);

        final MethodIdentifier dynamicIdentifier = MethodIdentifier.ofStatic(pool.getClassName(), lambdaMethodName, lambdaReturnType,
                JavaUtils.getMethodParameters(methodSignature));

        final CtClass ctClass;
        try {
            ctClass = ClassPool.getDefault().get(pool.getClassName());
        } catch (NotFoundException e) {
            throw new IllegalStateException("Could not analyze bytecode");
        }

        final BootstrapMethodsAttribute bootstrapMethods = (BootstrapMethodsAttribute) ctClass.getClassFile().getAttribute(JavaUtils.BOOTSTRAP_ATTRIBUTE_NAME);

        final int actualMethodIndex = bootstrapMethods.getMethods()[bootstrapIndex].arguments[1];
        final int actualMethodRefIndex = pool.getMethodHandleIndex(actualMethodIndex);
        final boolean actualMethodStatic = pool.getMethodHandleKind(actualMethodIndex) == ConstPool.REF_invokeStatic;

        MethodIdentifier actualIdentifier = buildInvokeInstruction(actualMethodRefIndex, actualMethodStatic).getIdentifier();
        return new InvokeDynamicInstruction(actualIdentifier, dynamicIdentifier);
    }

    private InvokeInstruction buildInvokeInstruction(final int methodRefIndex, final boolean staticMethod) throws BadBytecode {
        final int methodTag = pool.getTag(methodRefIndex);
        final String className = getClassName(methodRefIndex, methodTag);
        final String methodName = getMethodName(methodRefIndex, methodTag);
        final String poolMethodType = getMethodType(methodRefIndex, methodTag);

        final MethodIdentifier identifier = buildMethodIdentifier(className, methodName, poolMethodType, staticMethod);
        return new InvokeInstruction(identifier);
    }

    private String getMethodType(final int methodRefIndex, final int methodTag) {
        if (methodTag == ConstPool.CONST_Methodref)
            return pool.getMethodrefType(methodRefIndex);
        return pool.getInterfaceMethodrefType(methodRefIndex);
    }

    private String getClassName(final int methodRefIndex, final int methodTag) {
        if (methodTag == ConstPool.CONST_Methodref)
            return pool.getMethodrefClassName(methodRefIndex);
        return pool.getInterfaceMethodrefClassName(methodRefIndex);
    }

    private String getMethodName(final int methodRefIndex, final int methodTag) {
        if (methodTag == ConstPool.CONST_Methodref)
            return pool.getMethodrefName(methodRefIndex);
        return pool.getInterfaceMethodrefName(methodRefIndex);
    }

    private static MethodIdentifier buildMethodIdentifier(final String className, final String methodName, final String poolMethodType,
                                                          final boolean staticMethod) throws BadBytecode {
        final SignatureAttribute.MethodSignature methodSignature = SignatureAttribute.toMethodSignature(poolMethodType);
        final String returnType = JavaUtils.getMethodReturnType(methodSignature);
        final String[] parameterTypes = JavaUtils.getMethodParameters(methodSignature);

        return MethodIdentifier.of(className, methodName, returnType, staticMethod, parameterTypes);
    }


}
