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

package com.sebastian_daschner.jaxrs_analyzer.analysis.results;

import com.sebastian_daschner.jaxrs_analyzer.model.JavaUtils;
import com.sebastian_daschner.jaxrs_analyzer.model.elements.HttpResponse;
import com.sebastian_daschner.jaxrs_analyzer.model.javadoc.ClassComment;
import com.sebastian_daschner.jaxrs_analyzer.model.javadoc.MemberComment;
import com.sebastian_daschner.jaxrs_analyzer.model.javadoc.MemberParameterTag;
import com.sebastian_daschner.jaxrs_analyzer.model.javadoc.MethodComment;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.MethodParameter;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.ResourceMethod;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.Resources;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.Response;
import com.sebastian_daschner.jaxrs_analyzer.model.results.ClassResult;
import com.sebastian_daschner.jaxrs_analyzer.model.results.MethodResult;

import java.util.Optional;
import java.util.Set;

import static com.sebastian_daschner.jaxrs_analyzer.analysis.results.JavaDocParameterResolver.*;

/**
 * Interprets the analyzed project results to REST results.
 *
 * @author Sebastian Daschner
 */
public class ResultInterpreter {

    private JavaTypeAnalyzer javaTypeAnalyzer;
    private Resources resources;
    private DynamicTypeAnalyzer dynamicTypeAnalyzer;
    private StringParameterResolver stringParameterResolver;

    /**
     * Interprets the class results.
     *
     * @return All REST resources
     */
    public Resources interpret(final Set<ClassResult> classResults) {
        resources = new Resources();
        resources.setBasePath(PathNormalizer.getApplicationPath(classResults));

        javaTypeAnalyzer = new JavaTypeAnalyzer(resources.getTypeRepresentations());
        dynamicTypeAnalyzer = new DynamicTypeAnalyzer(resources.getTypeRepresentations());
        stringParameterResolver = new StringParameterResolver(resources.getTypeRepresentations(), javaTypeAnalyzer);

        classResults.stream().filter(c -> c.getResourcePath() != null).forEach(this::interpretClassResult);
        resources.consolidateMultiplePaths();

        return resources;
    }

    /**
     * Interprets the class result.
     *
     * @param classResult The class result
     */
    private void interpretClassResult(final ClassResult classResult) {
        classResult.getMethods().forEach(m -> interpretMethodResult(m, classResult));
    }

    /**
     * Interprets the method result.
     *
     * @param methodResult The method result
     * @param classResult  The result of the containing class
     */
    private void interpretMethodResult(final MethodResult methodResult, final ClassResult classResult) {
        if (methodResult.getSubResource() != null) {
            interpretClassResult(methodResult.getSubResource());
            return;
        }

        // determine resource of the method
        final String path = PathNormalizer.getPath(methodResult);

        final ResourceMethod resourceMethod = interpretResourceMethod(methodResult, classResult);

        resources.addMethod(path, resourceMethod);
    }

    /**
     * Interprets the result of a resource method.
     *
     * @param methodResult The method result
     * @param classResult  The result of the containing class
     * @return The resource method which this method represents
     */
    private ResourceMethod interpretResourceMethod(final MethodResult methodResult, final ClassResult classResult) {
        final MethodComment methodDoc = methodResult.getMethodDoc();

        final String description = methodDoc != null ? methodDoc.getComment() : null;
        final ResourceMethod resourceMethod = new ResourceMethod(methodResult.getHttpMethod(), description);
        updateMethodParameters(resourceMethod.getMethodParameters(), classResult.getClassFields());
        updateMethodParameters(resourceMethod.getMethodParameters(), methodResult.getMethodParameters());

        addParameterDescriptions(resourceMethod.getMethodParameters(), methodDoc);
        stringParameterResolver.replaceParametersTypes(resourceMethod.getMethodParameters());

        if (methodResult.getRequestBodyType() != null) {
            resourceMethod.setRequestBody(javaTypeAnalyzer.analyze(methodResult.getRequestBodyType()));
            resourceMethod.setRequestBodyDescription(findRequestBodyDescription(methodDoc));
        }

        // add default status code due to JSR 339
        addDefaultResponses(methodResult);

        methodResult.getResponses().forEach(r -> interpretResponse(r, resourceMethod));

        addResponseComments(methodResult, resourceMethod);

        addMediaTypes(methodResult, classResult, resourceMethod);

        if (methodResult.isDeprecated() || classResult.isDeprecated() || hasDeprecationTag(methodDoc))
            resourceMethod.setDeprecated(true);

        return resourceMethod;
    }

    /**
     * Adds the comments for the individual status code to the corresponding Responses.
     * The information is based on the {@code @response} javadoc tags.
     */
    private void addResponseComments(MethodResult methodResult, ResourceMethod resourceMethod) {
        MethodComment methodDoc = methodResult.getMethodDoc();
        if (methodDoc == null)
            return;

        methodDoc.getResponseComments()
                .forEach((k, v) -> addResponseComment(k, v, resourceMethod));

        ClassComment classDoc = methodDoc.getContainingClassComment();

        // class-level response comments are added last (if absent) to keep hierarchy
        if (classDoc != null)
            classDoc.getResponseComments()
                    .forEach((k, v) -> addResponseComment(k, v, resourceMethod));
    }

    private void addResponseComment(Integer status, String comment, ResourceMethod resourceMethod) {
        resourceMethod.getResponses().putIfAbsent(status, new Response(null, comment));
    }

    private boolean hasDeprecationTag(MethodComment doc) {
        if (doc == null)
            return false;
        return doc.isDeprecated() || hasClassDeprecationTag(doc.getContainingClassComment());
    }

    private boolean hasClassDeprecationTag(MemberComment doc) {
        return doc != null && doc.isDeprecated();
    }

    private void addParameterDescriptions(final Set<MethodParameter> methodParameters, final MethodComment methodDoc) {
        if (methodDoc == null)
            return;

        methodParameters.forEach(p -> {
            final Optional<MemberParameterTag> tag = findParameterDoc(p, methodDoc);

            final String description = tag.map(MemberParameterTag::getComment)
                    .orElseGet(() -> findFieldDoc(p, methodDoc.getContainingClassComment())
                            .map(MemberParameterTag::getComment).orElse(null));

            p.setDescription(description);
        });
    }

    private String findRequestBodyDescription(final MethodComment methodDoc) {
        if (methodDoc == null)
            return null;
        return findRequestBodyDoc(methodDoc).map(MemberParameterTag::getComment).orElse(null);
    }

    /**
     * Updates {@code parameters} to contain the {@code additional} parameters as well.
     * Preexisting parameters with identical names are overridden.
     */
    private void updateMethodParameters(final Set<MethodParameter> parameters, final Set<MethodParameter> additional) {
        additional.forEach(a -> {
            // remove preexisting parameters with identical names
            final Optional<MethodParameter> existingParameter = parameters.stream().filter(p -> p.getName().equals(a.getName())).findAny();
            existingParameter.ifPresent(parameters::remove);
            parameters.add(a);
        });
    }

    private void addDefaultResponses(final MethodResult methodResult) {
        if (methodResult.getResponses().isEmpty()) {
            final HttpResponse httpResponse = new HttpResponse();
            httpResponse.getStatuses().add(javax.ws.rs.core.Response.Status.NO_CONTENT.getStatusCode());
            methodResult.getResponses().add(httpResponse);
            return;
        }

        methodResult.getResponses().stream().filter(r -> r.getStatuses().isEmpty())
                .forEach(r -> r.getStatuses().add(javax.ws.rs.core.Response.Status.OK.getStatusCode()));
    }

    private void interpretResponse(final HttpResponse httpResponse, final ResourceMethod method) {
        method.getResponseMediaTypes().addAll(httpResponse.getContentTypes());
        httpResponse.getStatuses().forEach(s -> {
            Response response = httpResponse.getInlineEntities().stream().findAny()
                    .map(JsonMapper::map).map(dynamicTypeAnalyzer::analyze).map(Response::new).orElse(null);

            if (response == null) {
                // no inline entities -> potential class type will be considered
                response = httpResponse.getEntityTypes().isEmpty() ? new Response() :
                        new Response(javaTypeAnalyzer.analyze(JavaUtils.determineMostSpecificType(httpResponse.getEntityTypes().toArray(new String[0]))));
            }

            response.getHeaders().addAll(httpResponse.getHeaders());

            method.getResponses().put(s, response);
        });
    }

    /**
     * Adds the request and response media type information to the resource method.
     *
     * @param methodResult   The method result
     * @param classResult    The class result
     * @param resourceMethod The resource method
     */
    private void addMediaTypes(final MethodResult methodResult, final ClassResult classResult, final ResourceMethod resourceMethod) {
        // accept media types -> inherit
        resourceMethod.getRequestMediaTypes().addAll(methodResult.getRequestMediaTypes());
        if (resourceMethod.getRequestMediaTypes().isEmpty()) {
            resourceMethod.getRequestMediaTypes().addAll(classResult.getRequestMediaTypes());
        }

        // response media types -> use annotations if not yet present
        if (resourceMethod.getResponseMediaTypes().isEmpty())
            resourceMethod.getResponseMediaTypes().addAll(methodResult.getResponseMediaTypes());
        // -> inherit
        if (resourceMethod.getResponseMediaTypes().isEmpty()) {
            resourceMethod.getResponseMediaTypes().addAll(classResult.getResponseMediaTypes());
        }
    }

}
