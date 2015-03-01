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

package com.sebastian_daschner.jaxrs_analyzer.analysis;

import com.sebastian_daschner.jaxrs_analyzer.LogProvider;
import com.sebastian_daschner.jaxrs_analyzer.analysis.project.classes.ClassAnalyzer;
import com.sebastian_daschner.jaxrs_analyzer.analysis.results.ResultInterpreter;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.Resources;
import com.sebastian_daschner.jaxrs_analyzer.model.results.ClassResult;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Analyzes the JAX-RS project. This class is thread-safe.
 *
 * @author Sebastian Daschner
 */
public class ProjectAnalyzer {

    private final Lock lock = new ReentrantLock();
    private final ClassAnalyzer classAnalyzer = new ClassAnalyzer();
    private final ResultInterpreter resultInterpreter = new ResultInterpreter();

    /**
     * Creates a project analyzer with given dependency locations. The locations are included in the classpath.
     *
     * @param dependencyLocations The locations of additional project dependencies
     */
    public ProjectAnalyzer(final Path... dependencyLocations) {
        Stream.of(dependencyLocations).forEach(ProjectAnalyzer::addToClassPool);
    }

    /**
     * Analyzes all classes in the given project location.
     *
     * @param projectLocation The valid project root location
     * @return The REST resource representations
     */
    public Resources analyze(final Path projectLocation) {
        lock.lock();
        try {
            addToClassPool(projectLocation);

            // load classes
            final Set<CtClass> classes = getClasses(projectLocation.toString());

            // analyze relevant classes
            final Set<ClassResult> classResults = classes.stream()
                    .map(classAnalyzer::analyze)
                    .filter(Objects::nonNull).collect(Collectors.toSet());

            return resultInterpreter.interpret(classResults);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Adds the location to the class pool.
     *
     * @param location The location of a jar file or a directory
     */
    private static void addToClassPool(final Path location) {
        if (!location.toFile().exists())
            throw new IllegalArgumentException("The location '" + location + "' does not exist!");
        try {
            ClassPool.getDefault().insertClassPath(location.toString());
        } catch (NotFoundException e) {
            throw new IllegalArgumentException("The location '" + location + "' could not be loaded!", e);
        }
    }

    /**
     * Returns all classes in the given location and sub-locations recursively.
     *
     * @param projectLocation The project location where to search
     * @param packages        Each hierarchical packages of the current location
     * @return All found classes
     */
    private static Set<CtClass> getClasses(final String projectLocation, final String... packages) {
        final Set<CtClass> classes = new HashSet<>();
        final List<String> packageNames = Arrays.asList(packages);

        final Path projectPath = Paths.get(projectLocation);
        final Path currentPath = Paths.get(projectLocation, packages);

        final String packageName = Stream.of(packages).collect(Collectors.joining("."));

        final File[] classFiles = currentPath.toFile().listFiles((dir, name) -> name.endsWith("class"));

        for (final File classFile : classFiles) {
            // load test class
            final String classFileName = classFile.getName();
            final String className = classFileName.substring(0, classFileName.length() - ".class".length());

            try {
                final CtClass clazz = ClassPool.getDefault().getCtClass(packageName + '.' + className);
                classes.add(clazz);
            } catch (NotFoundException e) {
                LogProvider.getLogger().accept("Class " + className + " could not be loaded");
                // continue
            }
        }

        final File[] directories = currentPath.toFile().listFiles((dir, name) -> Paths.get(dir + "/" + name).toFile().isDirectory());

        for (final File directory : directories) {
            final List<String> newPackageNames = new ArrayList<>();
            newPackageNames.addAll(packageNames);
            newPackageNames.add(directory.getName());
            classes.addAll(getClasses(projectPath.toString(), newPackageNames.toArray(new String[newPackageNames.size()])));
        }

        return classes;
    }

}
