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

import com.sebastian_daschner.jaxrs_analyzer.backend.Backend;
import com.sebastian_daschner.jaxrs_analyzer.backend.swagger.SwaggerBackendBuilder;
import com.sebastian_daschner.jaxrs_analyzer.backend.swagger.SwaggerScheme;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Inspects the information of JAX-RS classes via bytecode analysis.
 *
 * @author Sebastian Daschner
 */
public class Main {

    private static final String DEFAULT_NAME = "project";
    private static final String DEFAULT_VERSION = "0.1-SNAPSHOT";

    private static final Set<Path> projectClassPaths = new HashSet<>();
    private static final Set<Path> projectSourcePaths = new HashSet<>();
    private static final Set<Path> classPaths = new HashSet<>();
    private static String name = DEFAULT_NAME;
    private static String version = DEFAULT_VERSION;
    private static BackendType backendType = BackendType.SWAGGER;
    private static String domain;
    private static Set<SwaggerScheme> swaggerSchemes;
    private static Boolean renderSwaggerTags;
    private static Integer swaggerTagsPathOffset;
    private static Path outputFileLocation;

    /**
     * Inspects JAX-RS projects and outputs the gathered information.
     * <p>
     * Argument usage: {@code [options] projectPath [projectPaths...]}
     * <p>
     * The {@code projectPath} entries may be directories or jar-files containing the classes to be analyzed
     * <p>
     * Following available options:
     * <ul>
     * <li>{@code -b backend} The backend to choose: {@code swagger} (default), {@code plaintext}, {@code asciidoc}</li>
     * <li>{@code -cp class path[:class paths...]} The additional class paths which contain classes which are used in the project</li>
     * <li>{@code -sp source path[:source paths...]} The optional source paths  needed for JavaDoc analysis</li>
     * <li>{@code -X} Debug enabled (prints error debugging information on Standard error out)</li>
     * <li>{@code -n project name} The name of the project</li>
     * <li>{@code -v project version} The version of the project</li>
     * <li>{@code -d project domain} The domain of the project</li>
     * <li>{@code -o output file} The location of the analysis output (will be printed to standard out if omitted)</li>
     * </ul>
     * <p>
     * Following available backend specific options (only have effect if the corresponding backend is selected):
     * <ul>
     * <li>{@code --swaggerSchemes scheme[,schemes]} The Swagger schemes: {@code http} (default), {@code https}, {@code ws}, {@code wss}")</li>
     * <li>{@code --renderSwaggerTags} Enables rendering of Swagger tags (will not be rendered per default)</li>
     * <li>{@code --swaggerTagsPathOffset path offset} The number at which path position the Swagger tags should be extracted ({@code 0} per default)</li>
     * </ul>
     *
     * @param args The arguments
     */
    public static void main(final String... args) {
        if (args.length < 1) {
            printUsageAndExit();
        }

        try {
            extractArgs(args);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage() + '\n');
            printUsageAndExit();
        }

        validateArgs();

        final Backend backend = constructBackend();

        final JAXRSAnalyzer jaxrsAnalyzer = new JAXRSAnalyzer(projectClassPaths, projectSourcePaths, classPaths, name, version, backend, outputFileLocation);
        jaxrsAnalyzer.analyze();
    }

    private static void extractArgs(String[] args) {
        try {
            for (int i = 0; i < args.length; i++) {
                if (args[i].startsWith("-")) {
                    switch (args[i]) {
                        case "-b":
                            backendType = extractBackend(args[++i]);
                            break;
                        case "-cp":
                            extractClassPaths(args[++i]).forEach(classPaths::add);
                            break;
                        case "-sp":
                            extractClassPaths(args[++i]).forEach(projectSourcePaths::add);
                            break;
                        case "-X":
                            LogProvider.injectDebugLogger(System.err::println);
                            break;
                        case "-n":
                            name = args[++i];
                            break;
                        case "-v":
                            version = args[++i];
                            break;
                        case "-d":
                            domain = args[++i];
                            break;
                        case "-o":
                            outputFileLocation = Paths.get(args[++i]);
                            break;
                        case "--swaggerSchemes":
                            swaggerSchemes = extractSwaggerSchemes(args[++i]);
                            break;
                        case "--renderSwaggerTags":
                            renderSwaggerTags = true;
                            break;
                        case "--swaggerTagsPathOffset":
                            swaggerTagsPathOffset = Integer.valueOf(args[++i]);
                            break;
                        default:
                            throw new IllegalArgumentException("Unknown option " + args[i]);
                    }
                } else {
                    final Path path = Paths.get(args[i].replaceFirst("^~", System.getProperty("user.home")));
                    if (!path.toFile().exists()) {
                        System.err.println("Location " + path.toFile() + " doesn't exist\n");
                        printUsageAndExit();
                    }
                    projectClassPaths.add(path);
                }
            }
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Please provide valid number of arguments");
        }
    }

    private static BackendType extractBackend(final String name) {
        switch (name.toLowerCase()) {
            case "swagger":
                return BackendType.SWAGGER;
            case "plaintext":
                return BackendType.PLAINTEXT;
            case "asciidoc":
                return BackendType.ASCIIDOC;
            default:
                throw new IllegalArgumentException("Unknown backend " + name);
        }
    }

    private static List<Path> extractClassPaths(final String classPaths) {
        final List<Path> paths = Stream.of(classPaths.split(File.pathSeparator))
                .map(s -> s.replaceFirst("^~", System.getProperty("user.home")))
                .map(Paths::get).collect(Collectors.toList());
        paths.forEach(p -> {
            if (!p.toFile().exists()) {
                throw new IllegalArgumentException("Class path " + p.toFile() + " doesn't exist");
            }
        });
        return paths;
    }

    private static Set<SwaggerScheme> extractSwaggerSchemes(final String schemes) {
        return Stream.of(schemes.split(","))
                .map(Main::extractSwaggerScheme)
                .collect(() -> EnumSet.noneOf(SwaggerScheme.class), Set::add, Set::addAll);
    }

    private static SwaggerScheme extractSwaggerScheme(final String scheme) {
        switch (scheme.toLowerCase()) {
            case "http":
                return SwaggerScheme.HTTP;
            case "https":
                return SwaggerScheme.HTTPS;
            case "ws":
                return SwaggerScheme.WS;
            case "wss":
                return SwaggerScheme.WSS;
            default:
                throw new IllegalArgumentException("Unknown swagger scheme " + scheme);
        }
    }

    private static void validateArgs() {
        if (swaggerTagsPathOffset != null && swaggerTagsPathOffset < 0) {
            System.err.println("Please provide positive integer number for option --swaggerTagsPathOffset\n");
            printUsageAndExit();
        }

        if (projectClassPaths.isEmpty()) {
            System.err.println("Please provide at least one project path\n");
            printUsageAndExit();
        }
    }

    private static Backend constructBackend() {
        switch (backendType) {
            case SWAGGER:
                return configureSwaggerBackend();
            case PLAINTEXT:
                return Backend.plainText().build();
            case ASCIIDOC:
                return Backend.asciiDoc().build();
            default:
                // can not happen
                throw new IllegalArgumentException("Unknown backend type " + backendType);
        }
    }

    private static Backend configureSwaggerBackend() {
        final SwaggerBackendBuilder builder = Backend.swagger();

        if (domain != null)
            builder.domain(domain);
        if (swaggerSchemes != null)
            builder.schemes(swaggerSchemes);
        if (renderSwaggerTags != null) {
            if (swaggerTagsPathOffset != null)
                builder.renderTags(renderSwaggerTags, swaggerTagsPathOffset);
            else
                builder.renderTags(renderSwaggerTags);
        }

        return builder.build();
    }

    private static void printUsageAndExit() {
        System.err.println("Usage: java -jar jaxrs-analyzer.jar [options] classPath [classPaths...]");
        System.err.println("The classPath entries may be directories or jar-files containing the classes to be analyzed\n");
        System.err.println("Following available options:\n");
        System.err.println(" -b <backend> The backend to choose: swagger (default), plaintext, asciidoc");
        System.err.println(" -cp <class path>[:class paths] Additional class paths (separated with colon) which contain classes used in the project (may be directories or jar-files)");
        System.err.println(" -sp <source path>[:source paths] Optional source paths (separated with colon) needed for JavaDoc analysis (may be directories or jar-files)");
        System.err.println(" -X Debug enabled (enabled error debugging information)");
        System.err.println(" -n <project name> The name of the project");
        System.err.println(" -v <project version> The version of the project");
        System.err.println(" -d <project domain> The domain of the project");
        System.err.println(" -o <output file> The location of the analysis output (will be printed to standard out if omitted)");
        System.err.println("\nFollowing available backend specific options (only have effect if the corresponding backend is selected):\n");
        System.err.println(" --swaggerSchemes <scheme>[,schemes] The Swagger schemes: http (default), https, ws, wss");
        System.err.println(" --renderSwaggerTags Enables rendering of Swagger tags (default tag will be used per default)");
        System.err.println(" --swaggerTagsPathOffset <path offset> The number at which path position the Swagger tags will be extracted (0 will be used per default)");
        System.err.println("\nExample: java -jar jaxrs-analyzer.jar -b swagger -n \"My Project\" -cp ~/libs/lib1.jar:~/libs/project/bin ~/project/target/classes");
        System.exit(1);
    }

    private enum BackendType {
        SWAGGER, PLAINTEXT, ASCIIDOC
    }
}
