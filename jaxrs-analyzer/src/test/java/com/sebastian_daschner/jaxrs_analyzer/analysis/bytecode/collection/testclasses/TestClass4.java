package com.sebastian_daschner.jaxrs_analyzer.analysis.bytecode.collection.testclasses;

import com.sebastian_daschner.jaxrs_analyzer.model.instructions.*;
import com.sebastian_daschner.jaxrs_analyzer.model.methods.MethodIdentifier;

import java.util.LinkedList;
import java.util.List;

public class TestClass4 {

    public int method(final int number) {
        try {
            return 3 * 2 / number;
        } finally {
            System.out.println("Computed");
        }
    }

    public static List<Instruction> getResult() {
        final List<Instruction> instructions = new LinkedList<>();

        // constant folding
        instructions.add(new PushInstruction(6));
        instructions.add(new LoadInstruction(1, "int", "number"));
        instructions.add(new SizeChangingInstruction("idiv", 1, 2));
        instructions.add(new StoreInstruction(2, "java.lang.Object", "variable$2"));
        instructions.add(new GetStaticInstruction("java.lang.System", "out", "java.io.PrintStream"));
        instructions.add(new PushInstruction("Computed"));
        instructions.add(new InvokeInstruction(MethodIdentifier.ofNonStatic("java.io.PrintStream", "println", null, "java.lang.String")));
        instructions.add(new LoadInstruction(2, "java.lang.Object", "variable$2"));
        instructions.add(new ReturnInstruction());

        instructions.add(new ExceptionHandlerInstruction());
        instructions.add(new StoreInstruction(3, "java.lang.Object", "variable$3"));
        instructions.add(new GetStaticInstruction("java.lang.System", "out", "java.io.PrintStream"));
        instructions.add(new PushInstruction("Computed"));
        instructions.add(new InvokeInstruction(MethodIdentifier.ofNonStatic("java.io.PrintStream", "println", null, "java.lang.String")));
        instructions.add(new LoadInstruction(3, "java.lang.Object", "variable$3"));
        instructions.add(new ThrowInstruction());

        return instructions;
    }

}
