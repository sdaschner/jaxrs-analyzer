package com.sebastian_daschner.jaxrs_analyzer.analysis.bytecode.simulation;

import com.sebastian_daschner.jaxrs_analyzer.model.methods.MethodIdentifier;
import com.sebastian_daschner.jaxrs_analyzer.model.elements.Element;
import com.sebastian_daschner.jaxrs_analyzer.model.methods.IdentifiableMethod;
import com.sebastian_daschner.jaxrs_analyzer.model.methods.Method;
import com.sebastian_daschner.jaxrs_analyzer.model.methods.ProjectMethod;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import java.util.stream.Stream;

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
//        System.err.println("applying DEFAULT method for: " + identifier + " (" + object + ", " + arguments + ')');
        if (identifier.getReturnType() != null)
            return new Element(identifier.getReturnType());
        return null;
    };

    private final List<IdentifiableMethod> availableMethods;
    private final ReadWriteLock readWriteLock;

    private MethodPool() {
        availableMethods = new LinkedList<>();

        // order matters, known methods are taken first
        Stream.of(KnownResponseResultMethod.values()).forEach(availableMethods::add);
        Stream.of(KnownJsonResultMethod.values()).forEach(availableMethods::add);

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
            availableMethods.add(method);
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
            final Optional<? extends IdentifiableMethod> method = availableMethods.stream().filter(m -> m.matches(identifier)).findAny();
            if (method.isPresent())
                return method.get();
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
