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

import com.sebastian_daschner.jaxrs_analyzer.model.instructions.PushInstruction;
import javassist.bytecode.ConstPool;

/**
 * Creates the {@link PushInstruction} for a given byte code position.
 *
 * @author Sebastian Daschner
 */
class LdcPushInstructionBuilder {

    private final ConstPool pool;

    LdcPushInstructionBuilder(final ConstPool pool) {
        this.pool = pool;
    }

    /**
     * Creates an LDC push instruction for the given const pool index.
     *
     * @param index The index in the const pool
     * @return The push instruction
     */
    PushInstruction build(final int index) {
        return new PushInstruction(getLdc(index));
    }

    /**
     * Returns the LDC object for the given index.
     *
     * @param index The index in the const pool
     * @return The LDC object
     */
    private Object getLdc(final int index) {
        int tag = pool.getTag(index);
        switch (tag) {
            case ConstPool.CONST_String:
                return pool.getStringInfo(index);
            case ConstPool.CONST_Integer:
                return pool.getIntegerInfo(index);
            case ConstPool.CONST_Float:
                return pool.getFloatInfo(index);
            case ConstPool.CONST_Long:
                return pool.getLongInfo(index);
            case ConstPool.CONST_Double:
                return pool.getDoubleInfo(index);
            case ConstPool.CONST_Class:
                // classes are pushed as string
                return pool.getClassInfo(index);
            default:
                throw new IllegalStateException("Unknown LDC instruction.");
        }
    }

}
