/*
 * Copyright (C) 2015 Sebastian Daschner, sebastian-daschner.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sebastian_daschner.jaxrs_analyzer.analysis.bytecode.collection.testclasses;

import com.sebastian_daschner.jaxrs_analyzer.model.Types;
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
        instructions.add(new StoreInstruction(2, Types.OBJECT, "variable$2"));
        instructions.add(new SizeChangingInstruction("monitorenter", 0, 1));
        instructions.add(new PushInstruction(6));
        instructions.add(new LoadInstruction(1, Types.PRIMITIVE_INT, "number"));
        instructions.add(new SizeChangingInstruction("idiv", 1, 2));
        instructions.add(new LoadInstruction(2, Types.OBJECT, "variable$2"));
        instructions.add(new SizeChangingInstruction("monitorexit", 0, 1));
        instructions.add(new ReturnInstruction());

        instructions.add(new ExceptionHandlerInstruction());
        instructions.add(new StoreInstruction(3, Types.OBJECT, "variable$3"));
        instructions.add(new LoadInstruction(2, Types.OBJECT, "variable$2"));
        instructions.add(new SizeChangingInstruction("monitorexit", 0, 1));
        instructions.add(new LoadInstruction(3, Types.OBJECT, "variable$3"));
        instructions.add(new ThrowInstruction());

        return instructions;
    }

}
