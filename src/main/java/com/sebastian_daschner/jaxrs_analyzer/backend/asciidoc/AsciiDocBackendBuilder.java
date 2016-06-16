package com.sebastian_daschner.jaxrs_analyzer.backend.asciidoc;

import com.sebastian_daschner.jaxrs_analyzer.backend.Backend;

/**
 * AsciiDoc backend builder.
 *
 * @author Sebastian Daschner
 */
public class AsciiDocBackendBuilder implements Backend.BackendBuilder {

    @Override
    public Backend build() {
        return new AsciiDocBackend();
    }

}
