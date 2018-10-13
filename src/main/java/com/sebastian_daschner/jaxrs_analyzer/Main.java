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
import com.sebastian_daschner.jaxrs_analyzer.backend.StringBackend;
import com.sebastian_daschner.jaxrs_analyzer.backend.swagger.SwaggerOptions;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private static final String DEFAULT_BACKEND = "swagger";
    private static final String COMMA_LIST_SEPARATOR = ",";

    private static final JAXRSAnalyzer.Analysis analysis = new JAXRSAnalyzer.Analysis();
    private static final Map<String, String> attributes = new HashMap<>();

    /**
     * Inspects JAX-RS projects and outputs the gathered information.
     * <p>
     * Argument usage: {@code [options] projectPath [projectPaths...]}
     * <p>
     * The {@code projectPath} entries may be directories or jar-files containing the classes to be analyzed
     * <p>
     * Following available options:
     * <ul>
     * <li>{@code -b backend} The backend to choose: {@code swagger} (default), {@code plaintext}, {@code asciidoc}, {@code markdown}</li>
     * <li>{@code -cp class path[:class paths...]} The additional class paths which contain classes which are used in the project</li>
     * <li>{@code -sp source path[:source paths...]} The optional source paths  needed for JavaDoc analysis</li>
     * <li>{@code -X} Debug enabled (prints error debugging information on Standard error out)</li>
     * <li>{@code -n project name} The name of the project</li>
     * <li>{@code -v project version} The version of the project</li>
     * <li>{@code -d project domain} The domain of the project</li>
     * <li>{@code -o output file} The location of the analysis output (will be printed to standard out if omitted)</li>
     * <li>{@code -e encoding} The source file encoding</li>
     * </ul>
     * <p>
     * Following available backend specific options (only have effect if the corresponding backend is selected):
     * <ul>
     * <li>{@code --swaggerSchemes scheme[,schemes]} The Swagger schemes: {@code http} (default), {@code https}, {@code ws}, {@code wss}")</li>
     * <li>{@code --renderSwaggerTags} Enables rendering of Swagger tags (will not be rendered per default)</li>
     * <li>{@code --swaggerTagsPathOffset path offset} The number at which path position the Swagger tags should be extracted ({@code 0} per default)</li>
     * <li>{@code --ignoredRootResources class[,classes]} JAX-RS root resource classes which should be ignored by analyze (empty per default)</li>
     * </ul>
     *
     * @param args The arguments
     */
    public static void main(final String... args) {
        if (args.length < 1) {
            printUsageAndExit();
        }

        try {
            setDefaults();
            extractArgs(args);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage() + '\n');
            printUsageAndExit();
        }

        validateArgs();
        configureBackend();

        new JAXRSAnalyzer(analysis).analyze();
    }

    private static void setDefaults() {
        analysis.setProjectName(DEFAULT_NAME);
        analysis.setProjectVersion(DEFAULT_VERSION);
    }

    private static void extractArgs(String[] args) {
        try {
            for (int i = 0; i < args.length; i++) {
                if (args[i].startsWith("-")) {
                    switch (args[i]) {
                        case "-b":
                            analysis.setBackend(extractBackend(args[++i]));
                            break;
                        case "-cp":
                            extractClassPaths(args[++i]).forEach(analysis::addClassPath);
                            break;
                        case "-sp":
                            extractClassPaths(args[++i]).forEach(analysis::addProjectSourcePath);
                            break;
                        case "-X":
                            LogProvider.injectDebugLogger(System.err::println);
                            break;
                        case "-n":
                            analysis.setProjectName(args[++i]);
                            break;
                        case "-v":
                            analysis.setProjectVersion(args[++i]);
                            break;
                        case "-d":
                            attributes.put(SwaggerOptions.DOMAIN, args[++i]);
                            break;
                        case "-o":
                            analysis.setOutputLocation(Paths.get(args[++i]));
                            break;
                        case "-e":
                            System.setProperty("project.build.sourceEncoding", args[++i]);
                            break;
                        case "--swaggerSchemes":
                            attributes.put(SwaggerOptions.SWAGGER_SCHEMES, args[++i]);
                            break;
                        case "--renderSwaggerTags":
                            attributes.put(SwaggerOptions.RENDER_SWAGGER_TAGS, "true");
                            break;
                        case "--swaggerTagsPathOffset":
                            attributes.put(SwaggerOptions.SWAGGER_TAGS_PATH_OFFSET, args[++i]);
                            break;
                        case "--noInlinePrettify":
                            attributes.put(StringBackend.INLINE_PRETTIFY, "false");
                            break;
                        case "--ignoredRootResources":
                            extractList(args[++i]).forEach(analysis::addIgnoredResource);
                            break;
                        case "-a":
                            addAttribute(args[++i]);
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
                    analysis.addProjectClassPath(path);
                }
            }
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Please provide valid number of arguments");
        }
    }

    static Map<String, String> addAttribute(String attribute) {
        int separatorIndex = attribute.indexOf('=');

        if (separatorIndex < 0) {
            attributes.put(attribute, "");
        } else {
            attributes.put(attribute.substring(0, separatorIndex).trim(), attribute.substring(separatorIndex + 1).trim());
        }

        return attributes;
    }

    private static Backend extractBackend(final String name) {
        return JAXRSAnalyzer.constructBackend(name.toLowerCase());
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

    private static List<String> extractList(String list) {
        return Stream.of(list.split(COMMA_LIST_SEPARATOR))
                .map(String::trim)
                .filter(item -> !item.isEmpty())
                .collect(Collectors.toList());
    }

    private static void validateArgs() {
        if (analysis.getProjectClassPaths().isEmpty()) {
            System.err.println("Please provide at least one project path\n");
            printUsageAndExit();
        }
    }

    private static void configureBackend() {
        if (analysis.getBackend() == null)
            analysis.setBackend(JAXRSAnalyzer.constructBackend(DEFAULT_BACKEND));
        analysis.configureBackend(attributes);
    }

    private static void printUsageAndExit() {
        System.err.println("Usage: java -jar jaxrs-analyzer.jar [options] classPath [classPaths...]");
        System.err.println("The classPath entries may be directories or jar-files containing the classes to be analyzed\n");
        System.err.println("Following available options:\n");
        System.err.println(" -b <backend> The backend to choose: swagger (default), plaintext, asciidoc, markdown");
        System.err.println(" -cp <class path>[:class paths] Additional class paths (separated with colon) which contain classes used in the project (may be directories or jar-files)");
        System.err.println(" -sp <source path>[:source paths] Optional source paths (separated with colon) needed for JavaDoc analysis (may be directories or jar-files)");
        System.err.println(" -X Debug enabled (enabled error debugging information)");
        System.err.println(" -n <project name> The name of the project");
        System.err.println(" -v <project version> The version of the project");
        System.err.println(" -d <project domain> The domain of the project");
        System.err.println(" -o <output file> The location of the analysis output (will be printed to standard out if omitted)");
        System.err.println(" -a <attribute name>=<attribute value> Set custom attributes for backends.");
        System.err.println(" -e <encoding> The source file encoding");
        System.err.println("\nFollowing available backend specific options (only have effect if the corresponding backend is selected):\n");
        System.err.println(" --swaggerSchemes <scheme>[,schemes] The Swagger schemes: http (default), https, ws, wss");
        System.err.println(" --renderSwaggerTags Enables rendering of Swagger tags (default tag will be used per default)");
        System.err.println(" --swaggerTagsPathOffset <path offset> The number at which path position the Swagger tags will be extracted (0 will be used per default)");
        System.err.println(" --ignoredRootResources <fully qualified classname [class,...]> JAX-RS root resource classes which should be ignored by analyze (empty per default)");
        System.err.println(" --noPrettyPrint Don't pretty print inline JSON body representations (will be pretty printed per default)");
        System.err.println("\nExample: java -jar jaxrs-analyzer.jar -b swagger -n \"My Project\" -cp ~/libs/lib1.jar:~/libs/project/bin ~/project/target/classes");
        System.exit(1);
    }

}
