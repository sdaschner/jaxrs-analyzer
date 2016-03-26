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
import com.sebastian_daschner.jaxrs_analyzer.analysis.bytecode.BytecodeAnalyzer;
import com.sebastian_daschner.jaxrs_analyzer.analysis.results.ResultInterpreter;
import com.sebastian_daschner.jaxrs_analyzer.analysis.classes.JAXRSClassVisitor;
import com.sebastian_daschner.jaxrs_analyzer.utils.Pair;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.Resources;
import com.sebastian_daschner.jaxrs_analyzer.model.results.ClassResult;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;

import javax.ws.rs.ApplicationPath;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

/**
 * Analyzes the JAX-RS project. This class is thread-safe.
 *
 * @author Sebastian Daschner
 */
public class ProjectAnalyzer {

    // TODO test following scenario:
    // 2 Maven modules -> a, b; a needs b
    // b contains interface with @Path & resource methods
    // a contains impl of iface without annotations
    // b should have result

    private final Lock lock = new ReentrantLock();
    private final Set<String> classes = new HashSet<>();
    private final ResultInterpreter resultInterpreter = new ResultInterpreter();
    private final BytecodeAnalyzer bytecodeAnalyzer = new BytecodeAnalyzer();
    private final URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();

    /**
     * Creates a project analyzer with given class path locations where to search for classes.
     *
     * @param classPaths The locations of additional class paths (can be directories or jar-files)
     */
    public ProjectAnalyzer(final Path... classPaths) {
        Stream.of(classPaths).forEach(this::addToClassPool);
    }

    /**
     * Analyzes all classes in the given project path.
     *
     * @param projectPaths The project paths
     * @return The REST resource representations
     */
    public Resources analyze(final Path... projectPaths) {
        lock.lock();
        try {
            Stream.of(projectPaths).forEach(this::addProjectPath);

            // analyze relevant classes
            final JobRegistry jobRegistry = JobRegistry.getInstance();
            final Set<ClassResult> classResults = new HashSet<>();

            classes.stream().filter(this::isJAXRSRootResource).forEach(c -> jobRegistry.analyzeResourceClass(c, new ClassResult()));

            Pair<String, ClassResult> classResultPair;
            while ((classResultPair = jobRegistry.nextUnhandledClass()) != null) {
                final ClassResult classResult = classResultPair.getRight();

                classResults.add(classResult);
                analyzeClass(classResultPair.getLeft(), classResult);

                bytecodeAnalyzer.analyzeBytecode(classResult);
            }

            return resultInterpreter.interpret(classResults);
        } finally {
            lock.unlock();
        }
    }

    private boolean isJAXRSRootResource(String className) {
        try {
            final Class<?> clazz = urlClassLoader.loadClass(className);
            return clazz.isAnnotationPresent(javax.ws.rs.Path.class) || clazz.isAnnotationPresent(ApplicationPath.class);
        } catch (ClassNotFoundException e) {
            LogProvider.error("The class " + className + " could not be loaded!");
            LogProvider.debug(e);
            return false;
        }
    }

    private void analyzeClass(final String className, ClassResult classResult) {
        try {
            final ClassReader classReader = new ClassReader(className);
            final ClassVisitor visitor = new JAXRSClassVisitor(classResult);

            classReader.accept(visitor, ClassReader.EXPAND_FRAMES);
        } catch (IOException e) {
            LogProvider.error("The class " + className + " could not be loaded!");
            LogProvider.debug(e);
        }
    }

    /**
     * Adds the location to the class pool.
     *
     * @param location The location of a jar file or a directory
     */
    private void addToClassPool(final Path location) {
        if (!location.toFile().exists())
            throw new IllegalArgumentException("The location '" + location + "' does not exist!");
        try {
            final Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true);
            method.invoke(urlClassLoader, location.toUri().toURL());
        } catch (Exception e) {
            throw new IllegalArgumentException("The location '" + location + "' could not be loaded to the class path!", e);
        }
    }

    /**
     * Adds the project paths and loads all classes.
     *
     * @param path The project path
     */
    private void addProjectPath(final Path path) {
        addToClassPool(path);

        if (path.toFile().isFile() && path.toString().endsWith(".jar")) {
            addJarClasses(path);
        } else if (path.toFile().isDirectory()) {
            addDirectoryClasses(path, Paths.get(""));
        } else {
            throw new IllegalArgumentException("The project path '" + path + "' must be a jar file or a directory");
        }
    }

    /**
     * Adds all classes in the given jar-file location to the set of known classes.
     *
     * @param location The location of the jar-file
     */
    private void addJarClasses(final Path location) {
        try (final JarFile jarFile = new JarFile(location.toFile())) {
            final Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                final String entryName = entries.nextElement().getName();
                if (entryName.endsWith(".class"))
                    classes.add(convertToQualifiedName(entryName));
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not read jar-file '" + location + "', reason: " + e.getMessage());
        }
    }

    /**
     * Adds all classes in the given directory location to the set of known classes.
     *
     * @param location The location of the current directory
     * @param subPath  The sub-path which is relevant for the package names or {@code null} if currently in the root directory
     */
    private void addDirectoryClasses(final Path location, final Path subPath) {
        for (final File file : location.toFile().listFiles()) {
            if (file.isDirectory())
                addDirectoryClasses(location.resolve(file.getName()), subPath.resolve(file.getName()));
            else if (file.isFile() && file.getName().endsWith(".class")) {
                final String classFileName = subPath.resolve(file.getName()).toString();
                classes.add(convertToQualifiedName(classFileName));
            }
        }
    }

    /**
     * Converts the given file name of a class-file to the fully-qualified class name.
     *
     * @param fileName The file name (e.g. a/package/AClass.class)
     * @return The fully-qualified class name (e.g. a.package.AClass)
     */
    private static String convertToQualifiedName(final String fileName) {
        final String replacedSeparators = fileName.replace(File.separatorChar, '.');
        return replacedSeparators.substring(0, replacedSeparators.length() - ".class".length());
    }

}
