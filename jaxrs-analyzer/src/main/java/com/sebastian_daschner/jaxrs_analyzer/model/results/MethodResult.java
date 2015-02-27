package com.sebastian_daschner.jaxrs_analyzer.model.results;

import com.sebastian_daschner.jaxrs_analyzer.model.elements.HttpResponse;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.HttpMethod;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.MethodParameters;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a resource method result.
 *
 * @author Sebastian Daschner
 */
public class MethodResult {

    private final Set<String> acceptMediaTypes = new HashSet<>();
    private final Set<String> responseMediaTypes = new HashSet<>();
    private final MethodParameters methodParameters = new MethodParameters();
    private final Set<HttpResponse> responses = new HashSet<>();
    private String path;
    private String requestBodyType;
    private HttpMethod httpMethod;
    private ClassResult subResource;
    private ClassResult parentResource;

    public Set<String> getAcceptMediaTypes() {
        return acceptMediaTypes;
    }

    public Set<String> getResponseMediaTypes() {
        return responseMediaTypes;
    }

    public MethodParameters getMethodParameters() {
        return methodParameters;
    }

    public Set<HttpResponse> getResponses() {
        return responses;
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

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final MethodResult that = (MethodResult) o;

        if (!acceptMediaTypes.equals(that.acceptMediaTypes)) return false;
        if (!responseMediaTypes.equals(that.responseMediaTypes)) return false;
        if (!methodParameters.equals(that.methodParameters)) return false;
        if (!responses.equals(that.responses)) return false;
        if (path != null ? !path.equals(that.path) : that.path != null) return false;
        if (requestBodyType != null ? !requestBodyType.equals(that.requestBodyType) : that.requestBodyType != null)
            return false;
        if (httpMethod != that.httpMethod) return false;
        if (subResource != null ? !subResource.equals(that.subResource) : that.subResource != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = acceptMediaTypes.hashCode();
        result = 31 * result + responseMediaTypes.hashCode();
        result = 31 * result + methodParameters.hashCode();
        result = 31 * result + responses.hashCode();
        result = 31 * result + (path != null ? path.hashCode() : 0);
        result = 31 * result + (requestBodyType != null ? requestBodyType.hashCode() : 0);
        result = 31 * result + (httpMethod != null ? httpMethod.hashCode() : 0);
        result = 31 * result + (subResource != null ? subResource.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MethodResult{" +
                "acceptMediaTypes=" + acceptMediaTypes +
                ", responseMediaTypes=" + responseMediaTypes +
                ", methodParameters=" + methodParameters +
                ", responses=" + responses +
                ", path='" + path + '\'' +
                ", requestBodyType='" + requestBodyType + '\'' +
                ", httpMethod=" + httpMethod +
                ", subResource=" + subResource +
                ", parentResource=" + (parentResource == null ? "null" : "notNull") +
                '}';
    }

}
