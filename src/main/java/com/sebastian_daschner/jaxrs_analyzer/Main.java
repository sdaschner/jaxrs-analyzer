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
import java.util.Set;

/**
 * Inspects the information of JAX-RS classes via bytecode analysis.
 *
 * @author Sebastian Daschner
 */
public class Main {

    /**
     * Inspects JAX-RS projects and outputs the gathered information.
     * <p>
     * Argument usage: {@code [options] classPath [classPaths...]}
     * <p>
     * The {@code classPath} entries may be directories or jar-files containing the classes to be analyzed
     * <p>
     * Following available options:
     * <ul>
     * <li>{@code -b <backend>} The backend to choose: {@code swagger} (default), {@code plaintext}, {@code asciidoc}</li>
     * <li>{@code -n <project name>} The name of the project</li>
     * <li>{@code -v <project version>} The version of the project</li>
     * <li>{@code -d <project domain>} The domain of the project</li>
     * <li>{@code -o <output file>} The location of the analysis output (will be printed to standard out if omitted)</li>
     * </ul>
     *
     * @param args The arguments
     */
    public static void main(final String... args) {
        if (args.length < 1) {
            printUsageAndExit();
        }

        final Set<Path> classPaths = new HashSet<>();
        BackendType backendType = null;
        String name = null;
        String version = null;
        String domain = null;
        Path outputFileLocation = null;

        try {
            for (int i = 0; i < args.length; i++) {
                if (args[i].startsWith("-")) {
                    switch (args[i]) {
                        case "-b":
                            backendType = BackendType.getByNameIgnoreCase(args[++i]);
                            if (backendType == null) {
                                System.err.println("Unknown backend " + args[i - 1] + '\n');
                                printUsageAndExit();
                            }
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
                        default:
                            System.err.print("Unknown option " + args[i] + '\n');
                            printUsageAndExit();
                    }
                } else {
                    final Path path = Paths.get(args[i]);
                    if (!path.toFile().exists()) {
                        System.err.println("Location " + path + " doesn't exist\n");
                        printUsageAndExit();
                    }
                    classPaths.add(path);
                }
            }
        } catch (IndexOutOfBoundsException e) {
            System.err.println("Please provide valid number of arguments\n");
            printUsageAndExit();
        }

        if (classPaths.isEmpty()) {
            System.err.println("Please provide at least one classPath\n");
            printUsageAndExit();
        }

        final JAXRSAnalyzer jaxrsAnalyzer = new JAXRSAnalyzer(classPaths, backendType, name, version, domain, outputFileLocation);
        jaxrsAnalyzer.analyze();
    }

    private static void printUsageAndExit() {
        System.err.println("Usage: java -jar jaxrs-analyzer.jar [options] classPath [classPaths...]");
        System.err.println("The classPath entries may be directories or jar-files containing the classes to be analyzed\n");
        System.err.println("Following available options:\n");
        System.err.println(" -b <backend> The backend to choose: swagger (default), plaintext, asciidoc");
        System.err.println(" -n <project name> The name of the project");
        System.err.println(" -v <project version> The version of the project");
        System.err.println(" -d <project domain> The domain of the project");
        System.err.println(" -o <output file> The location of the analysis output (will be printed to standard out if omitted)");
        System.err.println("\nExample: java -jar jaxrs-analyzer.jar -b swagger -n \"My Project\" ~/project/target/classes");
        System.exit(1);
    }

}
