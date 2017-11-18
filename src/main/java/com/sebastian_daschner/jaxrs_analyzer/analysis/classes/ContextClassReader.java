package com.sebastian_daschner.jaxrs_analyzer.analysis.classes;

import org.objectweb.asm.ClassReader;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * A {@link ClassReader} that is able to use a separate {@link ClassLoader}.
 *
 * @author Sebastian Daschner
 */
public class ContextClassReader extends ClassReader {

    private static final ExtensibleClassLoader CLASS_LOADER = new ExtensibleClassLoader();

    public ContextClassReader(final String className) throws IOException {
        super(CLASS_LOADER.getResourceAsStream(className.replace('.', '/') + ".class"));
    }

    public static ClassLoader getClassLoader() {
        return CLASS_LOADER;
    }

    public static void addClassPath(final URL url) {
        CLASS_LOADER.addURL(url);
    }

    private static class ExtensibleClassLoader extends URLClassLoader {

        ExtensibleClassLoader() {
            super(new URL[]{});
        }

        @Override
        public void addURL(final URL url) {
            super.addURL(url);
        }

    }

}
