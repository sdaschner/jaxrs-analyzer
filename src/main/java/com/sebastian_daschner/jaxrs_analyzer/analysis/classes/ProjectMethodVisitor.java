package com.sebastian_daschner.jaxrs_analyzer.analysis.classes;

import com.sebastian_daschner.jaxrs_analyzer.model.Types;
import com.sebastian_daschner.jaxrs_analyzer.model.instructions.*;
import com.sebastian_daschner.jaxrs_analyzer.model.results.MethodResult;
import org.objectweb.asm.*;

import java.util.*;

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

    ProjectMethodVisitor(MethodResult methodResult) {
        super(ASM5);
        this.methodResult = methodResult;
    }

    @Override
    public void visitParameter(String name, int access) {
        // TODO save params for later use w/ annotations -> determine request body
        System.out.println("visitParameter: name = [" + name + "], access = [" + access + "]");
    }

    @Override
    public AnnotationVisitor visitAnnotationDefault() {
        // TODO needed?
        return null;
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        System.out.println("visitTypeAnnotation: typeRef = [" + typeRef + "], typePath = [" + typePath + "], desc = [" + desc + "], visible = [" + visible + "]");
        // TODO needed?
        return null;
    }

    @Override
    public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
        exceptionHandlers.add(handler);
    }

    @Override
    public void visitLabel(Label label) {
        visitedLabels.add(label);
        if (exceptionHandlers.remove(label))
            methodResult.getInstructions().add(new ExceptionHandlerInstruction());
    }

    @Override
    public AnnotationVisitor visitInsnAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        System.out.println("visitInsnAnnotation: typeRef = [" + typeRef + "], typePath = [" + typePath + "], desc = [" + desc + "], visible = [" + visible + "]");
        return null;
    }

    @Override
    public void visitAttribute(Attribute attr) {
        System.out.println("visit attr = " + attr);
        // TODO needed?
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

            if (isLabelActive(placeholder.getLabel(), start, end)) {
                final String type = signature != null ? signature : desc;
                iterator.set(placeholder.getType() == LOAD_PLACEHOLDER ? new LoadInstruction(index, type, name) : new StoreInstruction(index, type, name));
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
        methodResult.getInstructions().add(buildInstruction(opcode));
    }

    @Override
    public void visitIntInsn(int opcode, int operand) {
        methodResult.getInstructions().add(buildIntInstruction(opcode, operand));
    }

    @Override
    public void visitVarInsn(int opcode, int index) {
        methodResult.getInstructions().add(buildLoadStoreInstruction(opcode, index, visitedLabels.get(visitedLabels.size() - 1)));
    }

    @Override
    public void visitTypeInsn(int opcode, String className) {
        methodResult.getInstructions().add(buildTypeInstruction(opcode, className));
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String desc) {
        methodResult.getInstructions().add(buildFieldInstruction(opcode, owner, name, desc));
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        methodResult.getInstructions().add(buildInvokeInstruction(opcode, owner, name, desc));
    }

    @Override
    public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs) {
        // TODO
        System.out.println("visitInvokeDynamicInsn");
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
        methodResult.getInstructions().add(buildJumpInstruction(opcode));
    }

    @Override
    public void visitLdcInsn(Object cst) {
        methodResult.getInstructions().add(new PushInstruction(cst));
    }

    @Override
    public void visitIincInsn(int var, int increment) {
        methodResult.getInstructions().add(new DefaultInstruction(OPCODES[IINC]));
    }

    @Override
    public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
        methodResult.getInstructions().add(new SizeChangingInstruction(OPCODES[TABLESWITCH], 0, 1));
    }

    @Override
    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
        methodResult.getInstructions().add(new SizeChangingInstruction(OPCODES[LOOKUPSWITCH], 0, 1));
    }

    @Override
    public void visitMultiANewArrayInsn(String desc, int dimensions) {
        methodResult.getInstructions().add(new SizeChangingInstruction(OPCODES[MULTIANEWARRAY], 1, dimensions));
    }

    @Override
    public AnnotationVisitor visitLocalVariableAnnotation(int typeRef, TypePath typePath, Label[] start, Label[] end, int[] index, String desc, boolean visible) {
        System.out.println("visitLocalVariableAnnotation: typeRef = [" + typeRef + "], typePath = [" + typePath + "], start = [" + start + "], end = [" + end + "], index = [" + index + "], desc = [" + desc + "], visible = [" + visible + "]");
        return null;
    }

    @Override
    public void visitEnd() {
        // resolve unresolved placeholders
        final ListIterator<Instruction> listIterator = methodResult.getInstructions().listIterator();
        while (listIterator.hasNext()) {
            final Instruction instruction = listIterator.next();
            if (instruction.getType() == LOAD_PLACEHOLDER) {
                listIterator.set(new LoadInstruction(((LoadStoreInstructionPlaceholder) instruction).getNumber(), Types.OBJECT));
            } else if (instruction.getType() == STORE_PLACEHOLDER) {
                listIterator.set(new StoreInstruction(((LoadStoreInstructionPlaceholder) instruction).getNumber(), Types.OBJECT));
            }
        }
    }
}
