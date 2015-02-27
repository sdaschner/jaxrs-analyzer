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

import com.sebastian_daschner.jaxrs_analyzer.model.instructions.NewInstruction;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ConstPool;

/**
 * Creates a {@link NewInstruction} for a given bytecode position.
 *
 * @author Sebastian Daschner
 */
class NewInstructionBuilder {

    private final CodeIterator codeIterator;
    private final ConstPool pool;

    public NewInstructionBuilder(final CodeIterator codeIterator, final ConstPool pool) {
        this.codeIterator = codeIterator;
        this.pool = pool;
    }

    /**
     * Creates a NEW instruction for the given position.
     *
     * @param position The position
     * @return The instruction
     */
    public NewInstruction build(final int position) {
        final int index = codeIterator.u16bitAt(position + 1);
        final String type = pool.getClassInfo(index);

        return new NewInstruction(type);
    }

}
