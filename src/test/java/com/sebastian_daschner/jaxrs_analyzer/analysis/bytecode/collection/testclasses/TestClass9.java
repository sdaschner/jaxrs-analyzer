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
import com.sebastian_daschner.jaxrs_analyzer.model.instructions.GetStaticInstruction;
import com.sebastian_daschner.jaxrs_analyzer.model.instructions.Instruction;
import com.sebastian_daschner.jaxrs_analyzer.model.instructions.InvokeInstruction;
import com.sebastian_daschner.jaxrs_analyzer.model.instructions.ReturnInstruction;
import com.sebastian_daschner.jaxrs_analyzer.model.methods.MethodIdentifier;

import javax.ws.rs.core.Response;
import java.util.LinkedList;
import java.util.List;

public class TestClass9 {

    public Response method(final int number) {
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    public static List<Instruction> getResult() {
        final List<Instruction> instructions = new LinkedList<>();

        // constant folding
        instructions.add(new GetStaticInstruction("javax/ws/rs/core/Response$Status", "BAD_REQUEST", Types.RESPONSE_STATUS, Response.Status.BAD_REQUEST));
        instructions.add(new InvokeInstruction(MethodIdentifier.ofStatic(Types.CLASS_RESPONSE, "status", Types.RESPONSE_BUILDER, Types.RESPONSE_STATUS)));
        instructions.add(new InvokeInstruction(MethodIdentifier.ofNonStatic(Types.CLASS_RESPONSE_BUILDER, "build", Types.RESPONSE)));
        instructions.add(new ReturnInstruction());

        return instructions;
    }

}
