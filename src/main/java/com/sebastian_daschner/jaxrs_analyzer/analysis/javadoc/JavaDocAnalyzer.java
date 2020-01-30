package com.sebastian_daschner.jaxrs_analyzer.analysis.javadoc;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.sebastian_daschner.jaxrs_analyzer.LogProvider;
import com.sebastian_daschner.jaxrs_analyzer.model.JavaUtils;
import com.sebastian_daschner.jaxrs_analyzer.model.javadoc.MethodComment;
import com.sebastian_daschner.jaxrs_analyzer.model.methods.MethodIdentifier;
import com.sebastian_daschner.jaxrs_analyzer.model.results.ClassResult;
import com.sebastian_daschner.jaxrs_analyzer.model.results.MethodResult;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

/**
 * @author Sebastian Daschner
 */
public class JavaDocAnalyzer {

    private final Map<MethodIdentifier, MethodComment> methodComments = new HashMap<>();
    private final JavaDocParserVisitor javaDocParserVisitor = new JavaDocParserVisitor(methodComments);

    public JavaDocAnalyzerResults analyze(final Set<Path> projectSourcePaths, final Set<ClassResult> classResults) {
        invokeParser(projectSourcePaths);
        combineResults(classResults);
        return new JavaDocAnalyzerResults(classResults, javaDocParserVisitor.getClassComments());
    }

    private void invokeParser(Set<Path> projectSourcePaths) {
        try {
            for (Path projectSourcePath : projectSourcePaths) {
                invokeParser(projectSourcePath);
            }
        } catch (IOException e) {
            LogProvider.error("could not analyze JavaDoc, reason: " + e.getMessage());
            LogProvider.debug(e);
        }
    }

    private void invokeParser(Path sourcePath) throws IOException {
        Set<Path> files = new HashSet<>();

        Files.walkFileTree(sourcePath, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.toString().endsWith(".java"))
                    files.add(file);
                return super.visitFile(file, attrs);
            }
        });

        files.forEach(path -> parseJavaDoc(path, javaDocParserVisitor));
    }

    private static void parseJavaDoc(Path path, JavaDocParserVisitor visitor) {
        try {
            CompilationUnit cu = JavaParser.parse(path.toFile());
            cu.accept(visitor, null);
        } catch (FileNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    private void combineResults(final Set<ClassResult> classResults) {
        methodComments.forEach((key, value) -> classResults.stream()
                .map(c -> findMethodResult(key, c))
                .filter(Objects::nonNull)
                .forEach(m -> m.setMethodDoc(value)));
    }

    private MethodResult findMethodResult(final MethodIdentifier identifier, final ClassResult classResult) {
        if (classResult.getOriginalClass().equals(identifier.getContainingClass()))
            return classResult.getMethods().stream()
                    .filter(methodResult -> equalsSimpleTypeNames(identifier, methodResult))
                    .findAny().orElse(null);

        return classResult.getMethods().stream()
                .map(MethodResult::getSubResource)
                .filter(Objects::nonNull)
                .map(c -> findMethodResult(identifier, c))
                .filter(Objects::nonNull)
                .findAny().orElse(null);
    }

    /**
     * This is a best-effort approach combining only the simple types.
     *
     * @see JavaDocParserVisitor#calculateMethodIdentifier(MethodDeclaration)
     */
    private boolean equalsSimpleTypeNames(MethodIdentifier identifier, MethodResult methodResult) {
        MethodIdentifier originalIdentifier = methodResult.getOriginalMethodSignature();

        return originalIdentifier.getMethodName().equals(identifier.getMethodName()) &&
                matchesTypeBestEffort(originalIdentifier.getReturnType(), identifier.getReturnType()) &&
                parameterMatch(originalIdentifier.getParameters(), identifier.getParameters());
    }

    private boolean parameterMatch(List<String> originalTypes, List<String> types) {
        if (originalTypes.size() != types.size())
            return false;
        for (int i = 0; i < originalTypes.size(); i++) {
            if (!matchesTypeBestEffort(originalTypes.get(i), types.get(i)))
                return false;
        }
        return true;
    }

    private boolean matchesTypeBestEffort(String originalType, String type) {
        // if types are generic types, use full original type signature
        if (type.contains("<"))
            return Stream.of(type.replace(">", "").split("<")).allMatch(originalType::contains);
        // otherwise use class name (for primitives)
        return JavaUtils.toClassName(originalType).contains(type);
    }

}
