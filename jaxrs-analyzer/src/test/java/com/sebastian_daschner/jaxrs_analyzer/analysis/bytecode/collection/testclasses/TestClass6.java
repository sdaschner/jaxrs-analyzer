package com.sebastian_daschner.jaxrs_analyzer.analysis.bytecode.collection.testclasses;

import com.sebastian_daschner.jaxrs_analyzer.model.instructions.*;

import java.util.LinkedList;
import java.util.List;

public class TestClass6 {

    public synchronized int method(final int number) {
        return 3 * 2 / number;
    }

    public static List<Instruction> getResult() {
        final List<Instruction> instructions = new LinkedList<>();

        // constant folding
        instructions.add(new PushInstruction(6));
        instructions.add(new LoadInstruction(1, "int", "number"));
        instructions.add(new SizeChangingInstruction("idiv", 1, 2));
        instructions.add(new ReturnInstruction());

        return instructions;
    }

}
