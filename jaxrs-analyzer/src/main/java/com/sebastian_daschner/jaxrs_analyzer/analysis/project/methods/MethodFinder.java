package com.sebastian_daschner.jaxrs_analyzer.analysis.project.methods;

import javassist.CtClass;
import javassist.CtMethod;

import java.util.Collection;
import java.util.function.Predicate;

/**
 * Contains functionality for finding Javassist methods.
 *
 * @author Sebastian Daschner
 */
final class MethodFinder {

    private MethodFinder() {
        throw new UnsupportedOperationException();
    }

    /**
     * Finds the first method with a specific signature which matches the predicate in the given classes.
     *
     * @param classes   The classes to search
     * @param signature The method signature (the Javassist signature is used)
     * @param predicate The predicate to test if the method is relevant
     * @return The first matching method or {@code null} if not found
     */
    static CtMethod findFirstMethod(final Collection<CtClass> classes, final String signature, final Predicate<CtMethod> predicate) {
        for (final CtClass ctClass : classes) {
            for (final CtMethod ctMethod : ctClass.getMethods()) {
                if (signature.equals(ctMethod.getSignature()) && predicate.test(ctMethod))
                    return ctMethod;
            }
        }
        return null;
    }

}
