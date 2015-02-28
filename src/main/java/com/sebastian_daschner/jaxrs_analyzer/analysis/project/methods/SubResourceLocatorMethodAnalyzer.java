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

package com.sebastian_daschner.jaxrs_analyzer.analysis.project.methods;

import com.sebastian_daschner.jaxrs_analyzer.LogProvider;
import com.sebastian_daschner.jaxrs_analyzer.analysis.bytecode.simulation.MethodPool;
import com.sebastian_daschner.jaxrs_analyzer.analysis.bytecode.simulation.MethodSimulator;
import com.sebastian_daschner.jaxrs_analyzer.analysis.project.classes.ClassAnalyzer;
import com.sebastian_daschner.jaxrs_analyzer.model.instructions.Instruction;
import com.sebastian_daschner.jaxrs_analyzer.model.methods.ProjectMethod;
import com.sebastian_daschner.jaxrs_analyzer.model.results.ClassResult;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Analyzes sub-resource-locator methods. This class is thread-safe.
 *
 * @author Sebastian Daschner
 */
class SubResourceLocatorMethodAnalyzer extends MethodContentAnalyzer {

    private final Lock lock = new ReentrantLock();
    private final ClassAnalyzer classAnalyzer = new ClassAnalyzer();
    private final MethodSimulator simulator = new MethodSimulator();

    /**
     * Analyzes the sub-resource-locator method as a class result (which will be the content of a method result).
     *
     * @param method      The method
     * @param classResult The class result
     */
    void analyze(final CtMethod method, final ClassResult classResult) {
        lock.lock();
        try {
            buildPackagePrefix(method);

            final String returnType = determineReturnType(method);

            final CtClass subResource = ClassPool.getDefault().get(returnType);
            classAnalyzer.analyzeSubResource(subResource, classResult);
        } catch (NotFoundException e) {
            LogProvider.getLogger().accept("Could not load analyze sub-resource class ");
        } finally {
            lock.unlock();
        }
    }

    /**
     * Determines the return type of the sub-resource-locator by analyzing the bytecode.
     *
     * @return The return type
     */
    private String determineReturnType(final CtMethod method) throws NotFoundException {
        final List<Instruction> visitedInstructions = interpretRelevantInstructions(method);

        // find project defined methods in invoke occurrences
        final Set<ProjectMethod> projectMethods = findProjectMethods(visitedInstructions);

        // add project methods to global method pool
        projectMethods.stream().forEach(MethodPool.getInstance()::addProjectMethod);

        return simulator.simulate(visitedInstructions).getType();
    }

}
