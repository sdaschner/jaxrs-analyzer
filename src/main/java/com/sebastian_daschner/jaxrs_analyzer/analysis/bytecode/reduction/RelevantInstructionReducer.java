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

import com.sebastian_daschner.jaxrs_analyzer.model.instructions.Instruction;
import com.sebastian_daschner.jaxrs_analyzer.model.instructions.LoadInstruction;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Determines the instructions, which are relevant for the return value of a method by simulating a runtime stack with the byte code. This class is thread-safe.
 *
 * @author Sebastian Daschner
 */
public class RelevantInstructionReducer {

    /**
     * These variable names will not be backtracked.
     */
    private static final String[] VARIABLE_NAMES_TO_IGNORE = {"this"};
    private final Lock lock = new ReentrantLock();
    private final StackSizeSimulator stackSizeSimulator = new StackSizeSimulator();
    private List<Instruction> instructions;

    /**
     * Returns all instructions which are somewhat "relevant" for the returned object of the method.
     * The instructions are visited backwards - starting from the return statement.
     * Load and Store operations are handled as well.
     *
     * @param instructions The instructions to reduce
     * @return The relevant instructions
     */
    public List<Instruction> reduceInstructions(final List<Instruction> instructions) {
        lock.lock();
        try {
            this.instructions = instructions;
            stackSizeSimulator.buildStackSizes(instructions);

            return reduceInstructionsInternal(instructions);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Returns all reduced instructions.
     *
     * @param instructions All instructions
     * @return The relevant instructions
     */
    private List<Instruction> reduceInstructionsInternal(final List<Instruction> instructions) {
        final List<Instruction> visitedInstructions = new LinkedList<>();
        final Set<Integer> visitedInstructionPositions = new HashSet<>();
        final Set<Integer> handledLoadIndexes = new HashSet<>();
        final Set<Integer> backtrackPositions = new LinkedHashSet<>(findSortedBacktrackPositions());

        while (!visitedInstructionPositions.containsAll(backtrackPositions)) {

            // unvisited backtrack position
            final int backtrackPosition = backtrackPositions.stream().filter(pos -> !visitedInstructionPositions.contains(pos))
                    .findFirst().orElseThrow(IllegalStateException::new);

            final List<Integer> lastVisitedPositions = stackSizeSimulator.simulateStatementBackwards(backtrackPosition);
            final List<Instruction> lastVisitedInstructions = lastVisitedPositions.stream().map(instructions::get).collect(Collectors.toList());

            visitedInstructionPositions.addAll(lastVisitedPositions);
            visitedInstructions.addAll(lastVisitedInstructions);

            // unhandled load indexes
            final Set<Integer> unhandledLoadIndexes = findUnhandledLoadIndexes(handledLoadIndexes, lastVisitedInstructions);

            // for each load occurrence index -> find load/store backtrack positions (reverse order matters here)
            final SortedSet<Integer> loadStoreBacktrackPositions = findLoadStoreBacktrackPositions(unhandledLoadIndexes);

            handledLoadIndexes.addAll(unhandledLoadIndexes);

            loadStoreBacktrackPositions.stream().forEach(backtrackPositions::add);
        }

        // sort in method natural order
        Collections.reverse(visitedInstructions);

        return visitedInstructions;
    }

    private List<Integer> findSortedBacktrackPositions() {
        final List<Integer> startPositions = new LinkedList<>(InstructionFinder.findReturnsAndThrows(instructions));

        // start with last return
        Collections.sort(startPositions, Comparator.reverseOrder());
        return startPositions;
    }


    /**
     * Searches for load indexes in the {@code lastVisitedInstructions} which are not contained in {@code handledLoadIndexes}.
     *
     * @param handledLoadIndexes      The load indexed which have been handled so far
     * @param lastVisitedInstructions The last visited instructions
     * @return The unhandled load indexes
     */
    private Set<Integer> findUnhandledLoadIndexes(final Set<Integer> handledLoadIndexes, final List<Instruction> lastVisitedInstructions) {
        final Set<Integer> lastLoadIndexes = InstructionFinder.findLoadIndexes(lastVisitedInstructions, RelevantInstructionReducer::isLoadIgnored);

        return lastLoadIndexes.stream().filter(k -> !handledLoadIndexes.contains(k)).collect(Collectors.toSet());
    }

    /**
     * Checks if the given LOAD instruction should be ignored for backtracking.
     *
     * @param instruction The instruction to check
     * @return {@code true} if the LOAD instruction will be ignored
     */
    private static boolean isLoadIgnored(final LoadInstruction instruction) {
        return Stream.of(VARIABLE_NAMES_TO_IGNORE).anyMatch(instruction.getName()::equals);
    }

    /**
     * Returns all backtrack positions of the given LOAD / STORE indexes in the instructions.
     * The backtrack positions of both LOAD and store instructions are the next positions where the runtime stack size is {@code 0}.
     *
     * @param unhandledLoadIndexes The LOAD/STORE indexes to find
     * @return The backtrack positions of the LOAD / STORE indexes
     */
    private SortedSet<Integer> findLoadStoreBacktrackPositions(final Set<Integer> unhandledLoadIndexes) {
        return unhandledLoadIndexes.stream()
                .map(index -> stackSizeSimulator.findLoadStoreBacktrackPositions(InstructionFinder.findLoadStores(index, instructions)))
                .collect(() -> new TreeSet<>(Comparator.reverseOrder()), Set::addAll, Set::addAll);
    }

}
