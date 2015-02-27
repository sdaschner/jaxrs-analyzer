package com.sebastian_daschner.jaxrs_analyzer.analysis.bytecode.collection.testclasses;

import com.sebastian_daschner.jaxrs_analyzer.model.instructions.*;

import java.util.LinkedList;
import java.util.List;

public class TestClass5 {

    public int method(final int number) {
        synchronized (this) {
            return 3 * 2 / number;
        }
    }

    public static List<Instruction> getResult() {
        final List<Instruction> instructions = new LinkedList<>();

        // constant folding
        instructions.add(new LoadInstruction(0, TestClass5.class.getCanonicalName(), "this"));
        instructions.add(new DupInstruction());
        instructions.add(new StoreInstruction(2, "java.lang.Object", "variable$2"));
        instructions.add(new SizeChangingInstruction("monitorenter", 0, 1));
        instructions.add(new PushInstruction(6));
        instructions.add(new LoadInstruction(1, "int", "number"));
        instructions.add(new SizeChangingInstruction("idiv", 1, 2));
        instructions.add(new LoadInstruction(2, "java.lang.Object", "variable$2"));
        instructions.add(new SizeChangingInstruction("monitorexit", 0, 1));
        instructions.add(new ReturnInstruction());

        instructions.add(new ExceptionHandlerInstruction());
        instructions.add(new StoreInstruction(3, "java.lang.Object", "variable$3"));
        instructions.add(new LoadInstruction(2, "java.lang.Object", "variable$2"));
        instructions.add(new SizeChangingInstruction("monitorexit", 0, 1));
        instructions.add(new LoadInstruction(3, "java.lang.Object", "variable$3"));
        instructions.add(new ThrowInstruction());

        return instructions;
    }

}
