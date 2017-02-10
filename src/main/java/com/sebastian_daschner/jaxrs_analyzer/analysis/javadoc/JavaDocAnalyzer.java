package com.sebastian_daschner.jaxrs_analyzer.analysis.javadoc;

import com.sebastian_daschner.jaxrs_analyzer.LogProvider;
import com.sebastian_daschner.jaxrs_analyzer.analysis.classes.ContextClassReader;
import com.sebastian_daschner.jaxrs_analyzer.model.methods.MethodIdentifier;
import com.sebastian_daschner.jaxrs_analyzer.model.results.ClassResult;
import com.sebastian_daschner.jaxrs_analyzer.model.results.MethodResult;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Sebastian Daschner
 */
public class JavaDocAnalyzer {

    private static final Map<MethodIdentifier, MethodDoc> METHOD_DOCS = new ConcurrentHashMap<>();
    // TODO use class results for POJO / JAXB enhancement
    private static final Map<String, ClassDoc> CLASS_DOCS = new ConcurrentHashMap<>();

    public void analyze(final Set<ClassResult> classResults, final Set<String> packages, final Set<Path> projectSourcePaths, final Set<Path> classPaths) {
        try {
            invokeDoclet(packages, projectSourcePaths, classPaths);
            combineResults(classResults);
        } catch (Exception e) {
            LogProvider.error("could not analyze JavaDoc, reason: " + e.getMessage());
            LogProvider.debug(e);
        }
    }

    private void invokeDoclet(final Set<String> packages, final Set<Path> projectSourcePaths, final Set<Path> classPaths) throws Exception {
        final String docletName = "com.sebastian_daschner.jaxrs_analyzer.analysis.javadoc.JAXRSDoclet";
        final Class<?> doclet = ContextClassReader.getClassLoader().loadClass(docletName);
        final String docletPath = Paths.get(doclet.getProtectionDomain().getCodeSource().getLocation().toURI()).toString();
        final String encoding = System.getProperty("project.build.sourceEncoding", Charset.defaultCharset().name());

        // TODO only invoke on sources visited in visitSource
        final String[] args = Stream.concat(
                Stream.of("-sourcepath", joinPaths(projectSourcePaths),
                        "-classpath", joinPaths(classPaths),
                        "-quiet",
                        "-docletpath", docletPath,
                        "-doclet", docletName,
                        "-encoding", encoding
                ),
                packages.stream()
        ).toArray(String[]::new);
        final Class<?> javaDocMain = ContextClassReader.getClassLoader().loadClass("com.sun.tools.javadoc.Main");
        final int result = (int) javaDocMain.getMethod("execute", String[].class).invoke(null, (Object) args);
        if (result != 0)
            LogProvider.error("Error in javadoc analysis");
    }

    private String joinPaths(final Set<Path> projectSourcePaths) {
        return projectSourcePaths.stream().map(Path::toString).collect(Collectors.joining(File.pathSeparator));
    }

    private void combineResults(final Set<ClassResult> classResults) {
        METHOD_DOCS.entrySet().forEach(e -> classResults.stream()
                .map(c -> findMethodResult(e.getKey(), c))
                .filter(Objects::nonNull)
                .forEach(m -> m.setMethodDoc(e.getValue())));
    }

    private MethodResult findMethodResult(final MethodIdentifier identifier, final ClassResult classResult) {
        if (classResult.getOriginalClass().equals(identifier.getContainingClass()))
            return classResult.getMethods().stream()
                    .filter(methodResult -> methodResult.getOriginalMethodSignature().equals(identifier))
                    .findAny().orElse(null);

        return classResult.getMethods().stream()
                .map(MethodResult::getSubResource)
                .filter(Objects::nonNull)
                .map(c -> findMethodResult(identifier, c))
                .filter(Objects::nonNull)
                .findAny().orElse(null);
    }

    public static void put(final MethodIdentifier identifier, final MethodDoc methodDoc) {
        METHOD_DOCS.put(identifier, methodDoc);
    }

    public static void put(final String className, final ClassDoc classDoc) {
        CLASS_DOCS.put(className, classDoc);
    }

    public static MethodDoc get(final MethodIdentifier identifier) {
        return METHOD_DOCS.get(identifier);
    }

    public static ClassDoc get(final String className) {
        return CLASS_DOCS.get(className);
    }

}
