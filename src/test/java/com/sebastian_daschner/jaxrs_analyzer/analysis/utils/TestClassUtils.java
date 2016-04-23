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

package com.sebastian_daschner.jaxrs_analyzer.analysis.utils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class TestClassUtils {

    private TestClassUtils() {
        // no instances allowed
    }

    /**
     * Returns all root class names of compilation units in the given package.
     *
     * @param packageName The package name where to search
     * @return All found classes except inner classes
     * @throws ClassNotFoundException If a class could not be found
     */
    public static Set<String> getClasses(final String packageName) throws ClassNotFoundException {
        final Set<String> classes = new HashSet<>();

        final String postfixPackageName = packageName + '/';

        final String classPath = new File(TestClassUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getAbsolutePath();
        final Path testClassesDir = Paths.get(classPath + (classPath.endsWith(File.separator) ? "" : File.separatorChar) + postfixPackageName.replace('/', File.separatorChar));

        final File[] testClasses = testClassesDir.toFile().listFiles((dir, name) -> name.endsWith("class") && !name.contains("$"));

        for (final File classFile : testClasses) {
            // load test class
            final String classFileName = classFile.getName();
            classes.add(postfixPackageName + classFileName.substring(0, classFileName.length() - ".class".length()));
        }

        return classes;
    }

}
