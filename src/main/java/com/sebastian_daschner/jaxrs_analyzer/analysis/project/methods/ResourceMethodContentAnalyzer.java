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

import com.sebastian_daschner.jaxrs_analyzer.analysis.bytecode.simulation.MethodPool;
import com.sebastian_daschner.jaxrs_analyzer.analysis.bytecode.simulation.MethodSimulator;
import com.sebastian_daschner.jaxrs_analyzer.model.elements.Element;
import com.sebastian_daschner.jaxrs_analyzer.model.elements.HttpResponse;
import com.sebastian_daschner.jaxrs_analyzer.model.elements.JsonValue;
import com.sebastian_daschner.jaxrs_analyzer.model.instructions.Instruction;
import com.sebastian_daschner.jaxrs_analyzer.model.methods.ProjectMethod;
import com.sebastian_daschner.jaxrs_analyzer.model.results.MethodResult;
import javassist.CtMethod;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.SignatureAttribute;

import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * Analyzes JAX-RS resource methods. This class is thread-safe.
 *
 * @author Sebastian Daschner
 */
class ResourceMethodContentAnalyzer extends MethodContentAnalyzer {

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

            // TODO handle void resource methods
            final Element returnedElement = methodSimulator.simulate(visitedInstructions);
            final String returnType = determineReturnType(method);

            final Set<Object> possibleObjects = returnedElement.getPossibleValues().stream().filter(o -> !(o instanceof HttpResponse))
                    .collect(Collectors.toSet());

            // for non-Response methods add a default if there are non-Response objects or none objects at all
            if (!Response.class.getName().equals(returnType)) {
                final HttpResponse defaultResponse = new HttpResponse();
                defaultResponse.getEntityTypes().add((Object.class.getName().equals(returnType)) ? returnedElement.getType() : returnType);
                possibleObjects.stream().filter(o -> o instanceof JsonValue).map(o -> (JsonValue) o).forEach(defaultResponse.getInlineEntities()::add);

                result.getResponses().add(defaultResponse);
            }

            returnedElement.getPossibleValues().stream().filter(o -> o instanceof HttpResponse).map(o -> (HttpResponse) o)
                    .forEach(result.getResponses()::add);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Returns the return type of the method. The parameterized type is taken, if generics are used.
     *
     * @param method The method
     * @return The return type
     * @throws IllegalArgumentException If the method signature could not be analyzed
     */
    private static String determineReturnType(final CtMethod method) {
        try {
            final String sig = method.getGenericSignature() != null ? method.getGenericSignature() : method.getSignature();
            return SignatureAttribute.toMethodSignature(sig).getReturnType().toString();
        } catch (BadBytecode e) {
            throw new IllegalArgumentException(e);
        }
    }

}
