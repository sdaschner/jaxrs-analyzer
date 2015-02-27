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
import com.sebastian_daschner.jaxrs_analyzer.model.elements.HttpResponse;
import com.sebastian_daschner.jaxrs_analyzer.analysis.bytecode.simulation.MethodSimulator;
import com.sebastian_daschner.jaxrs_analyzer.model.elements.Element;
import com.sebastian_daschner.jaxrs_analyzer.model.instructions.Instruction;
import com.sebastian_daschner.jaxrs_analyzer.model.methods.ProjectMethod;
import com.sebastian_daschner.jaxrs_analyzer.model.results.MethodResult;
import javassist.CtMethod;

import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Analyzes JAX-RS related methods which return a {@link javax.ws.rs.core.Response}. This class is thread-safe.
 *
 * @author Sebastian Daschner
 */
class ResponseMethodAnalyzer extends MethodContentAnalyzer {

    private final Lock lock = new ReentrantLock();
    private final MethodSimulator methodSimulator = new MethodSimulator();

    /**
     * Analyzes the method (including own project methods).
     *
     * @param method The method to analyze
     * @param result The result
     */
    void analyze(final CtMethod method, final MethodResult result) {
        lock.lock();
        try {
            buildPackagePrefix(method);

            final List<Instruction> visitedInstructions = interpretRelevantInstructions(method);

            // find project defined methods in invoke occurrences
            final Set<ProjectMethod> projectMethods = findProjectMethods(visitedInstructions);

            // add project methods to global method pool
            projectMethods.stream().forEach(MethodPool.getInstance()::addProjectMethod);

            final Element returnedElement = methodSimulator.simulate(visitedInstructions);

            returnedElement.getPossibleValues().stream().filter(this::filterHttpResponse).map(r -> (HttpResponse) r)
                    .forEach(result.getResponses()::add);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Checks if the given object is of type {@link HttpResponse}. Logs an error message otherwise.
     *
     * @param object The object to check
     * @return {@code true} If object is of type HttpResponse
     */
    private boolean filterHttpResponse(final Object object) {
        if (object instanceof HttpResponse) {
            return true;
        }
        LogProvider.getLogger().accept("Returned element of a Response method is no HttpResponse type.");
        return false;
    }

}
