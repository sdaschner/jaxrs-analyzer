package com.sebastian_daschner.jaxrs_analyzer.backend.swagger;

import java.util.EnumSet;
import java.util.Set;

/**
 * The Swagger backend specific configuration properties.
 *
 * @author Sebastian Daschner
 */
class SwaggerOptions {

    private static final String DEFAULT_DOMAIN = "example.com";
    private static final Set<SwaggerScheme> DEFAULT_SCHEMES = EnumSet.of(SwaggerScheme.HTTP);
    private static final boolean DEFAULT_RENDER_TAGS = false;
    private static final int DEFAULT_TAGS_PATH_OFFSET = 0;

    private String domain = DEFAULT_DOMAIN;
    private Set<SwaggerScheme> schemes = DEFAULT_SCHEMES;
    private boolean renderTags = DEFAULT_RENDER_TAGS;
    private int tagsPathOffset = DEFAULT_TAGS_PATH_OFFSET;

    String getDomain() {
        return domain;
    }

    void setDomain(final String domain) {
        this.domain = domain;
    }

    Set<SwaggerScheme> getSchemes() {
        return schemes;
    }

    void setSchemes(final Set<SwaggerScheme> schemes) {
        this.schemes = schemes;
    }

    boolean isRenderTags() {
        return renderTags;
    }

    void setRenderTags(final boolean renderTags) {
        this.renderTags = renderTags;
    }

    int getTagsPathOffset() {
        return tagsPathOffset;
    }

    void setTagsPathOffset(final int tagsPathOffset) {
        this.tagsPathOffset = tagsPathOffset;
    }

}
