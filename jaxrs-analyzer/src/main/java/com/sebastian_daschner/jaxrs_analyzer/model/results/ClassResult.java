package com.sebastian_daschner.jaxrs_analyzer.model.results;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a resource class result.
 *
 * @author Sebastian Daschner
 */
public class ClassResult {

    private String applicationPath;
    private String resourcePath;
    private final Set<MethodResult> methods = new HashSet<>();
    private final Set<String> acceptMediaTypes = new HashSet<>();
    private final Set<String> responseMediaTypes = new HashSet<>();
    private MethodResult parentSubResourceLocator;

    public String getApplicationPath() {
        return applicationPath;
    }

    public void setApplicationPath(final String applicationPath) {
        this.applicationPath = applicationPath;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public void setResourcePath(final String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public Set<MethodResult> getMethods() {
        return Collections.unmodifiableSet(methods);
    }

    public void add(final MethodResult methodResult) {
        methods.add(methodResult);
        methodResult.setParentResource(this);
    }

    public Set<String> getAcceptMediaTypes() {
        return acceptMediaTypes;
    }

    public Set<String> getResponseMediaTypes() {
        return responseMediaTypes;
    }

    public MethodResult getParentSubResourceLocator() {
        return parentSubResourceLocator;
    }

    public void setParentSubResourceLocator(final MethodResult parentSubResourceLocator) {
        this.parentSubResourceLocator = parentSubResourceLocator;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final ClassResult that = (ClassResult) o;

        if (!acceptMediaTypes.equals(that.acceptMediaTypes)) return false;
        if (applicationPath != null ? !applicationPath.equals(that.applicationPath) : that.applicationPath != null)
            return false;
        if (!methods.equals(that.methods)) return false;
        if (resourcePath != null ? !resourcePath.equals(that.resourcePath) : that.resourcePath != null) return false;
        return responseMediaTypes.equals(that.responseMediaTypes);
    }

    @Override
    public int hashCode() {
        int result = applicationPath != null ? applicationPath.hashCode() : 0;
        result = 31 * result + (resourcePath != null ? resourcePath.hashCode() : 0);
        result = 31 * result + methods.hashCode();
        result = 31 * result + acceptMediaTypes.hashCode();
        result = 31 * result + responseMediaTypes.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ClassResult{" +
                "applicationPath='" + applicationPath + '\'' +
                ", resourcePath='" + resourcePath + '\'' +
                ", methods=" + methods +
                ", acceptMediaTypes=" + acceptMediaTypes +
                ", responseMediaTypes=" + responseMediaTypes +
                ", parentSubResourceLocator=" + (parentSubResourceLocator == null ? "null" : "notNull") +
                '}';
    }

}
