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
 * Represents a XZY_LOAD_X instruction (for any number).
 *
 * @author Sebastian Daschner
 */
public class LoadInstruction extends LoadStoreInstruction {

    private final Label validUntil;

    public LoadInstruction(final int number, final String variableType, final Label label, final Label validUntil) {
        super(number, variableType, label);
        this.validUntil = validUntil;
    }

    public LoadInstruction(final int number, final String variableType, final String name, final Label label, final Label validUntil) {
        super(number, variableType, name, label);
        this.validUntil = validUntil;
    }

    @Override
    public int getStackSizeDifference() {
        return 1;
    }

    @Override
    public InstructionType getType() {
        return InstructionType.LOAD;
    }

    public Label getValidUntil() {
        return validUntil;
    }

    @Override
    public String toString() {
        return "LoadInstruction{" +
                "type='" + getType() + '\'' +
                ", number=" + getName() + '\'' +
                ", variableType=" + getVariableType() + '\'' +
                ", name=" + getName() +
                ", validUntil=" + validUntil +
                '}';
    }

}
