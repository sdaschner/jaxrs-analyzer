package com.sebastian_daschner.jaxrs_analyzer.analysis;

import com.sebastian_daschner.jaxrs_analyzer.model.results.ClassResult;
import com.sebastian_daschner.jaxrs_analyzer.utils.Pair;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Thread-safe container of unhandled class analysis jobs.
 *
 * @author Sebastian Daschner
 */
public class JobRegistry {

    private final Queue<Pair<String, ClassResult>> unhandledClasses = new ConcurrentLinkedQueue<>();

    public JobRegistry() {
    }

    /**
     * Adds the (sub-)resource class name to the analysis list with the associated class result.
     */
    public void analyzeResourceClass(final String className, final ClassResult classResult) {
        // TODO check if class has already been analyzed
        unhandledClasses.add(Pair.of(className, classResult));
    }

    /**
     * Returns a class which has not been analyzed yet.
     *
     * @return An unhandled class or {@code null} if all classes have been analyzed
     */
    public Pair<String, ClassResult> nextUnhandledClass() {
        return unhandledClasses.poll();
    }
}
