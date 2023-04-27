package com.sebastian_daschner.jaxrs_analyzer;

import com.sebastian_daschner.jaxrs_analyzer.analysis.ProjectAnalyzer;
import com.sebastian_daschner.jaxrs_analyzer.backend.Backend;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.Project;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.Resources;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.StreamSupport;

/**
 * Generates REST documentation of JAX-RS projects automatically by bytecode analysis.
 *
 * @author Sebastian Daschner
 */
public class JAXRSAnalyzer {

    private final Analysis analysis;

    /**
     * Constructs a JAX-RS Analyzer.
     */
    public JAXRSAnalyzer(Analysis analysis) {
        Objects.requireNonNull(analysis);
        Objects.requireNonNull(analysis.projectClassPaths);
        Objects.requireNonNull(analysis.projectSourcePaths);
        Objects.requireNonNull(analysis.classPaths);
        Objects.requireNonNull(analysis.projectName);
        Objects.requireNonNull(analysis.projectVersion);
        Objects.requireNonNull(analysis.backend);

        if (analysis.projectClassPaths.isEmpty())
            throw new IllegalArgumentException("At least one project path is mandatory");

        this.analysis = analysis;
    }

    /**
     * Analyzes the JAX-RS project at the class path and produces the output as configured.
     */
    public void analyze() {
        final Resources resources = new ProjectAnalyzer(analysis.classPaths)
                .analyze(analysis.projectClassPaths, analysis.projectSourcePaths, analysis.ignoredResources);

        if (resources.isEmpty()) {
            LogProvider.info("Empty JAX-RS analysis result, omitting output");
            return;
        }

        final Project project = new Project(analysis.projectName, analysis.projectVersion, analysis.projectOverview, resources);

        try {
            Writer output = null;
            if (analysis.outputLocation == null) {
                output = new PrintWriter(System.out);
            } else {
                output = new FileWriter(analysis.outputLocation.toFile());
            }
            try (Writer out = output) {
                analysis.backend.render(project, out);
            }
        } catch (Exception e) {
            LogProvider.error("Could not write to the specified output location, reason: " + e.getMessage());
            LogProvider.debug(e);
        }
    }

    public static Backend constructBackend(final String backendType) {
        final ServiceLoader<Backend> backends = ServiceLoader.load(Backend.class);
        return StreamSupport.stream(backends.spliterator(), false)
                .filter(b -> backendType.equalsIgnoreCase(b.getName()))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Unknown backend type " + backendType));
    }

    public static class Analysis {

        private final Set<Path> projectClassPaths = new HashSet<>();
        private final Set<Path> projectSourcePaths = new HashSet<>();
        private final Set<Path> classPaths = new HashSet<>();
        private final Set<String> ignoredResources = new HashSet<>();
        private String projectName;
        private String projectOverview;
        private String projectVersion;
        private Path outputLocation;
        private Backend backend;

        public Set<Path> getProjectClassPaths() {
            return projectClassPaths;
        }

        public void addProjectClassPath(Path classPath) {
            projectClassPaths.add(classPath);
        }

        public void addProjectSourcePath(Path sourcePath) {
            projectSourcePaths.add(sourcePath);
        }

        public void addClassPath(Path classPath) {
            classPaths.add(classPath);
        }

        public void addIgnoredResource(String ignored) {
            ignoredResources.add(ignored);
        }

        public void configureBackend(Map<String, String> attributes) {
            if (backend != null)
                backend.configure(attributes);
        }

        public void setProjectName(String projectName) {
            this.projectName = projectName;
        }

        public void setProjectOverview(String projectOverview) {
            this.projectOverview = projectOverview;
        }

        public void setProjectVersion(String projectVersion) {
            this.projectVersion = projectVersion;
        }

        public void setOutputLocation(Path outputLocation) {
            this.outputLocation = outputLocation;
        }

        public void setBackend(Backend backend) {
            this.backend = backend;
        }

        public Backend getBackend() {
            return backend;
        }
    }

}
