package com.sebastian_daschner.jaxrs_analyzer;

import com.sebastian_daschner.jaxrs_analyzer.analysis.ProjectAnalyzer;
import com.sebastian_daschner.jaxrs_analyzer.backend.Backend;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.Project;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.Resources;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Generates REST documentation of JAX-RS projects automatically by bytecode analysis.
 *
 * @author Sebastian Daschner
 */
public class JAXRSAnalyzer {

    private final Set<Path> projectPaths = new HashSet<>();
    private final Set<Path> classPaths = new HashSet<>();
    private final String projectName;
    private final String projectVersion;
    private final Path outputLocation;
    private final Backend backend;

    /**
     * Constructs a JAX-RS Analyzer.
     *
     * @param projectPaths   The paths of the projects to be analyzed (can either be directories or jar-files, at least one is mandatory)
     * @param classPaths     The additional class paths (can either be directories or jar-files)
     * @param projectName    The project name
     * @param projectVersion The project version
     * @param backend        The backend to render the output
     * @param outputLocation The location of the output file (output will be printed to standard out if {@code null})
     */
    public JAXRSAnalyzer(final Set<Path> projectPaths, final Set<Path> classPaths, final String projectName, final String projectVersion,
                         final Backend backend, final Path outputLocation) {
        Objects.requireNonNull(projectPaths);
        Objects.requireNonNull(classPaths);
        Objects.requireNonNull(projectName);
        Objects.requireNonNull(projectVersion);
        Objects.requireNonNull(backend);

        if (projectPaths.isEmpty())
            throw new IllegalArgumentException("At least one project path is mandatory");

        this.projectPaths.addAll(projectPaths);
        this.classPaths.addAll(classPaths);
        this.projectName = projectName;
        this.projectVersion = projectVersion;
        this.outputLocation = outputLocation;
        this.backend = backend;
    }

    /**
     * Analyzes the JAX-RS project at the class path and produces the output as configured.
     */
    public void analyze() {
        final Resources resources = new ProjectAnalyzer(classPaths.toArray(new Path[classPaths.size()])).analyze(projectPaths.toArray(new Path[projectPaths.size()]));

        if (resources.isEmpty()) {
            LogProvider.info("Empty JAX-RS analysis result, omitting output");
            return;
        }

        final Project project = new Project(projectName, projectVersion, resources);
        final String output = backend.render(project);

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
        } catch (IOException e) {
            LogProvider.error("Could not write to the specified output location, reason: " + e.getMessage());
            LogProvider.debug(e);
        }
    }

}
