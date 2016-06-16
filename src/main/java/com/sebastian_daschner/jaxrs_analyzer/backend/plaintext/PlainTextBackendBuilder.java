package com.sebastian_daschner.jaxrs_analyzer.backend.plaintext;

import com.sebastian_daschner.jaxrs_analyzer.backend.Backend;

/**
 * Plain text backend builder.
 *
 * @author Sebastian Daschner
 */
public class PlainTextBackendBuilder implements Backend.BackendBuilder {

    @Override
    public Backend build() {
        return new PlainTextBackend();
    }

}
