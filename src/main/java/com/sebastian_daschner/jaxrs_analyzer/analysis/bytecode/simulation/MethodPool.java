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

import com.sebastian_daschner.jaxrs_analyzer.LogProvider;
import com.sebastian_daschner.jaxrs_analyzer.model.elements.Element;
import com.sebastian_daschner.jaxrs_analyzer.model.methods.IdentifiableMethod;
import com.sebastian_daschner.jaxrs_analyzer.model.methods.Method;
import com.sebastian_daschner.jaxrs_analyzer.model.methods.MethodIdentifier;
import com.sebastian_daschner.jaxrs_analyzer.model.methods.ProjectMethod;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.sebastian_daschner.jaxrs_analyzer.model.Types.PRIMITIVE_VOID;

/**
 * A thread-safe singleton pool of known {@link Method}s.
 *
 * @author Sebastian Daschner
 */
public class MethodPool {

    /**
     * The only instance of the method pool.
     */
    private static final MethodPool INSTANCE = new MethodPool();
    private static final Function<MethodIdentifier, Method> DEFAULT_METHOD = identifier -> (object, arguments) -> {
        if (!PRIMITIVE_VOID.equals(identifier.getReturnType()))
            return new Element(identifier.getReturnType());
        return null;
    };

    private final Map<MethodIdentifier, IdentifiableMethod> availableMethods;
    private final ReadWriteLock readWriteLock;

    private MethodPool() {
        availableMethods = new HashMap<>();

        Stream.of(KnownResponseResultMethod.values()).forEach(knownResponseResultMethod -> availableMethods.put(knownResponseResultMethod.getIdentifier(), knownResponseResultMethod));
        Stream.of(KnownJsonResultMethod.values()).forEach(knownJsonResultMethod -> availableMethods.put(knownJsonResultMethod.getIdentifier(), knownJsonResultMethod));

        // could be made obsolete by using ConcurrentHashMap instead a HashMap for availableMethods
        readWriteLock = new ReentrantReadWriteLock();
    }

    /**
     * Adds a project method to the pool.
     *
     * @param method The method to add
     */
    public void addProjectMethod(final ProjectMethod method) {
        readWriteLock.writeLock().lock();
        try {
            availableMethods.put(method.getIdentifier(), method);

            // FIXME: just for debugging/testing
            if (availableMethods.containsKey(method.getIdentifier())) {
                final IdentifiableMethod method2 = availableMethods.get(method.getIdentifier());
                if(!method2.getIdentifier().equals(method.getIdentifier())) {
                    LogProvider.debug("replacing existing method in pool");
                }
            }
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    /**
     * Returns a method identified by an method identifier.
     *
     * @param identifier The method identifier
     * @return The found method or a default handler
     */
    public Method get(final MethodIdentifier identifier) {
        // search for available methods
        readWriteLock.readLock().lock();
        try {
            if (availableMethods.containsKey(identifier)) {
                return availableMethods.get(identifier);
            }
        } finally {
            readWriteLock.readLock().unlock();
        }

        // apply default behaviour
        return DEFAULT_METHOD.apply(identifier);
    }

    /**
     * Returns the singleton instance.
     *
     * @return The method pool
     */
    public static MethodPool getInstance() {
        return INSTANCE;
    }

}
