package com.sebastian_daschner.jaxrs_analyzer.analysis.bytecode.collection;

import com.sebastian_daschner.jaxrs_analyzer.LogProvider;
import com.sebastian_daschner.jaxrs_analyzer.model.instructions.*;
import com.sebastian_daschner.jaxrs_analyzer.model.methods.MethodIdentifier;
import org.objectweb.asm.Label;

import java.lang.reflect.Field;

import static jdk.internal.org.objectweb.asm.util.Printer.OPCODES;
import static org.objectweb.asm.Opcodes.*;

/**
 * @author Sebastian Daschner
 */
public final class InstructionBuilder {

    private InstructionBuilder() {
        throw new UnsupportedOperationException();
    }

    public static Instruction buildFieldInstruction(final int opcode, final String owner, final String name, final String desc) {
        final String containingClass = org.objectweb.asm.Type.getObjectType(owner).getClassName();
        final String opcodeName = OPCODES[opcode];

        switch (opcode) {
            case GETSTATIC:
                final Object value = getStaticValue(name, containingClass);
                return new GetStaticInstruction(containingClass, name, desc, value);
            case PUTSTATIC:
                return new SizeChangingInstruction(opcodeName, 0, 1);
            case GETFIELD:
                return new GetFieldInstruction(containingClass, name, desc);
            case PUTFIELD:
                return new SizeChangingInstruction(opcodeName, 0, 2);
            default:
                throw new IllegalArgumentException("Opcode " + opcode + " not a field instruction");
        }
    }

    public static Instruction buildInstruction(final int opcode) {
        final String opcodeName = OPCODES[opcode];

        switch (opcode) {
            case ICONST_0:
                return new PushInstruction(0);
            case ICONST_1:
                return new PushInstruction(1);
            case ICONST_2:
                return new PushInstruction(2);
            case ICONST_3:
                return new PushInstruction(3);
            case ICONST_4:
                return new PushInstruction(4);
            case ICONST_5:
                return new PushInstruction(5);
            case ICONST_M1:
                return new PushInstruction(-1);
            case DCONST_0:
                return new PushInstruction(0d);
            case DCONST_1:
                return new PushInstruction(1d);
            case FCONST_0:
                return new PushInstruction(1f);
            case FCONST_1:
                return new PushInstruction(1f);
            case FCONST_2:
                return new PushInstruction(2f);
            case LCONST_0:
                return new PushInstruction(0L);
            case LCONST_1:
                return new PushInstruction(1L);
            case IALOAD:
            case LALOAD:
            case FALOAD:
            case DALOAD:
            case AALOAD:
            case BALOAD:
            case CALOAD:
            case SALOAD:
                return new SizeChangingInstruction(opcodeName, 1, 2);
            case IASTORE:
            case LASTORE:
            case FASTORE:
            case DASTORE:
            case AASTORE:
            case BASTORE:
            case CASTORE:
            case SASTORE:
                return new SizeChangingInstruction(opcodeName, 0, 3);
            case DUP_X1:
            case DUP2_X1:
                return new SizeChangingInstruction(opcodeName, 3, 2);
            case DUP_X2:
            case DUP2_X2:
                return new SizeChangingInstruction(opcodeName, 4, 3);
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
                return new SizeChangingInstruction(opcodeName, 1, 1);
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
                return new SizeChangingInstruction(opcodeName, 1, 2);
            case IRETURN:
            case LRETURN:
            case FRETURN:
            case DRETURN:
            case ARETURN:
                return new ReturnInstruction();
            case ATHROW:
                return new ThrowInstruction();
            case RETURN:
            case NOP:
                return new DefaultInstruction(opcodeName);
            case POP:
            case POP2:
            case MONITORENTER:
            case MONITOREXIT:
                return new SizeChangingInstruction(opcodeName, 0, 1);
            case ACONST_NULL:
                return new SizeChangingInstruction(opcodeName, 1, 0);
            case DUP:
            case DUP2:
                return new DupInstruction();
            default:
                throw new IllegalArgumentException("Unexpected opcode " + opcode);
        }
    }

    public static Instruction buildLoadStoreInstruction(int opcode, int index, Label label) {
//        final String variableType = (type != null) ? type : Types.OBJECT;
//        final String variableName = (name != null) ? name : UNKNOWN_VARIABLE_NAME_PREFIX + index;

        switch (opcode) {
            case ILOAD:
            case LLOAD:
            case FLOAD:
            case DLOAD:
            case ALOAD:
//                return new LoadInstruction(index, variableType, variableName);
                return new LoadStoreInstructionPlaceholder(Instruction.InstructionType.LOAD_PLACEHOLDER, index, label);
            case ISTORE:
            case LSTORE:
            case FSTORE:
            case DSTORE:
            case ASTORE:
//                return new StoreInstruction(index, variableType, variableName);
                return new LoadStoreInstructionPlaceholder(Instruction.InstructionType.STORE_PLACEHOLDER, index, label);
            case RET:
                return new DefaultInstruction(OPCODES[opcode]);
            default:
                throw new IllegalArgumentException("Unexpected opcode " + opcode);
        }
    }

    public static Instruction buildTypeInstruction(int opcode, String className) {
        final String opcodeName = OPCODES[opcode];

        switch (opcode) {
            case NEW:
                return new NewInstruction(className);
            case ANEWARRAY:
            case INSTANCEOF:
                return new SizeChangingInstruction(opcodeName, 1, 1);
            case CHECKCAST:
                return new DefaultInstruction(opcodeName);
            default:
                throw new IllegalArgumentException("Unexpected opcode " + opcode);
        }
    }

    public static InvokeInstruction buildInvokeInstruction(final int opcode, String containingClass, String name, String desc) {
        switch (opcode) {
            case INVOKEINTERFACE:
            case INVOKEVIRTUAL:
            case INVOKESPECIAL:
                return new InvokeInstruction(MethodIdentifier.of(containingClass, name, desc, false));
            case INVOKESTATIC:
                return new InvokeInstruction(MethodIdentifier.of(containingClass, name, desc, true));
            default:
                throw new IllegalArgumentException("Unexpected opcode " + opcode);
        }
    }

    public static Instruction buildJumpInstruction(int opcode) {
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
                return new SizeChangingInstruction(opcodeName, 0, 1);
            case JSR:
                return new SizeChangingInstruction(opcodeName, 1, 0);
            case GOTO:
                return new DefaultInstruction(opcodeName);
            case IF_ICMPEQ:
            case IF_ICMPNE:
            case IF_ICMPLT:
            case IF_ICMPGE:
            case IF_ICMPGT:
            case IF_ICMPLE:
            case IF_ACMPEQ:
            case IF_ACMPNE:
                return new SizeChangingInstruction(opcodeName, 0, 2);
            default:
                throw new IllegalArgumentException("Unexpected opcode " + opcode);
        }
    }

    public static Instruction buildIntInstruction(int opcode, int operand) {
        switch (opcode) {
            case BIPUSH:
            case SIPUSH:
                return new PushInstruction(operand);
            case NEWARRAY:
                return new SizeChangingInstruction(OPCODES[NEWARRAY], 1, 1);
            default:
                throw new IllegalArgumentException("Unexpected opcode " + opcode);
        }
    }

    private static Object getStaticValue(String name, String containingClass) {
        final Field field;
        try {
            field = Class.forName(containingClass).getDeclaredField(name);
            field.setAccessible(true);
            return field.get(null);
        } catch (ReflectiveOperationException e) {
            LogProvider.error("Could not access static property, reason: " + e.getMessage());
            LogProvider.debug(e);
            return null;
        }
    }

}
