package com.sebastian_daschner.jaxrs_analyzer.analysis.bytecode.collection.testclasses;

import com.sebastian_daschner.jaxrs_analyzer.model.instructions.*;

import java.util.LinkedList;
import java.util.List;

public class TestClass8 {

    public double method(final int number) {
        // force dup2 opcode use
        final double d1, d2;

        d1 = d2 = 2.0;

        return d1;
    }

    public static List<Instruction> getResult() {
        final List<Instruction> instructions = new LinkedList<>();

        // constant folding
        instructions.add(new PushInstruction(2.0));
        instructions.add(new DupInstruction());
        instructions.add(new StoreInstruction(4, "double", "d2"));
        instructions.add(new StoreInstruction(2, "double", "d1"));
        instructions.add(new LoadInstruction(2, "double", "d1"));
        instructions.add(new ReturnInstruction());

        return instructions;
    }

}
