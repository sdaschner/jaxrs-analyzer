package com.sebastian_daschner.jaxrs_analyzer.analysis.bytecode.collection.testclasses;

import com.sebastian_daschner.jaxrs_analyzer.model.instructions.*;
import com.sebastian_daschner.jaxrs_analyzer.model.methods.MethodIdentifier;

import java.util.LinkedList;
import java.util.List;

public class TestClass3 {

    public int method(final int number) {
        try {
            return 3 * 2 / number;
        } catch (ArithmeticException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static List<Instruction> getResult() {
        final List<Instruction> instructions = new LinkedList<>();

        // constant folding
        instructions.add(new PushInstruction(6));
        instructions.add(new LoadInstruction(1, "int", "number"));
        instructions.add(new SizeChangingInstruction("idiv", 1, 2));
        instructions.add(new ReturnInstruction());

        instructions.add(new ExceptionHandlerInstruction());
        instructions.add(new StoreInstruction(2, "java.lang.ArithmeticException", "e"));
        instructions.add(new LoadInstruction(2, "java.lang.ArithmeticException", "e"));
        instructions.add(new InvokeInstruction(MethodIdentifier.ofNonStatic("java.lang.ArithmeticException", "printStackTrace", null)));
        instructions.add(new PushInstruction(0));
        instructions.add(new ReturnInstruction());

        return instructions;
    }

}
