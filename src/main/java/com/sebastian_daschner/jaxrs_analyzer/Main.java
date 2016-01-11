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

import com.sebastian_daschner.jaxrs_analyzer.backend.BackendType;

import java.nio.file.Path;
import java.nio.file.Paths;
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
     * <li>{@code -X} Debug enabled (prints error debugging information on Standard error out)</li>
     * <li>{@code -n project name} The name of the project</li>
     * <li>{@code -v project version} The version of the project</li>
     * <li>{@code -d project domain} The domain of the project</li>
     * <li>{@code -o output file} The location of the analysis output (will be printed to standard out if omitted)</li>
     * <li>{@code -renderSwaggerTags} Enables rendering of Swagger tags (has no effect on other backends</li>
     * <li>{@code -swaggerTagsPathOffset path offset} The number at which path position the Swagger tags should be extracted</li>
     * </ul>
     *
     * @param args The arguments
     */
    public static void main(final String... args) {
        if (args.length < 1) {
            printUsageAndExit();
        }

        final Set<Path> projectPaths = new HashSet<>();
        final Set<Path> classPaths = new HashSet<>();
        BackendType backendType = null;
        String name = null;
        String version = null;
        String domain = null;
        Path outputFileLocation = null;
        Boolean renderSwaggerTags = null;
        Integer swaggerTagsPathOffset = null;

        try {
            for (int i = 0; i < args.length; i++) {
                if (args[i].startsWith("-")) {
                    switch (args[i]) {
                        case "-b":
                            backendType = BackendType.getByNameIgnoreCase(args[++i]);
                            if (backendType == null) {
                                System.err.println("Unknown backend " + args[i] + '\n');
                                printUsageAndExit();
                            }
                            break;
                        case "-cp":
                            final List<Path> paths = Stream.of(args[++i].split(":"))
                                    .map(s -> s.replaceFirst("^~", System.getProperty("user.home")))
                                    .map(Paths::get).collect(Collectors.toList());
                            paths.forEach(p -> {
                                if (!p.toFile().exists()) {
                                    System.err.println("Class path " + p.toFile() + " doesn't exist\n");
                                    printUsageAndExit();
                                }
                            });

                            paths.forEach(classPaths::add);
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
                        case "-renderSwaggerTags":
                            renderSwaggerTags = true;
                            break;
                        case "-swaggerTagsPathOffset":
                            swaggerTagsPathOffset = Integer.valueOf(args[++i]);
                            break;
                        default:
                            System.err.print("Unknown option " + args[i] + '\n');
                            printUsageAndExit();
                    }
                } else {
                    final Path path = Paths.get(args[i].replaceFirst("^~", System.getProperty("user.home")));
                    if (!path.toFile().exists()) {
                        System.err.println("Location " + path.toFile() + " doesn't exist\n");
                        printUsageAndExit();
                    }
                    projectPaths.add(path);
                }
            }
        } catch (IndexOutOfBoundsException e) {
            System.err.println("Please provide valid number of arguments\n");
            printUsageAndExit();
        } catch(NumberFormatException nfe) {
            System.err.println("Please provide valid integer number as an argument\n");
            printUsageAndExit();
        }

        if( swaggerTagsPathOffset != null && swaggerTagsPathOffset < 0 ) {
            System.err.println("Please provide positive integer number for option -swaggerTagsPathOffset\n");
            printUsageAndExit();
        }

        if (projectPaths.isEmpty()) {
            System.err.println("Please provide at least one project path\n");
            printUsageAndExit();
        }

        final JAXRSAnalyzer jaxrsAnalyzer = new JAXRSAnalyzer(projectPaths, classPaths, backendType, name, version, domain, outputFileLocation,
            renderSwaggerTags, swaggerTagsPathOffset);
        jaxrsAnalyzer.analyze();
    }

    private static void printUsageAndExit() {
        System.err.println("Usage: java -jar jaxrs-analyzer.jar [options] classPath [classPaths...]");
        System.err.println("The classPath entries may be directories or jar-files containing the classes to be analyzed\n");
        System.err.println("Following available options:\n");
        System.err.println(" -b <backend> The backend to choose: swagger (default), plaintext, asciidoc");
        System.err.println(" -cp <class path>[:class paths] Additional class paths (separated with colon) which contain classes used in the project (may be directories or jar-files)");
        System.err.println(" -X Debug enabled (enabled error debugging information)");
        System.err.println(" -n <project name> The name of the project");
        System.err.println(" -v <project version> The version of the project");
        System.err.println(" -d <project domain> The domain of the project");
        System.err.println(" -o <output file> The location of the analysis output (will be printed to standard out if omitted)");
        System.err.println(" -renderSwaggerTags Enables rendering of Swagger tags (has no effect on other backends)");
        System.err.println(" -swaggerTagsPathOffset <path offset> The number at which path position the Swagger tags should be extracted");
        System.err.println("\nExample: java -jar jaxrs-analyzer.jar -b swagger -n \"My Project\" -cp ~/libs/lib1.jar:~/libs/project/bin ~/project/target/classes");
        System.exit(1);
    }

}
