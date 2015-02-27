package com.sebastian_daschner.jaxrs_analyzer.backend;

import com.sebastian_daschner.jaxrs_analyzer.model.rest.Resources;

/**
 * Renders the analyzed JAX-RS resources into a String representation.
 *
 * @author Sebastian Daschner
 */
public interface Backend {

    /**
     * Renders the content representation for the given resources.
     *
     * @param resources The resources to render
     * @return The String representation
     */
    String render(Resources resources);

}
