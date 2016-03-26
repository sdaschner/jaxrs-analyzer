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

package com.sebastian_daschner.jaxrs_analyzer.model.elements;

import com.sebastian_daschner.jaxrs_analyzer.analysis.bytecode.simulation.MethodPool;
import com.sebastian_daschner.jaxrs_analyzer.model.JavaUtils;
import com.sebastian_daschner.jaxrs_analyzer.model.methods.Method;
import com.sebastian_daschner.jaxrs_analyzer.model.methods.MethodIdentifier;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Represents a method handle element on the stack.
 * The method handles are created when working with lambdas or method references.
 *
 * @author Sebastian Daschner
 */
public class MethodHandle extends Element implements Method {

    /**
     * The possible identifier of the method which are encapsulated.
     */
    private final Set<MethodIdentifier> possibleIdentifiers = new HashSet<>();

    /**
     * The arguments which are transferred from the bootstrap call to the later invoke.
     */
    private final List<Element> transferredArguments = new LinkedList<>();

    public MethodHandle(final String returnType, final MethodIdentifier handleIdentifier, final List<Element> transferredArguments) {
        super(returnType);
        this.possibleIdentifiers.add(handleIdentifier);
        this.transferredArguments.addAll(transferredArguments);
    }

    public MethodHandle(final MethodHandle methodHandle) {
        super(methodHandle.getTypes());
        this.possibleIdentifiers.addAll(methodHandle.possibleIdentifiers);
        this.transferredArguments.addAll(methodHandle.transferredArguments);
    }

    public Set<MethodIdentifier> getPossibleIdentifiers() {
        return possibleIdentifiers;
    }

    public List<Element> getTransferredArguments() {
        return transferredArguments;
    }

    @Override
    public Element invoke(final Element unused, final List<Element> arguments) {
        // transfer invoke dynamic arguments
        final List<Element> combinedArguments = Stream.concat(transferredArguments.stream(), arguments.stream()).collect(Collectors.toList());
        return possibleIdentifiers.stream()
                .map(i -> {
                    final Method method = MethodPool.getInstance().get(i);
                    if (!i.isStaticMethod()) {
                        final List<Element> actualArguments = new ArrayList<>(combinedArguments);
                        final Element object = actualArguments.isEmpty() ? Element.EMPTY : actualArguments.remove(0);
                        if (JavaUtils.isInitializerName(i.getMethodName())) {
                            return new Element(i.getContainingClass());
                        }
                        return method.invoke(object, actualArguments);
                    }
                    return method.invoke(null, combinedArguments);
                })
                .filter(Objects::nonNull).reduce(Element::merge).orElse(null);
    }

    @Override
    public Element merge(final Element element) {
        super.merge(element);
        if (element instanceof MethodHandle) {
            final MethodHandle methodHandle = (MethodHandle) element;
            possibleIdentifiers.addAll(methodHandle.possibleIdentifiers);
            IntStream.range(0, Math.max(transferredArguments.size(), methodHandle.transferredArguments.size()))
                    .forEach(i -> {
                        if (transferredArguments.size() < i && methodHandle.transferredArguments.size() < i)
                            transferredArguments.get(i).merge(methodHandle.transferredArguments.get(i));
                        // more arguments from second method handle will be ignored
                    });
        }
        return this;
    }

    @Override
    public String toString() {
        return "MethodHandle{" +
                "possibleIdentifiers=" + possibleIdentifiers +
                "transferredArguments=" + transferredArguments +
                ", type='" + getTypes() + '\'' +
                '}';
    }

}
