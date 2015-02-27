package com.sebastian_daschner.jaxrs_analyzer.model.methods;

import com.sebastian_daschner.jaxrs_analyzer.model.instructions.InvokeInstruction;
import com.sebastian_daschner.jaxrs_analyzer.model.elements.Element;

import java.util.List;

/**
 * Represents a method which will be invoked via an {@link InvokeInstruction}.
 *
 * @author Sebastian Daschner
 */
public interface Method {

    /**
     * Invokes the method on the object with the given arguments.
     *
     * @param object    The object where the method is invoked ({@code null} for a static method)
     * @param arguments The arguments of the method
     * @return The method response or {@code null} for void
     */
    Element invoke(Element object, List<Element> arguments);

}
