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

package com.sebastian_daschner.jaxrs_analyzer.utils;

/**
 * Contains utility functions for Strings.
 *
 * @author Sebastian Daschner
 */
public final class StringUtils {

    private StringUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * Checks if the given String is not {@code null}, empty or consists solely of whitespaces.
     *
     * @param arg The String to check
     * @throws java.lang.IllegalArgumentException If the String is invalid.
     */
    public static void requireNonBlank(final String arg) {
        if (isBlank(arg))
            throw new IllegalArgumentException("String argument is blank.");
    }

    /**
     * Checks if the given String is {@code null}, empty or consists solely of whitespaces.
     *
     * @param string The String to check
     * @return {@code true} if the String is {@code null}, empty or whitespace
     */
    public static boolean isBlank(final String string) {
        if (string == null || string.isEmpty())
            return true;

        return string.chars().allMatch(Character::isWhitespace);
    }

}
