package com.sebastian_daschner.jaxrs_analyzer.analysis.classes;

import com.sebastian_daschner.jaxrs_analyzer.model.Types;
import com.sebastian_daschner.jaxrs_analyzer.model.instructions.*;
import com.sebastian_daschner.jaxrs_analyzer.model.results.MethodResult;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.util.*;
import java.util.stream.Stream;

import static com.sebastian_daschner.jaxrs_analyzer.analysis.bytecode.collection.InstructionBuilder.*;
import static com.sebastian_daschner.jaxrs_analyzer.model.instructions.Instruction.InstructionType.LOAD_PLACEHOLDER;
import static com.sebastian_daschner.jaxrs_analyzer.model.instructions.Instruction.InstructionType.STORE_PLACEHOLDER;
import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.util.Printer.OPCODES;

/**
 * @author Sebastian Daschner
 */
class ProjectMethodVisitor extends MethodVisitor {

    private final Set<Label> exceptionHandlers = new HashSet<>();
    private final List<Label> visitedLabels = new ArrayList<>();
    final MethodResult methodResult;
    private final String className;

    ProjectMethodVisitor(MethodResult methodResult, String className) {
        super(ASM5);
        // TODO refactor to list of instructions only
        this.methodResult = methodResult;
        this.className = className;
    }

    @Override
    public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
        exceptionHandlers.add(handler);
    }

    @Override
    public void visitLabel(Label label) {
        visitedLabels.add(label);
        if (exceptionHandlers.remove(label))
            methodResult.getInstructions().add(new ExceptionHandlerInstruction(label));
    }

    @Override
    public void visitMaxs(final int maxStack, final int maxLocals) {
        super.visitMaxs(maxStack, maxLocals);
    }

    @Override
    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
        // resolve LOAD & STORE placeholders
        ListIterator<Instruction> iterator = methodResult.getInstructions().listIterator();
        while (iterator.hasNext()) {
            final Instruction instruction = iterator.next();
            if (instruction.getType() != LOAD_PLACEHOLDER && instruction.getType() != STORE_PLACEHOLDER)
                continue;

            final LoadStoreInstructionPlaceholder placeholder = (LoadStoreInstructionPlaceholder) instruction;
            if (placeholder.getNumber() != index)
                continue;

            final Label label = placeholder.getLabel();
            if (isLabelActive(label, start, end)) {
                final String type = signature != null ? signature : desc;
                iterator.set(placeholder.getType() == LOAD_PLACEHOLDER ? new LoadInstruction(index, type, name, label, end) : new StoreInstruction(index, type, name, label));
            }
        }
    }

    private boolean isLabelActive(final Label label, final Label start, final Label end) {
        boolean startVisited = false;
        for (final Label current : visitedLabels) {
            if (current == start)
                startVisited = true;
            if (current == label)
                return startVisited;
            if (current == end)
                return false;
        }
        return false;
    }

    @Override
    public void visitInsn(int opcode) {
        methodResult.getInstructions().add(buildInstruction(opcode, getLastLabel()));
    }

    @Override
    public void visitIntInsn(int opcode, int operand) {
        methodResult.getInstructions().add(buildIntInstruction(opcode, operand, getLastLabel()));
    }

    @Override
    public void visitVarInsn(int opcode, int index) {
        final Label label = !visitedLabels.isEmpty() ? visitedLabels.get(visitedLabels.size() - 1) : null;
        methodResult.getInstructions().add(buildLoadStoreInstruction(opcode, index, label));
    }

    @Override
    public void visitTypeInsn(int opcode, String className) {
        methodResult.getInstructions().add(buildTypeInstruction(opcode, className, getLastLabel()));
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String desc) {
        methodResult.getInstructions().add(buildFieldInstruction(opcode, owner, name, desc, getLastLabel()));
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        methodResult.getInstructions().add(buildInvokeInstruction(opcode, owner, name, desc, getLastLabel()));
    }

    @Override
    public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs) {
        final Handle handle = Stream.of(bsmArgs).filter(a -> a instanceof Handle).map(a -> (Handle) a)
                .findAny().orElse(bsm);

        methodResult.getInstructions().add(buildInvokeDynamic(className, name, desc, handle, getLastLabel()));
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
        methodResult.getInstructions().add(buildJumpInstruction(opcode, label));
    }

    @Override
    public void visitLdcInsn(Object object) {
        methodResult.getInstructions().add(createLdcInstruction(object, getLastLabel()));
    }

    private Label getLastLabel() {
        if (visitedLabels.isEmpty())
            return null;
        return visitedLabels.get(visitedLabels.size() - 1);
    }

    private PushInstruction createLdcInstruction(final Object object, final Label label) {
        // see MethodVisitor
        if (object instanceof Integer) {
            return new PushInstruction(object, Types.INTEGER, label);
        }
        if (object instanceof Float) {
            return new PushInstruction(object, Types.FLOAT, label);
        }
        if (object instanceof Long) {
            return new PushInstruction(object, Types.LONG, label);
        }
        if (object instanceof Double) {
            return new PushInstruction(object, Types.DOUBLE, label);
        }
        if (object instanceof String) {
            return new PushInstruction(object, Types.STRING, label);
        }
        if (object instanceof Type) {
            return new PushInstruction(((Type) object).getDescriptor(), Types.CLASS, label);
        }
        return new PushInstruction(object, Type.getDescriptor(object.getClass()), label);
    }

    @Override
    public void visitIincInsn(int var, int increment) {
        methodResult.getInstructions().add(new DefaultInstruction(OPCODES[IINC], getLastLabel()));
    }

    @Override
    public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
        methodResult.getInstructions().add(new SizeChangingInstruction(OPCODES[TABLESWITCH], 0, 1, getLastLabel()));
    }

    @Override
    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
        methodResult.getInstructions().add(new SizeChangingInstruction(OPCODES[LOOKUPSWITCH], 0, 1, getLastLabel()));
    }

    @Override
    public void visitMultiANewArrayInsn(String desc, int dimensions) {
        methodResult.getInstructions().add(new SizeChangingInstruction(OPCODES[MULTIANEWARRAY], 1, dimensions, getLastLabel()));
    }

    @Override
    public void visitEnd() {
        // resolve unresolved placeholders
        final ListIterator<Instruction> listIterator = methodResult.getInstructions().listIterator();
        while (listIterator.hasNext()) {
            final Instruction instruction = listIterator.next();
            if (instruction.getType() == LOAD_PLACEHOLDER) {
                listIterator.set(new LoadInstruction(((LoadStoreInstructionPlaceholder) instruction).getNumber(), Types.OBJECT, instruction.getLabel(), null));
            } else if (instruction.getType() == STORE_PLACEHOLDER) {
                listIterator.set(new StoreInstruction(((LoadStoreInstructionPlaceholder) instruction).getNumber(), Types.OBJECT, instruction.getLabel()));
            }
        }
    }
}
