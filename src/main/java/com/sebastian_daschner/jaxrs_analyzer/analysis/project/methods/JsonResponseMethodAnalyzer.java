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
import com.sebastian_daschner.jaxrs_analyzer.model.elements.Element;
import com.sebastian_daschner.jaxrs_analyzer.model.elements.HttpResponse;
import com.sebastian_daschner.jaxrs_analyzer.model.elements.JsonValue;
import com.sebastian_daschner.jaxrs_analyzer.model.instructions.Instruction;
import com.sebastian_daschner.jaxrs_analyzer.model.methods.ProjectMethod;
import com.sebastian_daschner.jaxrs_analyzer.model.results.MethodResult;
import javassist.CtMethod;

import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Analyzes sub-resource-locator methods. This class is thread-safe.
 *
 * @author Sebastian Daschner
 */
class JsonResponseMethodAnalyzer extends MethodContentAnalyzer {

    private final Lock lock = new ReentrantLock();
    private final MethodSimulator simulator = new MethodSimulator();

    /**
     * Analyzes the method and gathers information about the returned JSON values.
     *
     * @param method The method to analyze
     * @param result The method result
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

            final Element returnedElement = simulator.simulate(visitedInstructions);

            final HttpResponse response = new HttpResponse();
            response.getEntityTypes().add(returnedElement.getType());
            returnedElement.getPossibleValues().stream().filter(this::filterJsonValue).map(e -> (JsonValue) e)
                    .forEach(response.getInlineEntities()::add);

            result.getResponses().add(response);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Checks if the given object is of type {@link JsonValue}. Logs an error message otherwise.
     *
     * @param object The object to check
     * @return {@code true} If object is of type JsonValue
     */
    private boolean filterJsonValue(final Object object) {
        if (object instanceof JsonValue) {
            return true;
        }
        LogProvider.getLogger().accept("Returned element of a JSON method is no JSON type.");
        return false;
    }

}
