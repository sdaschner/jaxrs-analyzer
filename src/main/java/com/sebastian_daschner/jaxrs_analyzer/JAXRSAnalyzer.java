package com.sebastian_daschner.jaxrs_analyzer;

import com.sebastian_daschner.jaxrs_analyzer.analysis.ProjectAnalyzer;
import com.sebastian_daschner.jaxrs_analyzer.backend.BackendType;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.Project;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.Resources;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Generates REST documentation of JAX-RS projects automatically by bytecode analysis.
 *
 * @author Sebastian Daschner
 */
public class JAXRSAnalyzer {

    private static final String DEFAULT_NAME = "project";
    private static final String DEFAULT_VERSION = "0.1-SNAPSHOT";
    private static final String DEFAULT_DOMAIN = "example.com";
    private static final BackendType DEFAULT_BACKEND = BackendType.SWAGGER;

    private final Set<Path> classPaths = new HashSet<>();
    private final BackendType backendType;
    private final String projectName;
    private final String projectVersion;
    private final String domain;
    private final Path outputLocation;

    /**
     * Constructs a JAX-RS Analyzer.
     *
     * @param classPaths     The class paths (can either be directories or jar-files, at least one is mandatory)
     * @param backendType    The backend type (can be {@code null})
     * @param projectName    The project name (can be {@code null})
     * @param projectVersion The project version (can be {@code null})
     * @param domain         The domain (can be {@code null})
     * @param outputLocation The location of the output file (output will be printed to standard out if {@code null})
     */
    public JAXRSAnalyzer(final Set<Path> classPaths, final BackendType backendType, final String projectName, final String projectVersion, final String domain,
                         final Path outputLocation) {
        Objects.requireNonNull(classPaths);
        if (classPaths.isEmpty())
            throw new IllegalArgumentException("At least one class path is mandatory");

        this.classPaths.addAll(classPaths);
        this.projectName = Optional.ofNullable(projectName).orElse(DEFAULT_NAME);
        this.projectVersion = Optional.ofNullable(projectVersion).orElse(DEFAULT_VERSION);
        this.domain = Optional.ofNullable(domain).orElse(DEFAULT_DOMAIN);
        this.backendType = Optional.ofNullable(backendType).orElse(DEFAULT_BACKEND);
        this.outputLocation = outputLocation;
    }

    /**
     * Analyzes the JAX-RS project at the class path and produces the output as configured.
     */
    public void analyze() {
        final Resources resources = new ProjectAnalyzer(classPaths.toArray(new Path[classPaths.size()])).analyze();
        final Project project = new Project(projectName, projectVersion, domain, resources);

        final String output = backendType.getBackendSupplier().get().render(project);

        if (outputLocation != null) {
            outputToFile(output, outputLocation);
        } else {
            System.out.println(output);
        }
    }

    private static void outputToFile(final String output, final Path outputLocation) {
        try (final Writer writer = new BufferedWriter(new FileWriter(outputLocation.toFile()))) {
            writer.write(output);
            writer.flush();
        } catch (IOException ex) {
            System.err.println("Could not write to the specified output location, reason: " + ex.getMessage());
        }
    }

}
