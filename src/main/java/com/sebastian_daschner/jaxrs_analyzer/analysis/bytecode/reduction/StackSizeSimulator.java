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

package com.sebastian_daschner.jaxrs_analyzer.analysis.bytecode.reduction;

import com.sebastian_daschner.jaxrs_analyzer.utils.Pair;
import com.sebastian_daschner.jaxrs_analyzer.model.instructions.Instruction;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Simulates runtime stack sizes of instructions.
 *
 * @author Sebastian Daschner
 */
class StackSizeSimulator {

    private List<Pair<Integer, Integer>> stackSizes;

    /**
     * Initializes the runtime stack sizes with the given instructions. This has to be called before {@link StackSizeSimulator#simulateStatementBackwards}
     *
     * @param instructions The instructions to simulate
     */
    void buildStackSizes(final List<Instruction> instructions) {
        stackSizes = new ArrayList<>();
        int stackSize = 0;

        for (Instruction instruction : instructions) {
            final int previousStackSize = stackSize;

            stackSize += instruction.getStackSizeDifference();

            if (isStackCleared(instruction))
                stackSize = 0;

            if (stackSize < 0) {
                throw new IllegalStateException("Runtime stack under-flow occurred.");
            }

            stackSizes.add(Pair.of(previousStackSize, stackSize));
        }
    }

    /**
     * Checks if the stack will be cleared on invoking the given instruction.
     *
     * @param instruction The instruction
     * @return {@code true} if the stack will be cleared
     */
    private static boolean isStackCleared(final Instruction instruction) {
        return instruction.getType() == Instruction.InstructionType.RETURN || instruction.getType() == Instruction.InstructionType.THROW;
    }

    /**
     * Returns the instruction positions which are visited <i>backwards</i> from {@code backtrackPosition}
     * until the runtime stack is empty.
     *
     * @param backtrackPosition The backtrack position where to start
     * @return All positions <i>backwards</i> until the previous empty position
     */
    List<Integer> simulateStatementBackwards(final int backtrackPosition) {
        // search for previous zero-position in stackSizes
        int currentPosition = backtrackPosition;

        // check against stack size before the instruction was executed
        while (stackSizes.get(currentPosition).getLeft() > 0) {
            currentPosition--;
        }

        return Stream.iterate(backtrackPosition, c -> --c).limit(backtrackPosition - currentPosition + 1)
                .collect(LinkedList::new, Collection::add, Collection::addAll);
    }

    /**
     * Returns all backtrack positions of the given instruction positions.
     * The backtrack positions of both LOAD and store instructions are the next positions where the runtime stack size is {@code 0}.
     *
     * @param loadStorePositions The LOAD/STORE positions
     * @return The backtrack positions
     */
    Set<Integer> findLoadStoreBacktrackPositions(final Set<Integer> loadStorePositions) {
        // go to this or next zero-position
        return loadStorePositions.stream().map(this::findBacktrackPosition).collect(Collectors.toSet());
    }

    /**
     * Returns the next position where the stack will be empty.
     *
     * @param position The current position
     * @return The next empty position
     */
    private int findBacktrackPosition(final int position) {
        int currentPosition = position;

        // check against stack size after the instruction was executed
        while (stackSizes.get(currentPosition).getRight() > 0) {
            currentPosition++;
        }

        return currentPosition;
    }

}