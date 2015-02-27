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
