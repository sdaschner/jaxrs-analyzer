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

package com.sebastian_daschner.jaxrs_analyzer.model.rest;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a set of resources and their possible methods.
 *
 * @author Sebastian Daschner
 */
public class Resources {

    private Map<String, Set<ResourceMethod>> resources = new HashMap<>();
    private final Map<TypeIdentifier, TypeRepresentation> typeRepresentations = new HashMap<>();
    private String basePath;

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

    /**
     * Checks if the resources contain reasonable data, i.e. actual methods mapped to resources.
     */
    public boolean isEmpty() {
        return resources.isEmpty() || resources.values().stream().allMatch(Set::isEmpty);
    }

    /**
     * Consolidates the information contained in multiple responses for the same path.
     * Internally creates new resources.
     */
    public void consolidateMultiplePaths() {
        Map<String, Set<ResourceMethod>> oldResources = resources;
        resources = new HashMap<>();

        oldResources.keySet().forEach(s -> consolidateMultipleMethodsForSamePath(s, oldResources.get(s)));
    }

    private void consolidateMultipleMethodsForSamePath(String path, Set<ResourceMethod> resourceMethods) {
        resourceMethods.stream()
                .collect(Collectors.groupingBy(m -> m.getMethod().toString().toLowerCase(),
                        Collectors.reducing(new ResourceMethod(), ResourceMethod::combine))
                ).forEach((k, v) -> addMethod(path, v));
    }

    public Map<TypeIdentifier, TypeRepresentation> getTypeRepresentations() {
        return typeRepresentations;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(final String basePath) {
        this.basePath = basePath;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        Resources that = (Resources) object;

        if (!resources.equals(that.resources)) return false;
        if (!typeRepresentations.equals(that.typeRepresentations)) return false;
        return !(basePath != null ? !basePath.equals(that.basePath) : that.basePath != null);
    }

    @Override
    public int hashCode() {
        int result = resources.hashCode();
        result = 31 * result + typeRepresentations.hashCode();
        result = 31 * result + (basePath != null ? basePath.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Resources{" +
                "resources=" + resources +
                ", typeRepresentations=" + typeRepresentations +
                ", basePath='" + basePath + '\'' +
                '}';
    }

}
