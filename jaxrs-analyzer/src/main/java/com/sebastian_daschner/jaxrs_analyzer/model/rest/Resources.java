package com.sebastian_daschner.jaxrs_analyzer.model.rest;

import java.util.*;

/**
 * Represents a set of resources and their possible methods.
 *
 * @author Sebastian Daschner
 */
public class Resources {

    private final Map<String, Set<ResourceMethod>> resources;
    private String basePath;

    public Resources() {
        resources = new HashMap<>();
    }

    /**
     * Adds the method to the resource's methods.
     *
     * @param resource The resource path where to add
     * @param method   The method to add
     */
    public void addMethod(final String resource, final ResourceMethod method) {
        resources.putIfAbsent(resource, new HashSet<>());
        resources.get(resource).add(method);
    }

    /**
     * Returns all resource paths.
     *
     * @return The resources
     */
    public Set<String> getResources() {
        return Collections.unmodifiableSet(resources.keySet());
    }

    /**
     * Returns the resource methods for a given resource.
     *
     * @param resource The resource path
     * @return The methods
     */
    public Set<ResourceMethod> getMethods(final String resource) {
        return Collections.unmodifiableSet(resources.get(resource));
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(final String basePath) {
        this.basePath = basePath;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Resources resources1 = (Resources) o;

        if (!resources.equals(resources1.resources)) return false;
        return !(basePath != null ? !basePath.equals(resources1.basePath) : resources1.basePath != null);
    }

    @Override
    public int hashCode() {
        int result = resources.hashCode();
        result = 31 * result + (basePath != null ? basePath.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Resources{" +
                "resources=" + resources +
                ", basePath='" + basePath + '\'' +
                '}';
    }

}
