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
import com.sebastian_daschner.jaxrs_analyzer.model.instructions.LoadStoreInstruction;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;

/**
 * Searches for specific instruction occurrences in the byte code.
 *
 * @author Sebastian Daschner
 */
final class InstructionFinder {

    private InstructionFinder() {
        throw new UnsupportedOperationException();
    }

    /**
     * Searches for all LOAD indexes which occur in the given instructions.
     * The LOAD instruction is checked against the given predicate if it should be ignored.
     *
     * @param instructions  The instructions where to search
     * @param isLoadIgnored The ignore predicate
     * @return All found LOAD indexes
     */
    static Set<Integer> findLoadIndexes(final List<Instruction> instructions, final Predicate<LoadInstruction> isLoadIgnored) {
        return instructions.stream().filter(i -> i.getType() == Instruction.InstructionType.LOAD).map(i -> (LoadInstruction) i)
                .filter(i -> !isLoadIgnored.test(i)).map(LoadInstruction::getNumber).collect(TreeSet::new, Set::add, Set::addAll);
    }

    /**
     * Searches for all LOAD &amp; STORE occurrences with {@code index} in the given instructions.
     *
     * @param index        The LOAD / STORE index
     * @param instructions The instructions where to search
     * @return The positions of all found LOAD_{@code index} / STORE_{@code index}
     */
    static Set<Integer> findLoadStores(final int index, final List<Instruction> instructions) {
        final Predicate<Instruction> loadStoreType = instruction -> instruction.getType() == Instruction.InstructionType.LOAD
                || instruction.getType() == Instruction.InstructionType.STORE;
        return find(loadStoreType.and(instruction -> ((LoadStoreInstruction) instruction).getNumber() == index), instructions);
    }

    /**
     * Searches for return instructions in the given instructions.
     *
     * @param instructions The instructions where to search
     * @return The positions of all found return instructions
     */
    static Set<Integer> findReturnsAndThrows(final List<Instruction> instructions) {
        return find(instruction -> instruction.getType() == Instruction.InstructionType.RETURN || instruction.getType() == Instruction.InstructionType.THROW, instructions);
    }

    /**
     * Searches for certain instruction positions be testing against the predicate.
     *
     * @param predicate    The criteria predicate
     * @param instructions The instructions where to search
     * @return The positions of all matching instructions
     */
    private static Set<Integer> find(final Predicate<Instruction> predicate, final List<Instruction> instructions) {
        final Set<Integer> positions = new HashSet<>();

        for (int i = 0; i < instructions.size(); i++) {
            final Instruction instruction = instructions.get(i);
            if (predicate.test(instruction)) {
                positions.add(i);
            }
        }

        return positions;
    }

}
