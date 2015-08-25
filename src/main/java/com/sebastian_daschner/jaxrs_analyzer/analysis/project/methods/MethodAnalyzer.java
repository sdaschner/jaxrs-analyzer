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

package com.sebastian_daschner.jaxrs_analyzer.analysis.project.methods;

import com.sebastian_daschner.jaxrs_analyzer.LogProvider;
import com.sebastian_daschner.jaxrs_analyzer.analysis.project.AnnotationInterpreter;
import com.sebastian_daschner.jaxrs_analyzer.analysis.utils.JavaUtils;
import com.sebastian_daschner.jaxrs_analyzer.model.elements.HttpResponse;
import com.sebastian_daschner.jaxrs_analyzer.model.results.ClassResult;
import com.sebastian_daschner.jaxrs_analyzer.model.results.MethodResult;
import com.sebastian_daschner.jaxrs_analyzer.model.types.Type;
import com.sebastian_daschner.jaxrs_analyzer.model.types.Types;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;

import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

/**
 * Analyzes a method by searching for JAX-RS relevant information. This class is thread-safe.
 *
 * @author Sebastian Daschner
 */
public class MethodAnalyzer {

    private static final Class<?>[] RELEVANT_METHOD_ANNOTATIONS = {Path.class, GET.class, PUT.class, POST.class, DELETE.class, OPTIONS.class, HEAD.class};
    private static final Class<?>[] RELEVANT_PARAMETER_ANNOTATIONS = {MatrixParam.class, QueryParam.class, PathParam.class, CookieParam.class,
            HeaderParam.class, FormParam.class, Context.class};

    private final Lock lock = new ReentrantLock();
    private final ResourceMethodContentAnalyzer resourceMethodAnalyzer = new ResourceMethodContentAnalyzer();
    private final SubResourceLocatorMethodContentAnalyzer subResourceLocatorMethodAnalyzer = new SubResourceLocatorMethodContentAnalyzer();
    private List<CtClass> superClasses;
    private CtMethod annotatedSuperMethod;
    private CtMethod method;

    /**
     * Analyzes the given method by searching for JAX-RS relevant information.
     *
     * @return The method result or {@code null} if the method is not relevant or could not be analyzed
     */
    public MethodResult analyze(final CtMethod ctMethod) {
        lock.lock();
        try {
            this.method = ctMethod;

            determineAnnotatedSuperMethod();

            if (!isRelevant())
                return null;

            return analyzeInternal();
        } catch (Exception e) {
            LogProvider.error("Could not analyze the method: " + method);
            LogProvider.debug(e);
            return null;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Determines a potential super method which is annotated with JAX-RS annotations.
     */
    private void determineAnnotatedSuperMethod() {
        superClasses = new LinkedList<>();
        try {
            determineSuperDeclarations();
        } catch (NotFoundException e) {
            LogProvider.error("Could not determine super classes");
            LogProvider.debug(e);
            // ignore
        }
        annotatedSuperMethod = MethodFinder.findFirstMethod(superClasses, method.getSignature(), MethodAnalyzer::hasJaxRsAnnotations);
    }

    /**
     * Determines all super classes and interfaces of the enclosing class recursively (exclusive the contained class itself and {@link java.lang.Object}).
     */
    private void determineSuperDeclarations() throws NotFoundException {
        CtClass ctClass = method.getDeclaringClass();
        final Queue<CtClass> classesToCheck = new LinkedBlockingQueue<>();

        do {
            if (ctClass.getSuperclass() != null && !Object.class.getName().equals(ctClass.getSuperclass().getName()))
                classesToCheck.add(ctClass.getSuperclass());

            Stream.of(ctClass.getInterfaces()).forEach(classesToCheck::add);

            if (!method.getDeclaringClass().equals(ctClass))
                superClasses.add(ctClass);

        } while ((ctClass = classesToCheck.poll()) != null);
    }

    /**
     * Checks if the method is somehow relevant for JAX-RS analysis.
     * This means the method itself or any super class or interface method definition which this method overrides has JAX-RS annotations.
     *
     * @return {@code true} if the method is relevant for analysis
     */
    private boolean isRelevant() {
        if (JavaUtils.isInitializerName(method.getName()))
            return false;

        if (hasJaxRsAnnotations(method))
            return true;

        return annotatedSuperMethod != null;
    }

    /**
     * Analyzes the current method.
     *
     * @return The method result
     * @throws BadBytecode If the method could not be analyzed
     */
    private MethodResult analyzeInternal() throws BadBytecode {
        final MethodResult methodResult = new MethodResult();

        analyzeMethodInformation(method, methodResult);

        if (methodResult.getHttpMethod() == null) {
            // method is a sub resource locator
            analyzeSubResourceLocator(methodResult);
            return methodResult;
        }

        final int modifiers = method.getModifiers();
        if (!Modifier.isNative(modifiers) && !Modifier.isAbstract(modifiers))
            analyzeMethodContent(methodResult);
        else {
            // build empty response with return type
            final Type returnType = JavaUtils.getReturnType(method, null);
            if (!Types.RESPONSE.equals(returnType) && !Types.PRIMITIVE_VOID.equals(returnType)) {
                final HttpResponse emptyResponse = new HttpResponse();
                emptyResponse.getEntityTypes().add(returnType);
                methodResult.getResponses().add(emptyResponse);
            }
        }

        return methodResult;
    }

    /**
     * Analyzes the method annotations and parameters.
     *
     * @param ctMethod     The method to analyze
     * @param methodResult The method result
     * @throws BadBytecode If the method could not be analyzed
     */
    private void analyzeMethodInformation(final CtMethod ctMethod, final MethodResult methodResult) throws BadBytecode {

        if (!hasJaxRsAnnotations(ctMethod) && annotatedSuperMethod != null) {
            // handle inherited annotations from superclass
            analyzeMethodInformation(annotatedSuperMethod, methodResult);
            return;
        }

        for (final Object annotation : ctMethod.getAvailableAnnotations()) {
            AnnotationInterpreter.interpretMethodAnnotation(annotation, methodResult);
        }

        analyzeMethodParameters(ctMethod, methodResult);
    }

    /**
     * Analyzes the method parameters and parameter annotations.
     *
     * @param ctMethod     The method to analyze
     * @param methodResult The method result
     * @throws BadBytecode If the method signature could not be analyzed
     */
    private void analyzeMethodParameters(final CtMethod ctMethod, final MethodResult methodResult) throws BadBytecode {
        // method parameters and parameter annotations
        final List<Type> parameterTypes = JavaUtils.getParameterTypes(ctMethod);

        final Object[][] parameterAnnotations = ctMethod.getAvailableParameterAnnotations();
        for (int index = 0; index < parameterTypes.size(); index++) {
            final Type parameterType = parameterTypes.get(index);

            if (isEntityParameter(ctMethod, index)) {
                methodResult.setRequestBodyType(parameterType);
                continue;
            }

            for (final Object annotation : parameterAnnotations[index]) {
                AnnotationInterpreter.interpretMethodParameterAnnotation(annotation, parameterType, methodResult);
            }
        }
    }

    /**
     * Analyzes the method as sub-resource-locator.
     *
     * @param methodResult The method result
     */
    private void analyzeSubResourceLocator(final MethodResult methodResult) {
        final ClassResult classResult = new ClassResult();
        methodResult.setSubResource(classResult);
        subResourceLocatorMethodAnalyzer.analyze(method, classResult);
    }

    /**
     * Analyzes the method return type and gathers further information for known return types (e.g. {@link Response} or {@link JsonObject}).
     *
     * @param methodResult The method result
     */
    private void analyzeMethodContent(final MethodResult methodResult) {
        resourceMethodAnalyzer.analyze(method, methodResult);
    }

    /**
     * Checks if the parameter with {@code index} of the method is the entity parameter of the JAX-RS method.
     *
     * @param ctMethod The method
     * @param index    The {@code index}-th parameter of the method
     * @return {@code true} if entity parameter
     */
    private boolean isEntityParameter(final CtMethod ctMethod, final int index) {
        for (final Object annotation : ctMethod.getAvailableParameterAnnotations()[index]) {
            if (Stream.of(RELEVANT_PARAMETER_ANNOTATIONS).anyMatch(c -> c.isAssignableFrom(annotation.getClass()))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the method has relevant JAX-RS annotations at the method itself or any method parameters.
     *
     * @return {@code true} if the method has relevant annotations
     */
    private static boolean hasJaxRsAnnotations(final CtMethod ctMethod) {
        for (final Object annotation : ctMethod.getAvailableAnnotations()) {
            if (Stream.of(RELEVANT_METHOD_ANNOTATIONS).anyMatch(c -> c.isAssignableFrom(annotation.getClass())))
                return true;
        }

        for (Object[] annotations : ctMethod.getAvailableParameterAnnotations()) {
            for (Object annotation : annotations) {
                if (Stream.of(RELEVANT_PARAMETER_ANNOTATIONS).anyMatch(c -> c.isAssignableFrom(annotation.getClass())))
                    return true;
            }
        }

        return false;
    }

}
