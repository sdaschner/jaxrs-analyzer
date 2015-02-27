package com.sebastian_daschner.jaxrs_analyzer.analysis.utils;

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
