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

package com.sebastian_daschner.jaxrs_analyzer.analysis.project.classes;

import com.sebastian_daschner.jaxrs_analyzer.LogProvider;
import com.sebastian_daschner.jaxrs_analyzer.analysis.project.AnnotationInterpreter;
import com.sebastian_daschner.jaxrs_analyzer.analysis.project.methods.MethodAnalyzer;
import com.sebastian_daschner.jaxrs_analyzer.model.results.ClassResult;
import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Path;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

/**
 * Analyzes a class by searching for JAX-RS relevant information. This class is thread-safe.
 *
 * @author Sebastian Daschner
 */
public class ClassAnalyzer {

    private static final Class<?>[] RELEVANT_CLASS_ANNOTATIONS = {ApplicationPath.class, Path.class};

    private final Lock lock = new ReentrantLock();
    private CtClass ctClass;

    /**
     * Analyzes the given class by searching for JAX-RS relevant information (in both the annotations and the byte code of methods).
     *
     * @param ctClass The class to analyze
     * @return The class result including the results for the methods or {@code null} if the class was not found or is not relevant
     */
    public ClassResult analyze(final CtClass ctClass) {
        lock.lock();
        try {
            this.ctClass = ctClass;

            if (ctClass == null || !isRelevant()) {
                return null;
            }

            final ClassResult classResult = new ClassResult();

            analyzeAnnotations(classResult);
            analyzeInternal(classResult);

            return classResult;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Analyzes the current sub-resource class.
     *
     * @param ctClass     The sub-resource class to analyze
     * @param classResult The class result for the sub-resource
     */
    public void analyzeSubResource(final CtClass ctClass, final ClassResult classResult) {
        try {
            lock.lock();
            this.ctClass = ctClass;

            if (ctClass == null)
                return;

            // no annotation analysis on a sub resource class

            analyzeInternal(classResult);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Analyzes the class annotations.
     *
     * @param classResult The class result
     */
    private void analyzeAnnotations(final ClassResult classResult) {
        for (final Object annotation : ctClass.getAvailableAnnotations()) {
            AnnotationInterpreter.interpretClassAnnotation(annotation, classResult);
        }
    }

    /**
     * Analyzes the current class by searching for JAX-RS relevant information (in both the annotations and the byte code of methods).
     *
     * @param classResult The class result to update
     */
    private void analyzeInternal(final ClassResult classResult) {

        // REST activator doesn't need further analyzing
        if (classResult.getApplicationPath() != null)
            return;

        final MethodAnalyzer methodAnalyzer = new MethodAnalyzer();

        Stream.of(ctClass.getMethods())
                .map(methodAnalyzer::analyze).filter(Objects::nonNull)
                .forEach(classResult::add);

        analyzeFields(classResult);
    }

    /**
     * Checks if the class is a JAX-RS application or root resource class.
     *
     * @return {@code true} if the class is relevant and should be analyzed
     */
    private boolean isRelevant() {
        for (final Object annotation : ctClass.getAvailableAnnotations()) {
            if (Stream.of(RELEVANT_CLASS_ANNOTATIONS).anyMatch(c -> c.isAssignableFrom(annotation.getClass()))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Analyzes the annotated JAX-RS fields on this class. Inherits the fields to all method results.
     *
     * @param classResult The class result to update
     */
    private void analyzeFields(final ClassResult classResult) {
        for (CtField ctField : ctClass.getDeclaredFields()) {
            try {
                final String fieldType = ctField.getType().getName();
                Stream.of(ctField.getAnnotations()).forEach(a -> AnnotationInterpreter.interpretFieldAnnotation(a, fieldType, classResult.getMethods()));

            } catch (ClassNotFoundException | NotFoundException e) {
                LogProvider.error("Could not analyze class field " + e.getMessage());
                LogProvider.debug(e);
            }
        }
    }

}
