package com.sebastian_daschner.jaxrs_analyzer.backend;

import java.util.Comparator;
import java.util.Map;

/**
 * Common used functionality for creating comparators.
 *
 * @author Sebastian Daschner
 */
public final class ComparatorUtils {

    private ComparatorUtils() {
        throw new UnsupportedOperationException();
    }

    public static <T extends Comparable<? super T>> Comparator<Map.Entry<T, ?>> mapKeyComparator() {
        return Comparator.comparing(Map.Entry::getKey);
    }

}
