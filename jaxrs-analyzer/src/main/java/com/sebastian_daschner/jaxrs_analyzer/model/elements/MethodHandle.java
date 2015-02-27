package com.sebastian_daschner.jaxrs_analyzer.model.elements;

import com.sebastian_daschner.jaxrs_analyzer.model.methods.MethodIdentifier;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

/**
 * Represents a method handle element on the stack.
 * The method handles are created when working with lambdas or method references.
 *
 * @author Sebastian Daschner
 */
public class MethodHandle extends Element {

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
        super(methodHandle.getType());
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
                ", type='" + getType() + '\'' +
                '}';
    }

}
