package com.sebastian_daschner.jaxrs_analyzer.maven;

import com.sebastian_daschner.jaxrs_analyzer.backend.Backend;
import com.sebastian_daschner.jaxrs_analyzer.backend.plaintext.PlainTextBackend;
import com.sebastian_daschner.jaxrs_analyzer.backend.swagger.SwaggerBackend;

import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * @author Sebastian Daschner
 */
public enum MavenBackend {

    PLAINTEXT("Plain text backend", "rest-resources.txt", PlainTextBackend::new),

    SWAGGER("Swagger backend", "swagger.json", SwaggerBackend::new);

    private String name;
    private String fileName;
    private Supplier<Backend> backendSupplier;

    MavenBackend(final String name, final String fileName, final Supplier<Backend> backendSupplier) {
        this.name = name;
        this.fileName = fileName;
        this.backendSupplier = backendSupplier;
    }

    public String getFileName() {
        return fileName;
    }

    public Backend instantiateBackend() {
        return backendSupplier.get();
    }

    public String getName() {
        return name;
    }

    public static MavenBackend forName(final String backendName) {
        return Stream.of(values()).filter(e -> e.name().toLowerCase().equals(backendName)).findAny().orElse(null);
    }

}
