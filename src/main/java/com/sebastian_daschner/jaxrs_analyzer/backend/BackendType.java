package com.sebastian_daschner.jaxrs_analyzer.backend;

import com.sebastian_daschner.jaxrs_analyzer.backend.asciidoc.AsciiDocBackend;
import com.sebastian_daschner.jaxrs_analyzer.backend.plaintext.PlainTextBackend;
import com.sebastian_daschner.jaxrs_analyzer.backend.swagger.SwaggerBackend;

import java.util.function.Supplier;

/**
 * The available backend types.
 *
 * @author Sebastian Daschner
 */
public enum BackendType {

    PLAINTEXT("Plain text", PlainTextBackend::new),

    ASCIIDOC("AsciiDoc", AsciiDocBackend::new),

    SWAGGER("Swagger", SwaggerBackend::new);

    private final String name;
    private final Supplier<Backend> backendSupplier;

    BackendType(final String name, final Supplier<Backend> backendSupplier) {
        this.name = name;
        this.backendSupplier = backendSupplier;
    }

    public String getName() {
        return name;
    }

    public Supplier<Backend> getBackendSupplier() {
        return backendSupplier;
    }

    public static BackendType getByNameIgnoreCase(final String name) {
        for (final BackendType backend : values()) {
            if (backend.name().equalsIgnoreCase(name)) {
                return backend;
            }
        }
        return null;
    }

}
