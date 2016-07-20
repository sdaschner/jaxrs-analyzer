package com.sebastian_daschner.jaxrs_analyzer.analysis.classes;

import com.sebastian_daschner.jaxrs_analyzer.model.instructions.Instruction;
import com.sebastian_daschner.jaxrs_analyzer.model.results.ClassResult;
import com.sebastian_daschner.jaxrs_analyzer.model.results.MethodResult;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class JAXRSMethodVisitorTest {

    private JAXRSMethodVisitor cut;
    private ClassResult classResult;

    @Before
    public void setUp() {
        classResult = new ClassResult();
        cut = new JAXRSMethodVisitor(classResult, "Foobar", "test", "()V", null, new MethodResult(), true);
    }

    @Test
    public void test() {
        final Label start = new Label();
        final Label end = new Label();

        cut.visitLabel(new Label());
        cut.visitIntInsn(Opcodes.BIPUSH, 2);
        cut.visitLabel(start);
        cut.visitVarInsn(Opcodes.ISTORE, 1);
        cut.visitInsn(Opcodes.NOP);
        cut.visitLabel(new Label());
        cut.visitVarInsn(Opcodes.ILOAD, 1);
        cut.visitLabel(end);

        final List<Instruction> instructions = classResult.getMethods().iterator().next().getInstructions();
        assertThat(instructions.size(), is(4));

        cut.visitLocalVariable("foobar", "Ljava/lang/String;", null, start, end, 1);

        assertThat(instructions.size(), is(4));
        assertThat(instructions.stream().filter(i -> i.getType() == Instruction.InstructionType.LOAD).count(), is(1L));
        assertThat(instructions.stream().filter(i -> i.getType() == Instruction.InstructionType.STORE).count(), is(1L));
    }

}