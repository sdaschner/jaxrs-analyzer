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

package com.sebastian_daschner.jaxrs_analyzer.analysis.bytecode;

import com.sebastian_daschner.jaxrs_analyzer.analysis.JobRegistry;
import com.sebastian_daschner.jaxrs_analyzer.analysis.bytecode.simulation.MethodPool;
import com.sebastian_daschner.jaxrs_analyzer.analysis.bytecode.simulation.MethodSimulator;
import com.sebastian_daschner.jaxrs_analyzer.model.JavaUtils;
import com.sebastian_daschner.jaxrs_analyzer.model.instructions.Instruction;
import com.sebastian_daschner.jaxrs_analyzer.model.methods.ProjectMethod;
import com.sebastian_daschner.jaxrs_analyzer.model.results.ClassResult;
import com.sebastian_daschner.jaxrs_analyzer.model.results.MethodResult;

import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Analyzes sub-resource-locator methods. This class is thread-safe.
 *
 * @author Sebastian Daschner
 */
class SubResourceLocatorMethodContentAnalyzer extends MethodContentAnalyzer {

    private final Lock lock = new ReentrantLock();
    private final MethodSimulator simulator = new MethodSimulator();

    /**
     * Analyzes the sub-resource locator method as a class result (which will be the content of a method result).
     *
     * @param methodResult The method result of the sub-resource locator (containing the instructions, and a sub-resource class result)
     */
    void analyze(final MethodResult methodResult) {
        lock.lock();
        try {
            buildPackagePrefix(methodResult.getParentResource().getOriginalClass());

            determineReturnTypes(methodResult.getInstructions()).stream()
                    // FEATURE handle several sub-resource impl's
                    .reduce((l, r) -> JavaUtils.determineMostSpecificType(l, r))
                    .ifPresent(t -> registerSubResourceJob(t, methodResult.getSubResource()));
        } finally {
            lock.unlock();
        }
    }

    /**
     * Determines the possible return types of the sub-resource-locator by analyzing the bytecode.
     * This will analyze the concrete returned types (which then are further analyzed).
     */
    private Set<String> determineReturnTypes(final List<Instruction> instructions) {
        final List<Instruction> visitedInstructions = interpretRelevantInstructions(instructions);

        // find project defined methods in invoke occurrences
        final Set<ProjectMethod> projectMethods = findProjectMethods(visitedInstructions);

        // add project methods to global method pool
        projectMethods.stream().forEach(MethodPool.getInstance()::addProjectMethod);

        return simulator.simulate(visitedInstructions).getTypes();
    }

    private void registerSubResourceJob(final String type, final ClassResult classResult) {
        final String className = JavaUtils.toClassName(type);
        JobRegistry.getInstance().analyzeResourceClass(className, classResult);
    }

}
