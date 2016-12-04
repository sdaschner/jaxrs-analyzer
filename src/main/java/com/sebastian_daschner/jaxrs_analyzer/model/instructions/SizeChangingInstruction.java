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
 * Represents any instruction which changes the stack elements and is not covered
 * by other implementations of {@link Instruction}.
 *
 * @author Sebastian Daschner
 */
public class SizeChangingInstruction extends DefaultInstruction {

    private final int numberOfPushes;
    private final int numberOfPops;

    public SizeChangingInstruction(final String description, final int numberOfPushes, final int numberOfPops, final Label label) {
        super(description, label);

        if (numberOfPushes < 0 || numberOfPops < 0)
            throw new IllegalArgumentException("Number of pushes and pops cannot be negative");

        this.numberOfPushes = numberOfPushes;
        this.numberOfPops = numberOfPops;
    }

    public int getNumberOfPushes() {
        return numberOfPushes;
    }

    public int getNumberOfPops() {
        return numberOfPops;
    }

    @Override
    public InstructionType getType() {
        return InstructionType.SIZE_CHANGE;
    }

    @Override
    public int getStackSizeDifference() {
        return numberOfPushes - numberOfPops;
    }

}