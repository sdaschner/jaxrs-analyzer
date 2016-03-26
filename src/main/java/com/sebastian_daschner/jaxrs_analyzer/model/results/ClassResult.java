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

package com.sebastian_daschner.jaxrs_analyzer.model.results;

import com.sebastian_daschner.jaxrs_analyzer.model.rest.MethodParameters;

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
    private String originalClass;
    private final MethodParameters classFields = new MethodParameters();
    private final Set<MethodResult> methods = new HashSet<>();
    private final Set<String> requestMediaTypes = new HashSet<>();
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

    public String getOriginalClass() {
        return originalClass;
    }

    public void setOriginalClass(String originalClass) {
        this.originalClass = originalClass;
    }

    public MethodParameters getClassFields() {
        return classFields;
    }

    public Set<MethodResult> getMethods() {
        return Collections.unmodifiableSet(methods);
    }

    public void add(final MethodResult methodResult) {
        methods.add(methodResult);
        methodResult.setParentResource(this);
    }

    public Set<String> getRequestMediaTypes() {
        return requestMediaTypes;
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

        if (!requestMediaTypes.equals(that.requestMediaTypes)) return false;
        if (applicationPath != null ? !applicationPath.equals(that.applicationPath) : that.applicationPath != null)
            return false;
        if (!classFields.equals(that.classFields)) return false;
        if (!methods.equals(that.methods)) return false;
        if (resourcePath != null ? !resourcePath.equals(that.resourcePath) : that.resourcePath != null) return false;
        return responseMediaTypes.equals(that.responseMediaTypes);
    }

    @Override
    public int hashCode() {
        int result = applicationPath != null ? applicationPath.hashCode() : 0;
        result = 31 * result + (resourcePath != null ? resourcePath.hashCode() : 0);
        result = 31 * result + classFields.hashCode();
        result = 31 * result + methods.hashCode();
        result = 31 * result + requestMediaTypes.hashCode();
        result = 31 * result + responseMediaTypes.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ClassResult{" +
                "applicationPath='" + applicationPath + '\'' +
                ", resourcePath='" + resourcePath + '\'' +
                ", classFields=" + classFields +
                ", methods=" + methods +
                ", requestMediaTypes=" + requestMediaTypes +
                ", responseMediaTypes=" + responseMediaTypes +
                ", parentSubResourceLocator=" + (parentSubResourceLocator == null ? "null" : "notNull") +
                '}';
    }

}
