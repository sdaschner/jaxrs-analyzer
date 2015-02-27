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

package com.sebastian_daschner.jaxrs_analyzer.maven;

import com.sebastian_daschner.jaxrs_analyzer.LogProvider;
import com.sebastian_daschner.jaxrs_analyzer.analysis.ProjectAnalyzer;
import com.sebastian_daschner.jaxrs_analyzer.backend.Backend;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.Resources;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Maven goal which analyzes JAX-RS resources.
 *
 * @author Sebastian Daschner
 * @goal analyze-jaxrs
 * @phase process-classes
 */
public class JAXRSAnalyzerMojo extends AbstractMojo {

    /**
     * The chosen backend format. Defaults to plaintext.
     *
     * @parameter default-value="plaintext"
     * @required
     * @readonly
     */
    private String backend;

    /**
     * @parameter property="project.build.outputDirectory"
     * @required
     * @readonly
     */
    private File outputDirectory;

    /**
     * @parameter property="project.build.directory"
     * @required
     * @readonly
     */
    private File buildDirectory;

    private File resourcesDirectory;
    private Consumer<String> logger;

    @Override
    public void execute() throws MojoExecutionException {
        injectLoggers();

        // avoid execution if output directory does not exist
        if (!outputDirectory.exists() || !outputDirectory.isDirectory()) {
            logger.accept("skipping non existing directory " + outputDirectory);
            return;
        }

        final MavenBackend mavenBackend = MavenBackend.forName(backend);
        if (mavenBackend == null)
            throw new MojoExecutionException("Backend " + backend + " not valid! Valid values are: " +
                    Stream.of(MavenBackend.values()).map(Enum::name).map(String::toLowerCase).collect(Collectors.joining(", ")));

        logger.accept("analyzing JAX-RS resources, using " + mavenBackend.getName());

        final ProjectAnalyzer projectAnalyzer = new ProjectAnalyzer();
        final Resources resources = projectAnalyzer.analyze(outputDirectory.toPath());

        if (!isEmpty(resources)) {

            resourcesDirectory = Paths.get(buildDirectory.getPath(), "jaxrs-analyzer").toFile();
            if (!resourcesDirectory.exists() && !resourcesDirectory.mkdirs())
                throw new MojoExecutionException("Could not create directory " + resourcesDirectory);

            writeOutput(mavenBackend, resources);
        }
    }

    private void injectLoggers() {
        LogProvider.injectLogger(getLog()::info);
        logger = LogProvider.getLogger();
    }

    private boolean isEmpty(final Resources resources) {
        return resources.getResources().isEmpty() || resources.getResources().stream().map(resources::getMethods).noneMatch(s -> !s.isEmpty());
    }

    private void writeOutput(final MavenBackend mavenBackend, final Resources resources) throws MojoExecutionException {
        final File touch = new File(resourcesDirectory, mavenBackend.getFileName());
        try (final FileWriter writer = new FileWriter(touch)) {
            final Backend backend = mavenBackend.instantiateBackend();
            writer.write(backend.render(resources));
        } catch (IOException e) {
            throw new MojoExecutionException("Could not create file " + touch, e);
        }
    }

}
