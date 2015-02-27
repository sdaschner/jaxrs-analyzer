package com.sebastian_daschner.jaxrs_analyzer.analysis.bytecode.collection;

import com.sebastian_daschner.jaxrs_analyzer.model.instructions.*;
import javassist.CtBehavior;
import javassist.bytecode.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

/**
 * Collects the bytecode information of a method as {@link Instruction}s.
 * This class is thread-safe.
 *
 * @author Sebastian Daschner
 */
public class ByteCodeCollector implements Opcode {

    private final Lock lock = new ReentrantLock();
    private CodeIterator codeIterator;
    private Set<Integer> exceptionHandlerPositions;

    private InvokeInstructionBuilder invokeInstructionBuilder;
    private FieldInstructionBuilder fieldInstructionBuilder;
    private LoadStoreInstructionBuilder loadStoreInstructionBuilder;
    private LdcPushInstructionBuilder ldcPushInstructionBuilder;
    private WideInstructionBuilder wideInstructionBuilder;
    private NewInstructionBuilder newInstructionBuilder;

    /**
     * Builds the instructions for the given method.
     *
     * @param method The method
     * @return The instructions of the method
     */
    public List<Instruction> buildInstructions(final CtBehavior method) {
        lock.lock();
        try {
            initializeBuilders(method);

            final List<Instruction> instructions = new ArrayList<>();
            codeIterator.move(0);

            while (codeIterator.hasNext()) {
                final int position = codeIterator.next();

                if (exceptionHandlerPositions.contains(position)) {
                    instructions.add(new ExceptionHandlerInstruction());
                }

                instructions.add(interpretInstruction(position));
            }

            return instructions;
        } catch (final BadBytecode e) {
            throw new IllegalStateException("Could not analyze byte code.", e);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Initializes the needed functionality for collecting the bytecode information of the method.
     *
     * @param method The method
     */
    private void initializeBuilders(final CtBehavior method) {
        final MethodInfo methodInfo = method.getMethodInfo();
        final CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
        final ConstPool pool = methodInfo.getConstPool();

        codeIterator = codeAttribute.iterator();

        invokeInstructionBuilder = new InvokeInstructionBuilder(codeIterator, pool);
        fieldInstructionBuilder = new FieldInstructionBuilder(codeIterator, pool);
        loadStoreInstructionBuilder = new LoadStoreInstructionBuilder(codeAttribute);
        ldcPushInstructionBuilder = new LdcPushInstructionBuilder(pool);
        wideInstructionBuilder = new WideInstructionBuilder(codeIterator, loadStoreInstructionBuilder);
        newInstructionBuilder = new NewInstructionBuilder(codeIterator, pool);

        exceptionHandlerPositions = buildExceptionHandlerPositions(codeAttribute.getExceptionTable());
    }

    private Set<Integer> buildExceptionHandlerPositions(final ExceptionTable exceptionTable) {
        return IntStream.range(0, exceptionTable.size()).map(exceptionTable::handlerPc).collect(HashSet::new, Set::add, Set::addAll);
    }

    /**
     * Builds the instruction on the given bytecode position.
     *
     * @param position The bytecode position
     * @return The created instruction
     * @throws BadBytecode If the bytecode could not be analyzed
     */
    private Instruction interpretInstruction(final int position) throws BadBytecode {
        final int currentByte = codeIterator.byteAt(position);
        final String opCodeName = Mnemonic.OPCODE[currentByte];

        switch (currentByte) {

            // LOAD instructions
            case ALOAD:
            case DLOAD:
            case FLOAD:
            case ILOAD:
            case LLOAD:
                return loadStoreInstructionBuilder.buildLoad(codeIterator.byteAt(position + 1));
            case ALOAD_0:
            case DLOAD_0:
            case FLOAD_0:
            case ILOAD_0:
            case LLOAD_0:
                return loadStoreInstructionBuilder.buildLoad(0);
            case ALOAD_1:
            case DLOAD_1:
            case FLOAD_1:
            case ILOAD_1:
            case LLOAD_1:
                return loadStoreInstructionBuilder.buildLoad(1);
            case ALOAD_2:
            case DLOAD_2:
            case FLOAD_2:
            case ILOAD_2:
            case LLOAD_2:
                return loadStoreInstructionBuilder.buildLoad(2);
            case ALOAD_3:
            case DLOAD_3:
            case FLOAD_3:
            case ILOAD_3:
            case LLOAD_3:
                return loadStoreInstructionBuilder.buildLoad(3);

            // STORE instructions
            case ASTORE:
            case DSTORE:
            case FSTORE:
            case ISTORE:
            case LSTORE:
                return loadStoreInstructionBuilder.buildStore(codeIterator.byteAt(position + 1));
            case ASTORE_0:
            case DSTORE_0:
            case FSTORE_0:
            case ISTORE_0:
            case LSTORE_0:
                return loadStoreInstructionBuilder.buildStore(0);
            case ASTORE_1:
            case DSTORE_1:
            case FSTORE_1:
            case ISTORE_1:
            case LSTORE_1:
                return loadStoreInstructionBuilder.buildStore(1);
            case ASTORE_2:
            case DSTORE_2:
            case FSTORE_2:
            case ISTORE_2:
            case LSTORE_2:
                return loadStoreInstructionBuilder.buildStore(2);
            case ASTORE_3:
            case DSTORE_3:
            case FSTORE_3:
            case ISTORE_3:
            case LSTORE_3:
                return loadStoreInstructionBuilder.buildStore(3);

            case ARETURN:
            case DRETURN:
            case FRETURN:
            case IRETURN:
            case LRETURN:
                return new ReturnInstruction();

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
                return new PushInstruction(0f);
            case FCONST_1:
                return new PushInstruction(1f);
            case FCONST_2:
                return new PushInstruction(2f);
            case LCONST_0:
                return new PushInstruction(0L);
            case LCONST_1:
                return new PushInstruction(1L);
            case LDC:
                return ldcPushInstructionBuilder.build(codeIterator.byteAt(position + 1));
            case LDC_W:
            case LDC2_W:
                return ldcPushInstructionBuilder.build(codeIterator.u16bitAt(position + 1));
            case BIPUSH:
                return new PushInstruction(codeIterator.byteAt(position + 1));
            case SIPUSH:
                return new PushInstruction(codeIterator.s16bitAt(position + 1));

            case GETSTATIC:
                return fieldInstructionBuilder.buildGetStatic(position);
            case GETFIELD:
                return fieldInstructionBuilder.buildGetField(position);

            case NEW:
                return newInstructionBuilder.build(position);
            case ACONST_NULL:
            case JSR:
            case JSR_W:
                return new SizeChangingInstruction(opCodeName, 1, 0);
            case DUP:
            case DUP2:
                return new DupInstruction();

            case PUTFIELD:
            case IF_ACMPEQ:
            case IF_ACMPNE:
            case IF_ICMPEQ:
            case IF_ICMPGE:
            case IF_ICMPGT:
            case IF_ICMPLE:
            case IF_ICMPLT:
            case IF_ICMPNE:
                return new SizeChangingInstruction(opCodeName, 0, 2);

            case IADD:
            case IDIV:
            case IMUL:
            case IREM:
            case ISUB:
            case DADD:
            case DDIV:
            case DMUL:
            case DREM:
            case DSUB:
            case FADD:
            case FDIV:
            case FMUL:
            case FREM:
            case FSUB:
            case LADD:
            case LDIV:
            case LMUL:
            case LSUB:
            case LREM:
            case ISHL:
            case ISHR:
            case IUSHR:
            case LSHL:
            case LSHR:
            case LUSHR:
            case IAND:
            case IOR:
            case IXOR:
            case LAND:
            case LOR:
            case LXOR:
            case DCMPG:
            case DCMPL:
            case FCMPG:
            case FCMPL:
            case LCMP:
            case AALOAD:
            case BALOAD:
            case CALOAD:
            case DALOAD:
            case FALOAD:
            case IALOAD:
            case LALOAD:
            case SALOAD:
                return new SizeChangingInstruction(opCodeName, 1, 2);
            case AASTORE:
            case BASTORE:
            case CASTORE:
            case DASTORE:
            case FASTORE:
            case IASTORE:
            case LASTORE:
            case SASTORE:
                return new SizeChangingInstruction(opCodeName, 0, 3);

            case ANEWARRAY:
            case ARRAYLENGTH:
            case D2F:
            case D2I:
            case D2L:
            case F2D:
            case F2I:
            case F2L:
            case I2B:
            case I2C:
            case I2D:
            case I2F:
            case I2L:
            case I2S:
            case L2D:
            case L2F:
            case L2I:
            case DNEG:
            case FNEG:
            case INEG:
            case LNEG:
            case INSTANCEOF:
            case SWAP:
            case NEWARRAY:
                return new SizeChangingInstruction(opCodeName, 1, 1);
            case PUTSTATIC:
            case POP:
            case POP2:
            case IFEQ:
            case IFLE:
            case IFNE:
            case IFGE:
            case IFLT:
            case IFGT:
            case IFNONNULL:
            case IFNULL:
            case MONITORENTER:
            case MONITOREXIT:
            case TABLESWITCH:
            case LOOKUPSWITCH:
                return new SizeChangingInstruction(opCodeName, 0, 1);
            case DUP_X1:
            case DUP2_X1:
                return new SizeChangingInstruction(opCodeName, 3, 2);
            case DUP_X2:
            case DUP2_X2:
                return new SizeChangingInstruction(opCodeName, 4, 3);
            case MULTIANEWARRAY:
                return new SizeChangingInstruction(opCodeName, 1, codeIterator.byteAt(position + 3));

            case INVOKEINTERFACE:
            case INVOKEVIRTUAL:
            case INVOKESPECIAL:
                return invokeInstructionBuilder.build(position);
            case INVOKESTATIC:
                return invokeInstructionBuilder.buildStatic(position);
            case INVOKEDYNAMIC:
                return invokeInstructionBuilder.buildDynamic(position);
            case ATHROW:
                return new ThrowInstruction();
            case CHECKCAST:
            case RETURN:
            case IINC:
            case NOP:
            case GOTO:
            case GOTO_W:
            case RET:
                // do nothing
                return new DefaultInstruction(opCodeName);
            case WIDE:
                return wideInstructionBuilder.build(position);
            default:
                throw new UnsupportedOperationException(opCodeName + " not handled!");
        }
    }


}
