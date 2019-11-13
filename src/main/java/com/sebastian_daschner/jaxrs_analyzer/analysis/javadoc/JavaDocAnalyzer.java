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
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Stream;

/**
 * @author Sebastian Daschner
 */
public class JavaDocAnalyzer {

    private final Map<MethodIdentifier, MethodComment> methodComments = new HashMap<>();

    public void analyze(final Set<Path> projectSourcePaths, final Set<ClassResult> classResults) {
        invokeParser(projectSourcePaths);
        combineResults(classResults);
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

        files.forEach(path -> parseJavaDoc(path, new JavaDocParserVisitor(methodComments)));
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
        classResults.stream()
            .flatMap(classResult -> classResult.getMethods().stream()) // flatten our set of sets (multiple methods within multiple class results) into one set
            .forEach(methodResult -> {
                // For our single methodResult, see if we have a matching entry in the methodComments map (if we do, set the 'methodDoc' property)
                this.methodComments.entrySet().stream()
                    .filter(entry -> equalsSimpleTypeNames(entry.getKey(), methodResult))
                    .map(Entry::getValue)
                    .findAny()
                    .ifPresent(methodResult::setMethodDoc);
            });
    }

    /**
     * This is a best-effort approach combining only the simple types.
     * @param identifier the MethodIdentifier from methodComments.
     * @param methodResult the MethodResult (containing a MethodIdentifier in originalMethodSignature) from classResults.
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
