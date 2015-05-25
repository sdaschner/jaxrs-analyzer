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

package com.sebastian_daschner.jaxrs_analyzer;

import com.sebastian_daschner.jaxrs_analyzer.analysis.ProjectAnalyzer;
import com.sebastian_daschner.jaxrs_analyzer.backend.Backend;
import com.sebastian_daschner.jaxrs_analyzer.backend.asciidoc.AsciiDocBackend;
import com.sebastian_daschner.jaxrs_analyzer.backend.plaintext.PlainTextBackend;
import com.sebastian_daschner.jaxrs_analyzer.backend.swagger.SwaggerBackend;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.Project;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.Resources;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Inspects the information of JAX-RS classes via bytecode analysis.
 *
 * @author Sebastian Daschner
 */
public class Main {

    private static final String DEFAULT_NAME = "project";
    private static final String DEFAULT_VERSION = "0.1-SNAPSHOT";
    private static final String DEFAULT_DOMAIN = "example.com";

    /**
     * Inspects the JAX-RS test project and prints the gathered information as swagger JSON.
     *
     * @param args The arguments
     *             0: Path of the analyzed project
     *             1: Backend (swagger (default), plaintext)
     *             2: Name of the project
     *             3: Version of the project
     *             4: Domain of the deployed project
     */
    public static void main(final String... args) {
        if (args.length < 1) {
            System.err.println("Usage: java -jar rest-documentation-analyzer.jar <projectPath> [<backend>] [<project name>] [<project version>] [<project domain>]");
            System.err.println("Backends: swagger (default), plaintext");
            System.exit(1);
        }

        final Path projectLocation = Paths.get(args[0]);

        if (!projectLocation.toFile().exists() || !projectLocation.toFile().isDirectory()) {
            System.err.println("Please provide a valid directory!");
            System.exit(1);
        }

        final String name = args.length >= 3 ? args[2] : DEFAULT_NAME;
        final String version = args.length >= 4 ? args[3] : DEFAULT_VERSION;
        final String domain = args.length >= 5 ? args[4] : DEFAULT_DOMAIN;

        final Resources resources = new ProjectAnalyzer(projectLocation).analyze();
        final Project project = new Project(name, version, domain, resources);

        final String output = chooseBackend(args.length >= 2 ? args[1] : null).render(project);
        System.out.println(output);
    }

    private static Backend chooseBackend(final String backendName) {
        switch (backendName) {
            case "plaintext":
                return new PlainTextBackend();
            case "asciidoc":
                return new AsciiDocBackend();
            case "swagger":
            default:
                return new SwaggerBackend();
        }
    }

}
