package com.sebastian_daschner.jaxrs_analyzer.backend.swagger;

import com.sebastian_daschner.jaxrs_analyzer.backend.Backend;

import java.util.Set;

/**
 * Swagger backend builder.
 *
 * @author Sebastian Daschner
 */
public class SwaggerBackendBuilder implements Backend.BackendBuilder {

    SwaggerOptions options = new SwaggerOptions();

    /**
     * Sets the deployed domain of the project.
     */
    public SwaggerBackendBuilder domain(final String domain) {
        options.setDomain(domain);
        return this;
    }

    /**
     * Sets the transfer protocol (https://github.com/OAI/OpenAPI-Specification/blob/master/versions/2.0.md#swaggerSchemes) of the project.
     */
    public SwaggerBackendBuilder schemes(final Set<SwaggerScheme> schemes) {
        options.setSchemes(schemes);
        return this;
    }

    /**
     * Specifies whether Swagger tags (https://github.com/OAI/OpenAPI-Specification/blob/master/versions/2.0.md#tagObject)
     * &mdash; generated from the paths &mdash; should be rendered
     *
     * @param renderTags Flag if tags should be rendered
     */
    public SwaggerBackendBuilder renderTags(final boolean renderTags) {
        options.setRenderTags(renderTags);
        return this;
    }

    /**
     * Specifies whether Swagger tags (https://github.com/OAI/OpenAPI-Specification/blob/master/versions/2.0.md#tagObject)
     * &mdash; generated from the paths &mdash; should be rendered
     *
     * @param renderTags     Flag if tags should be rendered
     * @param tagsPathOffset The path offset to take
     */
    public SwaggerBackendBuilder renderTags(final boolean renderTags, final int tagsPathOffset) {
        options.setRenderTags(renderTags);
        options.setTagsPathOffset(tagsPathOffset);
        return this;
    }

    @Override
    public SwaggerBackend build() {
        return new SwaggerBackend(options);
    }

}
