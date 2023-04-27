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

import com.sebastian_daschner.jaxrs_analyzer.model.elements.HttpResponse;
import com.sebastian_daschner.jaxrs_analyzer.model.instructions.Instruction;
import com.sebastian_daschner.jaxrs_analyzer.model.javadoc.MethodComment;
import com.sebastian_daschner.jaxrs_analyzer.model.methods.MethodIdentifier;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.HttpMethod;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.MethodParameter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a resource method result.
 *
 * @author Sebastian Daschner
 */
public class MethodResult {

    private final Set<String> requestMediaTypes = new HashSet<>();
    private final Set<String> responseMediaTypes = new HashSet<>();
    private final Set<MethodParameter> methodParameters = new HashSet<>();
    private final Set<HttpResponse> responses = new HashSet<>();
    private final List<Instruction> instructions = new ArrayList<>();
    private String path;
    private String requestBodyType;
    private MethodIdentifier originalMethodSignature;
    private HttpMethod httpMethod;
    private ClassResult subResource;
    private ClassResult parentResource;
    private MethodComment methodDoc;
    private boolean deprecated;
    private boolean authRequired;

    public Set<String> getRequestMediaTypes() {
        return requestMediaTypes;
    }

    public Set<String> getResponseMediaTypes() {
        return responseMediaTypes;
    }

    public Set<MethodParameter> getMethodParameters() {
        return methodParameters;
    }

    public Set<HttpResponse> getResponses() {
        return responses;
    }

    public List<Instruction> getInstructions() {
        return instructions;
    }

    public String getPath() {
        return path;
    }

    public void setPath(final String path) {
        this.path = path;
    }

    public String getRequestBodyType() {
        return requestBodyType;
    }

    public void setRequestBodyType(final String requestBodyType) {
        this.requestBodyType = requestBodyType;
    }

    public MethodIdentifier getOriginalMethodSignature() {
        return originalMethodSignature;
    }

    public void setOriginalMethodSignature(MethodIdentifier originalMethodSignature) {
        this.originalMethodSignature = originalMethodSignature;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(final HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    public ClassResult getSubResource() {
        return subResource;
    }

    public void setSubResource(final ClassResult subResource) {
        this.subResource = subResource;
        subResource.setParentSubResourceLocator(this);
    }

    public ClassResult getParentResource() {
        return parentResource;
    }

    public void setParentResource(final ClassResult parentResource) {
        this.parentResource = parentResource;
    }

    public MethodComment getMethodDoc() {
        return methodDoc;
    }

    public void setMethodDoc(final MethodComment methodDoc) {
        this.methodDoc = methodDoc;
    }

    public boolean isDeprecated() {
        return deprecated;
    }

    public void setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
    }

	public void setAuthRequired(boolean authRequired) {
		this.authRequired = authRequired;
	}

	public boolean isAuthRequired() {
		return authRequired;
	}

	@Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final MethodResult that = (MethodResult) o;

        if (!requestMediaTypes.equals(that.requestMediaTypes)) return false;
        if (!responseMediaTypes.equals(that.responseMediaTypes)) return false;
        if (!methodParameters.equals(that.methodParameters)) return false;
        if (!responses.equals(that.responses)) return false;
        // TODO double-check
//        if (!instructions.equals(that.instructions)) return false;
        if (path != null ? !path.equals(that.path) : that.path != null) return false;
        if (requestBodyType != null ? !requestBodyType.equals(that.requestBodyType) : that.requestBodyType != null)
            return false;
        if (httpMethod != that.httpMethod) return false;
        if (subResource != null ? !subResource.equals(that.subResource) : that.subResource != null) return false;
        if (methodDoc != null ? !methodDoc.equals(that.methodDoc) : that.methodDoc != null) return false;
        if (deprecated != that.deprecated) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = requestMediaTypes.hashCode();
        result = 31 * result + responseMediaTypes.hashCode();
        result = 31 * result + methodParameters.hashCode();
        result = 31 * result + responses.hashCode();
//        result = 31 * result + instructions.hashCode();
        result = 31 * result + (path != null ? path.hashCode() : 0);
        result = 31 * result + (requestBodyType != null ? requestBodyType.hashCode() : 0);
        result = 31 * result + (httpMethod != null ? httpMethod.hashCode() : 0);
        result = 31 * result + (subResource != null ? subResource.hashCode() : 0);
        result = 31 * result + (methodDoc != null ? methodDoc.hashCode() : 0);
        result = 31 * result + (deprecated ? 1231 : 1237);
        return result;
    }

    @Override
    public String toString() {
        return "MethodResult{" +
                "requestMediaTypes=" + requestMediaTypes +
                ", responseMediaTypes=" + responseMediaTypes +
                ", methodParameters=" + methodParameters +
                ", responses=" + responses +
//                ", instructions=" + instructions +
                ", path='" + path + '\'' +
                ", requestBodyType='" + requestBodyType + '\'' +
                ", httpMethod=" + httpMethod +
                ", subResource=" + subResource +
                ", methodDoc=" + methodDoc +
                ", parentResource=" + (parentResource == null ? "null" : "notNull") +
                ", deprecated=" + deprecated +
                '}';
    }

}
