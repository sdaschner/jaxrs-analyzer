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

package com.sebastian_daschner.jaxrs_analyzer.analysis.bytecode.simulation;

import com.sebastian_daschner.jaxrs_analyzer.model.elements.Element;
import com.sebastian_daschner.jaxrs_analyzer.model.instructions.Instruction;
import com.sebastian_daschner.jaxrs_analyzer.model.methods.MethodIdentifier;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

/**
 * Simulates the the instructions of a project method. The parameters of the method can be set with the actual arguments. This class is thread-safe.
 *
 * @author Sebastian Daschner
 */
public class InjectableArgumentMethodSimulator extends MethodSimulator {

    private final Lock lock = new ReentrantLock();

    /**
     * Simulates the instructions of the method which will be called with the given arguments.
     *
     * @param arguments    The argument values
     * @param instructions The instructions of the method
     * @param identifier   The identifier of the method
     * @return The return value or {@code null} if return type is void
     */
    public Element simulate(final List<Element> arguments, final List<Instruction> instructions, final MethodIdentifier identifier) {
        lock.lock();
        try {
            injectArguments(arguments, identifier);

            return simulateInternal(instructions);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Injects the arguments of the method invocation to the local variables.
     *
     * @param arguments The argument values
     */
    private void injectArguments(final List<Element> arguments, final MethodIdentifier identifier) {
        final boolean staticMethod = identifier.isStaticMethod();
        final int startIndex = staticMethod ? 0 : 1;
        final int endIndex = staticMethod ? arguments.size() - 1 : arguments.size();

        IntStream.rangeClosed(startIndex, endIndex).forEach(i -> localVariables.put(i, arguments.get(staticMethod ? i : i - 1)));
    }

}
