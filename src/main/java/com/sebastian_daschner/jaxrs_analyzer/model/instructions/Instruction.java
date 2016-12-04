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

package com.sebastian_daschner.jaxrs_analyzer.model.instructions;

import org.objectweb.asm.Label;

/**
 * Represents a byte code instruction.
 *
 * @author Sebastian Daschner
 */
public abstract class Instruction {

    private final Label label;

    public Instruction(final Label label) {
        this.label = label;
    }

    public abstract int getStackSizeDifference();

    public abstract InstructionType getType();

    public Label getLabel() {
        return label;
    }

    /**
     * Represents the available types of {@link Instruction}s.
     */
    public enum InstructionType {

        PUSH, LOAD, STORE, INVOKE, RETURN, SIZE_CHANGE, GET_FIELD, GET_STATIC, NEW, DUP, THROW, METHOD_HANDLE, OTHER, LOAD_PLACEHOLDER, STORE_PLACEHOLDER

    }

}
