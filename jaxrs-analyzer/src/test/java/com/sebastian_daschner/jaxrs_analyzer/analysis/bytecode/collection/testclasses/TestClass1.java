package com.sebastian_daschner.jaxrs_analyzer.analysis.bytecode.collection.testclasses;

import com.sebastian_daschner.jaxrs_analyzer.model.instructions.*;

import java.util.LinkedList;
import java.util.List;

public class TestClass1 {

    public void method() {
        int status = 200;
        int anotherStatus = 100;
        status = anotherStatus = 300;
    }

    public static List<Instruction> getResult() {
        final List<Instruction> instructions = new LinkedList<>();

        instructions.add(new PushInstruction(200));
        instructions.add(new StoreInstruction(1, "int", "status"));
        instructions.add(new PushInstruction(100));
        instructions.add(new StoreInstruction(2, "int", "anotherStatus"));
        instructions.add(new PushInstruction(300));
        instructions.add(new DupInstruction());
        instructions.add(new StoreInstruction(2, "int", "anotherStatus"));
        instructions.add(new StoreInstruction(1, "int", "status"));
        instructions.add(new DefaultInstruction("return"));

        return instructions;
    }

}
