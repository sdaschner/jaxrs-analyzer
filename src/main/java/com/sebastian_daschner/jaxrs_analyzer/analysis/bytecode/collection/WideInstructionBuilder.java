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

package com.sebastian_daschner.jaxrs_analyzer.analysis.bytecode.collection;

import com.sebastian_daschner.jaxrs_analyzer.model.instructions.DefaultInstruction;
import com.sebastian_daschner.jaxrs_analyzer.model.instructions.Instruction;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.Mnemonic;
import javassist.bytecode.Opcode;

/**
 * Creates instructions for WIDE opcodes.
 *
 * @author Sebastian Daschner
 */
class WideInstructionBuilder implements Opcode {

    private final CodeIterator codeIterator;
    private final LoadStoreInstructionBuilder loadStoreInstructionBuilder;

    WideInstructionBuilder(final CodeIterator codeIterator, final LoadStoreInstructionBuilder loadStoreInstructionBuilder) {
        this.codeIterator = codeIterator;
        this.loadStoreInstructionBuilder = loadStoreInstructionBuilder;
    }

    /**
     * Creates an LOAD, STORE, IINC or RET instruction for the given wide byte code position.
     *
     * @param position The byte code position
     * @return The contained instruction
     */
    Instruction build(final int position) {
        if (codeIterator.byteAt(position) != WIDE)
            throw new IllegalArgumentException("WIDE instruction is not valid.");

        int containedOpcode = codeIterator.byteAt(position + 1);
        switch (containedOpcode) {
            case ILOAD:
            case LLOAD:
            case FLOAD:
            case DLOAD:
            case ALOAD:
                return loadStoreInstructionBuilder.buildLoad(codeIterator.u16bitAt(position + 2));
            case ISTORE:
            case LSTORE:
            case FSTORE:
            case DSTORE:
            case ASTORE:
                return loadStoreInstructionBuilder.buildStore(codeIterator.u16bitAt(position + 2));
            case IINC:
            case RET:
                return new DefaultInstruction(Mnemonic.OPCODE[containedOpcode]);
            default:
                throw new IllegalArgumentException("WIDE instruction is not valid.");
        }
    }


}
