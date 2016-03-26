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
 * Represents a tuple of two elements.
 *
 * @param <T> The type of the left element
 * @param <U> The type of the right element
 * @author Sebastian Daschner
 */
public class Pair<T, U> {

    private final T left;
    private final U right;

    private Pair(final T left, final U right) {
        this.left = left;
        this.right = right;
    }

    public T getLeft() {
        return left;
    }

    public U getRight() {
        return right;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Pair pair = (Pair) o;

        if (left != null ? !left.equals(pair.left) : pair.left != null) return false;
        return !(right != null ? !right.equals(pair.right) : pair.right != null);
    }

    @Override
    public int hashCode() {
        int result = left != null ? left.hashCode() : 0;
        result = 31 * result + (right != null ? right.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Pair{" +
                "left=" + left +
                ", right=" + right +
                '}';
    }

    /**
     * Creates a new pair with left and right value.
     *
     * @param left  The left value (can be {@code null})
     * @param right The right value (can be {@code null})
     * @param <V>   The type of the left value
     * @param <W>   The type of the right value
     * @return The constructed pair
     */
    public static <V, W> Pair<V, W> of(final V left, final W right) {
        return new Pair<>(left, right);
    }

}
