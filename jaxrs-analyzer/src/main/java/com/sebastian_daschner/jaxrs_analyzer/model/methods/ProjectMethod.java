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
