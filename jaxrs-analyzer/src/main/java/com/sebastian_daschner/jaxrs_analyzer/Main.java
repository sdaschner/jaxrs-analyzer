package com.sebastian_daschner.jaxrs_analyzer;

import com.sebastian_daschner.jaxrs_analyzer.backend.Backend;
import com.sebastian_daschner.jaxrs_analyzer.backend.plaintext.PlainTextBackend;
import com.sebastian_daschner.jaxrs_analyzer.backend.swagger.SwaggerBackend;
import com.sebastian_daschner.jaxrs_analyzer.analysis.ProjectAnalyzer;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.Resources;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Inspects the information of JAX-RS classes via bytecode analysis.
 *
 * @author Sebastian Daschner
 */
public class Main {

    /**
     * Inspects the JAX-RS test project and prints the gathered information as swagger JSON.
     *
     * @param args The arguments
     *             0: Path of the analyzed project
     *             1: Backend (swagger (default), plaintext)
     */
    public static void main(final String... args) {
        if (args.length < 1) {
            System.err.println("Usage: java -jar rest-documentation-analyzer.jar <projectPath> [<backend>]");
            System.err.println("Backends: swagger (default), plaintext");
            System.exit(1);
        }

        final Path projectLocation = Paths.get(args[0]);

        if (!projectLocation.toFile().exists() || !projectLocation.toFile().isDirectory()) {
            System.err.println("Please provide a valid directory!");
            System.exit(1);
        }

        final Resources resources = new ProjectAnalyzer().analyze(projectLocation);

        final Backend backend = args.length >= 2 && "plaintext".equals(args[1]) ? new PlainTextBackend() : new SwaggerBackend();

        final String output = backend.render(resources);
        System.out.println(output);
    }

}
