package com.sebastian_daschner.jaxrs_analyzer.analysis.bytecode.collection;

import com.sebastian_daschner.jaxrs_analyzer.LogProvider;
import com.sebastian_daschner.jaxrs_analyzer.model.JavaUtils;
import com.sebastian_daschner.jaxrs_analyzer.model.instructions.*;
import com.sebastian_daschner.jaxrs_analyzer.model.methods.MethodIdentifier;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import java.lang.reflect.Field;

import static com.sebastian_daschner.jaxrs_analyzer.model.Types.*;
import static com.sebastian_daschner.jaxrs_analyzer.model.methods.MethodIdentifier.of;
import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.util.Printer.OPCODES;

/**
 * @author Sebastian Daschner
 */
public final class InstructionBuilder {

    private InstructionBuilder() {
        throw new UnsupportedOperationException();
    }

    public static Instruction buildFieldInstruction(final int opcode, final String ownerClass, final String name, final String desc, final Label label) {
        // TODO remove
        if (org.objectweb.asm.Type.getObjectType(ownerClass).getClassName().equals(ownerClass.replace('.', '/')))
            throw new AssertionError("!");

        final String opcodeName = OPCODES[opcode];

        switch (opcode) {
            case GETSTATIC:
                final Object value = getStaticValue(name, ownerClass);
                return new GetStaticInstruction(ownerClass, name, desc, value, label);
            case PUTSTATIC:
                return new SizeChangingInstruction(opcodeName, 0, 1, label);
            case GETFIELD:
                return new GetFieldInstruction(ownerClass, name, desc, label);
            case PUTFIELD:
                return new SizeChangingInstruction(opcodeName, 0, 2, label);
            default:
                throw new IllegalArgumentException("Opcode " + opcode + " not a field instruction");
        }
    }

    public static Instruction buildInstruction(final int opcode, final Label label) {
        final String opcodeName = OPCODES[opcode];

        switch (opcode) {
            case ICONST_0:
                return new PushInstruction(0, PRIMITIVE_INT, label);
            case ICONST_1:
                return new PushInstruction(1, PRIMITIVE_INT, label);
            case ICONST_2:
                return new PushInstruction(2, PRIMITIVE_INT, label);
            case ICONST_3:
                return new PushInstruction(3, PRIMITIVE_INT, label);
            case ICONST_4:
                return new PushInstruction(4, PRIMITIVE_INT, label);
            case ICONST_5:
                return new PushInstruction(5, PRIMITIVE_INT, label);
            case ICONST_M1:
                return new PushInstruction(-1, PRIMITIVE_INT, label);
            case DCONST_0:
                return new PushInstruction(0d, PRIMITIVE_DOUBLE, label);
            case DCONST_1:
                return new PushInstruction(1d, PRIMITIVE_DOUBLE, label);
            case FCONST_0:
                return new PushInstruction(1f, PRIMITIVE_FLOAT, label);
            case FCONST_1:
                return new PushInstruction(1f, PRIMITIVE_FLOAT, label);
            case FCONST_2:
                return new PushInstruction(2f, PRIMITIVE_FLOAT, label);
            case LCONST_0:
                return new PushInstruction(0L, PRIMITIVE_LONG, label);
            case LCONST_1:
                return new PushInstruction(1L, PRIMITIVE_LONG, label);
            case IALOAD:
            case LALOAD:
            case FALOAD:
            case DALOAD:
            case AALOAD:
            case BALOAD:
            case CALOAD:
            case SALOAD:
                return new SizeChangingInstruction(opcodeName, 1, 2, label);
            case IASTORE:
            case LASTORE:
            case FASTORE:
            case DASTORE:
            case AASTORE:
            case BASTORE:
            case CASTORE:
            case SASTORE:
                return new SizeChangingInstruction(opcodeName, 0, 3, label);
            case DUP_X1:
            case DUP2_X1:
                return new SizeChangingInstruction(opcodeName, 3, 2, label);
            case DUP_X2:
            case DUP2_X2:
                return new SizeChangingInstruction(opcodeName, 4, 3, label);
            case ARRAYLENGTH:
            case I2L:
            case I2F:
            case I2D:
            case L2I:
            case L2F:
            case L2D:
            case F2I:
            case F2L:
            case F2D:
            case D2I:
            case D2L:
            case D2F:
            case I2B:
            case I2C:
            case I2S:
            case INEG:
            case LNEG:
            case FNEG:
            case DNEG:
            case SWAP:
                return new SizeChangingInstruction(opcodeName, 1, 1, label);
            case IADD:
            case LADD:
            case FADD:
            case DADD:
            case ISUB:
            case LSUB:
            case FSUB:
            case DSUB:
            case IMUL:
            case LMUL:
            case FMUL:
            case DMUL:
            case IDIV:
            case LDIV:
            case FDIV:
            case DDIV:
            case IREM:
            case LREM:
            case FREM:
            case DREM:
            case ISHL:
            case LSHL:
            case ISHR:
            case LSHR:
            case IUSHR:
            case LUSHR:
            case IAND:
            case LAND:
            case IOR:
            case LOR:
            case IXOR:
            case LXOR:
            case LCMP:
            case FCMPL:
            case FCMPG:
            case DCMPL:
            case DCMPG:
                return new SizeChangingInstruction(opcodeName, 1, 2, label);
            case IRETURN:
            case LRETURN:
            case FRETURN:
            case DRETURN:
            case ARETURN:
                return new ReturnInstruction(label);
            case ATHROW:
                return new ThrowInstruction(label);
            case RETURN:
            case NOP:
                return new DefaultInstruction(opcodeName, label);
            case POP:
            case POP2:
            case MONITORENTER:
            case MONITOREXIT:
                return new SizeChangingInstruction(opcodeName, 0, 1, label);
            case ACONST_NULL:
                return new SizeChangingInstruction(opcodeName, 1, 0, label);
            case DUP:
            case DUP2:
                return new DupInstruction(label);
            default:
                throw new IllegalArgumentException("Unexpected opcode " + opcode);
        }
    }

    public static Instruction buildLoadStoreInstruction(int opcode, int index, Label label) {
        switch (opcode) {
            case ILOAD:
            case LLOAD:
            case FLOAD:
            case DLOAD:
            case ALOAD:
                return new LoadStoreInstructionPlaceholder(Instruction.InstructionType.LOAD_PLACEHOLDER, index, label);
            case ISTORE:
            case LSTORE:
            case FSTORE:
            case DSTORE:
            case ASTORE:
                return new LoadStoreInstructionPlaceholder(Instruction.InstructionType.STORE_PLACEHOLDER, index, label);
            case RET:
                return new DefaultInstruction(OPCODES[opcode], label);
            default:
                throw new IllegalArgumentException("Unexpected opcode " + opcode);
        }
    }

    public static Instruction buildTypeInstruction(int opcode, String className, final Label label) {
        final String opcodeName = OPCODES[opcode];

        switch (opcode) {
            case NEW:
                return new NewInstruction(className, label);
            case ANEWARRAY:
            case INSTANCEOF:
                return new SizeChangingInstruction(opcodeName, 1, 1, label);
            case CHECKCAST:
                return new DefaultInstruction(opcodeName, label);
            default:
                throw new IllegalArgumentException("Unexpected opcode " + opcode);
        }
    }

    public static InvokeInstruction buildInvokeInstruction(final int opcode, String containingClass, String name, String desc, final Label label) {
        switch (opcode) {
            case INVOKEINTERFACE:
            case INVOKEVIRTUAL:
            case INVOKESPECIAL:
                return new InvokeInstruction(of(containingClass, name, desc, false), label);
            case INVOKESTATIC:
                return new InvokeInstruction(of(containingClass, name, desc, true), label);
            default:
                throw new IllegalArgumentException("Unexpected opcode " + opcode);
        }
    }

    public static Instruction buildInvokeDynamic(final String className, final String name, final String desc, final Handle handle, final Label label) {
        final MethodIdentifier actualIdentifier = of(handle.getOwner(), handle.getName(), handle.getDesc(), handle.getTag() == Opcodes.H_INVOKESTATIC);

        final MethodIdentifier dynamicIdentifier = of(className, name, desc, true);
        return new InvokeDynamicInstruction(actualIdentifier, dynamicIdentifier, label);
    }

    public static Instruction buildJumpInstruction(int opcode, final Label label) {
        final String opcodeName = OPCODES[opcode];

        switch (opcode) {
            case IFEQ:
            case IFNE:
            case IFLT:
            case IFGE:
            case IFGT:
            case IFLE:
            case IFNULL:
            case IFNONNULL:
                return new SizeChangingInstruction(opcodeName, 0, 1, label);
            case JSR:
                return new SizeChangingInstruction(opcodeName, 1, 0, label);
            case GOTO:
                return new DefaultInstruction(opcodeName, label);
            case IF_ICMPEQ:
            case IF_ICMPNE:
            case IF_ICMPLT:
            case IF_ICMPGE:
            case IF_ICMPGT:
            case IF_ICMPLE:
            case IF_ACMPEQ:
            case IF_ACMPNE:
                return new SizeChangingInstruction(opcodeName, 0, 2, label);
            default:
                throw new IllegalArgumentException("Unexpected opcode " + opcode);
        }
    }

    public static Instruction buildIntInstruction(int opcode, int operand, final Label label) {
        switch (opcode) {
            case BIPUSH:
            case SIPUSH:
                return new PushInstruction(operand, PRIMITIVE_INT, label);
            case NEWARRAY:
                return new SizeChangingInstruction(OPCODES[NEWARRAY], 1, 1, label);
            default:
                throw new IllegalArgumentException("Unexpected opcode " + opcode);
        }
    }

    private static Object getStaticValue(String name, String containingClass) {
        final Field field;
        try {
            // needs to load same class instance in Maven plugin, not from extended classloader
            final Class<?> clazz = Class.forName(containingClass.replace('/', '.'));
            field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            return field.get(null);
        } catch (Exception e) {
            LogProvider.error("Could not access static property, reason: " + e.getMessage());
            LogProvider.debug(e);
            return null;
        }
    }

}
