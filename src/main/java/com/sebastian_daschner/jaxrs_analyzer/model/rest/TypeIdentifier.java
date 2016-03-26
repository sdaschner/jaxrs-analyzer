package com.sebastian_daschner.jaxrs_analyzer.model.rest;

import com.sebastian_daschner.jaxrs_analyzer.model.Types;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Identifies a type representation.
 * A type identifier is either a Java type or an &quo;dynamic&quo; type (i.e. a {@link javax.json.JsonValue}).
 *
 * @author Sebastian Daschner
 */
public abstract class TypeIdentifier {

    public static final String DYNAMIC_TYPE_PREFIX = "$";
    private static final AtomicInteger dynamicCounter = new AtomicInteger();

    @Override
    public abstract boolean equals(final Object object);

    public abstract String getType();

    public abstract String getName();

    public static TypeIdentifier ofType(final String type) {
        return new JavaTypeIdentifier(type);
    }

    public static TypeIdentifier ofDynamic() {
        return new DynamicTypeIdentifier(dynamicCounter.incrementAndGet());
    }

    private static class JavaTypeIdentifier extends TypeIdentifier {
        private final String type;

        public JavaTypeIdentifier(final String type) {
            Objects.requireNonNull(type);
            this.type = type;
        }

        @Override
        public String getType() {
            return type;
        }

        @Override
        public String getName() {
            return type;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            JavaTypeIdentifier that = (JavaTypeIdentifier) o;

            return type.equals(that.type);
        }

        @Override
        public int hashCode() {
            return type.hashCode();
        }

        @Override
        public String toString() {
            return "JavaTypeIdentifier{" +
                    "type=" + type +
                    '}';
        }
    }

    private static class DynamicTypeIdentifier extends TypeIdentifier {

        private final int number;

        public DynamicTypeIdentifier(final int number) {
            this.number = number;
        }

        @Override
        public String getType() {
            return Types.JSON;
        }

        @Override
        public String getName() {
            return DYNAMIC_TYPE_PREFIX + number;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            DynamicTypeIdentifier that = (DynamicTypeIdentifier) o;

            return number == that.number;
        }

        @Override
        public int hashCode() {
            return number;
        }

        @Override
        public String toString() {
            return "DynamicTypeIdentifier{" +
                    "number=" + number +
                    '}';
        }
    }

}
