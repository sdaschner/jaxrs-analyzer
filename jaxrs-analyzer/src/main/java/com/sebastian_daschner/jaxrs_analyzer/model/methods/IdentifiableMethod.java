package com.sebastian_daschner.jaxrs_analyzer.model.methods;

/**
 * Represents a method which is identifiable for a class name, method name and parameter types.
 *
 * @author Sebastian Daschner
 */
public interface IdentifiableMethod extends Method {

    /**
     * Checks if the given signature matches this method.
     *
     * @param identifier The method signature
     * @return {@code true} if this method matches the signature
     */
    boolean matches(final MethodIdentifier identifier);

}
