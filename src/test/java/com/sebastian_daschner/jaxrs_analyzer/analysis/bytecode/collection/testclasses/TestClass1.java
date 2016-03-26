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

public class TestClass1 {

    public void method() {
        int status = 200;
        int anotherStatus = 100;
        status = anotherStatus = 300;
    }

    public static List<Instruction> getResult() {
        final List<Instruction> instructions = new LinkedList<>();

        instructions.add(new PushInstruction(200));
        instructions.add(new StoreInstruction(1, Types.PRIMITIVE_INT, "status"));
        instructions.add(new PushInstruction(100));
        instructions.add(new StoreInstruction(2, Types.PRIMITIVE_INT, "anotherStatus"));
        instructions.add(new PushInstruction(300));
        instructions.add(new DupInstruction());
        instructions.add(new StoreInstruction(2, Types.PRIMITIVE_INT, "anotherStatus"));
        instructions.add(new StoreInstruction(1, Types.PRIMITIVE_INT, "status"));
        instructions.add(new DefaultInstruction("return"));

        return instructions;
    }

}
