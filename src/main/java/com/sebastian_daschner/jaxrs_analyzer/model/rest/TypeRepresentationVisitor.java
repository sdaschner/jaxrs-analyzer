package com.sebastian_daschner.jaxrs_analyzer.model.rest;

/**
 * Visitor for the {@link TypeRepresentation} implementations.
 *
 * @author Sebastian Daschner
 */
public interface TypeRepresentationVisitor {

    void visit(TypeRepresentation.ConcreteTypeRepresentation representation);

    void visit(TypeRepresentation.CollectionTypeRepresentation representation);

    void visit(TypeRepresentation.EnumTypeRepresentation representation);

}
