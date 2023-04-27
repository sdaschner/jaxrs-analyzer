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

package com.sebastian_daschner.jaxrs_analyzer.model.rest;

import com.sebastian_daschner.jaxrs_analyzer.model.Types;
import com.sebastian_daschner.jaxrs_analyzer.model.javadoc.MemberParameterTag;

import java.util.*;

/**
 * Represents a request/response body type including the properties which actually will be serialized (e.g. depending on the JAXB mapping).
 * Enables the {@link TypeRepresentationVisitor}s to access the recursive model.
 *
 * @author Sebastian Daschner
 */
public abstract class TypeRepresentation {

    private final TypeIdentifier identifier;

    private TypeRepresentation(final TypeIdentifier identifier) {
        this.identifier = identifier;
    }

    public abstract void accept(final TypeRepresentationVisitor visitor);

    public TypeIdentifier getIdentifier() {
        return identifier;
    }

    /**
     * Returns the component type which is either the actual type identifier or the contained type for a collection type.
     *
     * @return The component type
     */
    public abstract TypeIdentifier getComponentType();

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TypeRepresentation that = (TypeRepresentation) o;
        return identifier.equals(that.identifier);
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }

    /**
     * Creates a type representation of a concrete type (i.e. a Java type, not a programmatically created type) without actual properties.
     * This is used for JDK internal types (like {@link String}, {@link Object}) where no property analysis is desired.
     *
     * @param identifier The type identifier
     * @return The type representation
     */
    public static TypeRepresentation ofConcrete(final TypeIdentifier identifier) {
        return new ConcreteTypeRepresentation(identifier, Collections.emptyMap());
    }

    /**
     * Creates a type representation of a concrete type (i.e. a Java type, not a programmatically created type) plus the actual properties.
     *
     * @param identifier The type identifier
     * @param properties The type (POJO) description
     * @return The type representation
     */
    public static TypeRepresentation ofConcrete(final TypeIdentifier identifier, final Map<String, TypeIdentifier> properties) {
        return new ConcreteTypeRepresentation(identifier, properties);
    }

    /**
     * Creates a type representation of a collection type (i.e. anything assignable to {@link java.util.Collection} or an array) which contains an actual representation.
     * <p>
     * Example: {@code identifier: java.util.List<java.lang.String>, typeRepresentation: java.lang.String}
     *
     * @param identifier         The type identifier of the collection type
     * @param typeRepresentation The contained type representation
     * @return The type representation
     */
    public static TypeRepresentation ofCollection(final TypeIdentifier identifier, final TypeRepresentation typeRepresentation) {
        return new CollectionTypeRepresentation(identifier, typeRepresentation);
    }

	/**
	 * Creates a type representation of an optional type.
	 * <p>
	 * Example: {@code identifier: java.util.Optional<java.lang.String>, typeRepresentation: java.lang.String}
	 *
	 * @param identifier         The type identifier of the optional type
	 * @param typeRepresentation The contained type representation
	 * @return The type representation
	 */
	public static TypeRepresentation ofOptional(final TypeIdentifier identifier, final TypeRepresentation typeRepresentation) {
		return new OptionalTypeRepresentation(identifier, typeRepresentation);
	}

    /**
     * Creates a type representation of an enum type plus the available enumeration values.
     *
     * @param identifier The type identifier
     * @param enumValues The enum values
     * @return The type representation
     */
    public static TypeRepresentation ofEnum(final TypeIdentifier identifier, final String... enumValues) {
        return new EnumTypeRepresentation(identifier, new HashSet<>(Arrays.asList(enumValues)));
    }

    public static class ConcreteTypeRepresentation extends TypeRepresentation {

        private final Map<String, TypeIdentifier> properties;

        private final Map<String, MemberParameterTag> propertyDocs = new HashMap<>();

        private String classDoc;

        private ConcreteTypeRepresentation(final TypeIdentifier identifier, final Map<String, TypeIdentifier> properties) {
            super(identifier);
            this.properties = properties;
        }

        public String getClassDoc() {
            return classDoc;
        }

        public void setClassDoc(String classDoc) {
            this.classDoc = classDoc;
        }

        public Map<String, MemberParameterTag> getPropertyDocs() {
            return propertyDocs;
        }

        public Map<String, TypeIdentifier> getProperties() {
            return properties;
        }

        @Override
        public TypeIdentifier getComponentType() {
            return getIdentifier();
        }

        @Override
        public void accept(final TypeRepresentationVisitor visitor) {
            visitor.visit(this);
        }

        /**
         * Checks if the properties of this representation matches the given properties.
         *
         * @param properties The other properties to check
         * @return {@code true} if the content equals
         */
        public boolean contentEquals(final Map<String, TypeIdentifier> properties) {
            return this.properties.equals(properties);
        }

        @Override
        public String toString() {
            return "ConcreteTypeRepresentation{" +
                    "identifier=" + getIdentifier() +
                    ",properties=" + properties +
                    '}';
        }
    }

	public static class OptionalTypeRepresentation extends TypeRepresentation {

		private final TypeRepresentation representation;

		private OptionalTypeRepresentation(final TypeIdentifier identifier, final TypeRepresentation representation) {
			super(identifier);
			this.representation = representation;
		}

		public TypeRepresentation getRepresentation() {
			return representation;
		}

		@Override
		public TypeIdentifier getComponentType() {
			return representation.getIdentifier();
		}

		@Override
		public void accept(final TypeRepresentationVisitor visitor) {
			representation.accept(visitor);
		}

		/**
		 * Checks if the nested type of this collection representation matches the given type (i.e. the same property bindings for concrete types
		 * or the same contained representation for collection types). This does not check the actual type (identifier).
		 *
		 * @param representation The other nested representation to check
		 * @return {@code true} if the content equals
		 */
		public boolean contentEquals(final TypeRepresentation representation) {
			final boolean thisStaticType = !this.representation.getIdentifier().getType().equals(Types.JSON);
			final boolean otherStaticType = !representation.getIdentifier().getType().equals(Types.JSON);

			if (thisStaticType ^ otherStaticType)
				return false;

			if (thisStaticType)
				return this.representation.getIdentifier().equals(representation.getIdentifier());

			final boolean thisOption = this.representation instanceof OptionalTypeRepresentation;
			final boolean thatOption = representation instanceof OptionalTypeRepresentation;
			if (thisOption ^ thatOption)
				return false;

			if (thisOption)
				return ((OptionalTypeRepresentation) this.representation).contentEquals(((OptionalTypeRepresentation) representation).getRepresentation());

			return ((ConcreteTypeRepresentation) this.representation).contentEquals(((ConcreteTypeRepresentation) representation).getProperties());
		}

		@Override
		public String toString() {
			return "OptionalTypeRepresentation{" +
					"identifier=" + getIdentifier() +
					",representation=" + representation +
					'}';
		}
	}

    public static class CollectionTypeRepresentation extends TypeRepresentation {

        private final TypeRepresentation representation;

        private CollectionTypeRepresentation(final TypeIdentifier identifier, final TypeRepresentation representation) {
            super(identifier);
            this.representation = representation;
        }

        public TypeRepresentation getRepresentation() {
            return representation;
        }

        @Override
        public TypeIdentifier getComponentType() {
            return representation.getIdentifier();
        }

        @Override
        public void accept(final TypeRepresentationVisitor visitor) {
            visitor.visitStart(this);
            representation.accept(visitor);
            visitor.visitEnd(this);
        }

        /**
         * Checks if the nested type of this collection representation matches the given type (i.e. the same property bindings for concrete types
         * or the same contained representation for collection types). This does not check the actual type (identifier).
         *
         * @param representation The other nested representation to check
         * @return {@code true} if the content equals
         */
        public boolean contentEquals(final TypeRepresentation representation) {
            final boolean thisStaticType = !this.representation.getIdentifier().getType().equals(Types.JSON);
            final boolean otherStaticType = !representation.getIdentifier().getType().equals(Types.JSON);

            if (thisStaticType ^ otherStaticType)
                return false;

            if (thisStaticType)
                return this.representation.getIdentifier().equals(representation.getIdentifier());

            final boolean thisCollection = this.representation instanceof CollectionTypeRepresentation;
            final boolean thatCollection = representation instanceof CollectionTypeRepresentation;
            if (thisCollection ^ thatCollection)
                return false;

            if (thisCollection)
                return ((CollectionTypeRepresentation) this.representation).contentEquals(((CollectionTypeRepresentation) representation).getRepresentation());

            return ((ConcreteTypeRepresentation) this.representation).contentEquals(((ConcreteTypeRepresentation) representation).getProperties());
        }

        @Override
        public String toString() {
            return "CollectionTypeRepresentation{" +
                    "identifier=" + getIdentifier() +
                    ",representation=" + representation +
                    '}';
        }
    }

    public static class EnumTypeRepresentation extends TypeRepresentation {

        private final Set<String> enumValues;

        private final Map<String, MemberParameterTag> enumValuesDoc = new HashMap<>();
        private String classDoc;

        private EnumTypeRepresentation(final TypeIdentifier identifier, final Set<String> enumValues) {
            super(identifier);
            this.enumValues = enumValues;
        }

        public String getClassDoc() {
            return classDoc;
        }

        public void setClassDoc(String classDoc) {
            this.classDoc = classDoc;
        }

        public Map<String, MemberParameterTag> getEnumValuesDoc() {
            return enumValuesDoc;
        }

        public Set<String> getEnumValues() {
            return enumValues;
        }

        @Override
        public void accept(final TypeRepresentationVisitor visitor) {
            visitor.visit(this);
        }

        @Override
        public TypeIdentifier getComponentType() {
            return getIdentifier();
        }

        @Override
        public String toString() {
            return "EnumTypeRepresentation{" +
                    "enumValues=" + enumValues +
                    '}';
        }
    }

}
