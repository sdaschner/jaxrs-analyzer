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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.function.Consumer;

/**
 * Exposes functionality to replace / retrieve an external logger.
 *
 * @author Sebastian Daschner
 */
public final class LogProvider {

    private static Consumer<String> infoLogger = System.err::println;
    private static Consumer<String> debugLogger = s -> {
        // do nothing
    };
    private static Consumer<String> errorLogger = System.err::println;

    private LogProvider() {
        throw new UnsupportedOperationException();
    }

    /**
     * Injects an own info logger functionality. Overwrites the previously associated info logger.
     *
     * @param logger The new info logger
     */
    public static void injectInfoLogger(final Consumer<String> logger) {
        LogProvider.infoLogger = logger;
    }

    /**
     * Injects an own debug logger functionality. Overwrites the previously associated debug logger.
     *
     * @param logger The new debug logger
     */
    public static void injectDebugLogger(final Consumer<String> logger) {
        LogProvider.debugLogger = logger;
    }

    /**
     * Injects an own error logger functionality. Overwrites the previously associated error logger.
     *
     * @param logger The new error logger
     */
    public static void injectErrorLogger(final Consumer<String> logger) {
        LogProvider.errorLogger = logger;
    }

    /**
     * Logs a message to the configured info logger.
     *
     * @param message The message to log
     */
    public static void info(final String message) {
        infoLogger.accept(message);
    }

    /**
     * Logs a message to the configured debug logger.
     *
     * @param message The message to log
     */
    public static void debug(final String message) {
        debugLogger.accept(message);
    }

    /**
     * Logs the stacktrace of the throwable to the debug logger.
     *
     * @param throwable The throwable to log
     */
    public static void debug(final Throwable throwable) {
        final StringWriter errors = new StringWriter();
        throwable.printStackTrace(new PrintWriter(errors));
        debugLogger.accept(errors.toString());
    }

    /**
     * Logs a message to the configured error logger.
     *
     * @param message The message to log
     */
    public static void error(final String message) {
        errorLogger.accept(message);
    }

}
