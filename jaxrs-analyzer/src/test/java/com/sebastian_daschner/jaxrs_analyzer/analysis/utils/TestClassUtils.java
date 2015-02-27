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
     * Returns all classes in the given package.
     *
     * @param packageName The package name where to search
     * @return All found classes
     * @throws ClassNotFoundException If a class could not be found
     */
    public static Set<Class<?>> getClasses(final String packageName) throws ClassNotFoundException {
        final Set<Class<?>> classes = new HashSet<>();

        final String postfixPackageName = packageName + '.';

        final String classPath = new File(TestClassUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getAbsolutePath();
        final Path testClassesDir = Paths.get(classPath + (classPath.endsWith(File.separator) ? "" : File.separatorChar) + postfixPackageName.replace('.', File.separatorChar));

        final File[] testClasses = testClassesDir.toFile().listFiles((dir, name) -> name.endsWith("class"));

        for (final File classFile : testClasses) {
            // load test class
            final String classFileName = classFile.getName();
            classes.add(Class.forName(postfixPackageName + classFileName.substring(0, classFileName.length() - ".class".length())));
        }

        return classes;
    }

}
