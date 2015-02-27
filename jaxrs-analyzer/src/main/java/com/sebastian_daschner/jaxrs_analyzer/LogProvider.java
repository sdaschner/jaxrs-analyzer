package com.sebastian_daschner.jaxrs_analyzer;

import java.util.function.Consumer;

/**
 * Exposes functionality to replace / retrieve an external logger.
 *
 * @author Sebastian Daschner
 */
public final class LogProvider {

    private static Consumer<String> logger = System.err::println;

    /**
     * Injects an own logger functionality. Overwrites the previously associated logger.
     *
     * @param logger The new logger
     */
    public static void injectLogger(final Consumer<String> logger) {
        LogProvider.logger = logger;
    }

    /**
     * Returns the associated logger. Defaults to {@code System.err.println()}.
     *
     * @return The logger
     */
    public static Consumer<String> getLogger() {
        return logger;
    }

}
