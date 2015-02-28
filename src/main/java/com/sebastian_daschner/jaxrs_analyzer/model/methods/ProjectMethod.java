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

package com.sebastian_daschner.jaxrs_analyzer.model.methods;

import com.sebastian_daschner.jaxrs_analyzer.analysis.bytecode.simulation.InjectableArgumentMethodSimulator;
import com.sebastian_daschner.jaxrs_analyzer.model.instructions.Instruction;
import com.sebastian_daschner.jaxrs_analyzer.model.elements.Element;

import java.util.List;

/**
 * Represents a method which is defined in the analyzed project and will be invoked at interpret time with the correct arguments.
 *
 * @author Sebastian Daschner
 */
public class ProjectMethod implements IdentifiableMethod {

    private final MethodIdentifier identifier;
    private final List<Instruction> instructions;

    public ProjectMethod(final MethodIdentifier identifier, final List<Instruction> instructions) {
        this.identifier = identifier;
        this.instructions = instructions;
    }

    @Override
    public boolean matches(final MethodIdentifier identifier) {
        return this.identifier.equals(identifier);
    }

    @Override
    public Element invoke(final Element object, final List<Element> arguments) {
        return new InjectableArgumentMethodSimulator().simulate(arguments, instructions, identifier);
    }

}
