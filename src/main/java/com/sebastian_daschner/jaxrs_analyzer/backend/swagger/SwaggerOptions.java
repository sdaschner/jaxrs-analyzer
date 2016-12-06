package com.sebastian_daschner.jaxrs_analyzer.backend.swagger;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

/**
 * The Swagger backend specific configuration properties.
 *
 * @author Sebastian Daschner
 */
public class SwaggerOptions {

    public static final String DOMAIN = "domain";
    public static final String SWAGGER_SCHEMES = "swaggerSchemes";
    public static final String RENDER_SWAGGER_TAGS = "renderSwaggerTags";
    public static final String SWAGGER_TAGS_PATH_OFFSET = "swaggerTagsPathOffset";

    private static final String DEFAULT_DOMAIN = "example.com";
    private static final Set<SwaggerScheme> DEFAULT_SCHEMES = EnumSet.of(SwaggerScheme.HTTP);
    private static final boolean DEFAULT_RENDER_TAGS = false;
    private static final int DEFAULT_TAGS_PATH_OFFSET = 0;

    /**
     * The deployed domain of the project.
     */
    private String domain = DEFAULT_DOMAIN;

    /**
     * The transfer protocol (https://github.com/OAI/OpenAPI-Specification/blob/master/versions/2.0.md#swaggerSchemes) of the project.
     */
    private Set<SwaggerScheme> schemes = DEFAULT_SCHEMES;

    /**
     * Flag if Swagger tags (https://github.com/OAI/OpenAPI-Specification/blob/master/versions/2.0.md#tagObject)
     * &mdash; generated from the paths &mdash; should be rendered.
     */
    private boolean renderTags = DEFAULT_RENDER_TAGS;

    /**
     * The path offset of the Swagger tags.
     */
    private int tagsPathOffset = DEFAULT_TAGS_PATH_OFFSET;

    String getDomain() {
        return domain;
    }

    Set<SwaggerScheme> getSchemes() {
        return schemes;
    }

    boolean isRenderTags() {
        return renderTags;
    }

    int getTagsPathOffset() {
        return tagsPathOffset;
    }

    void configure(final Map<String, String> config) {
        if (config.containsKey(SWAGGER_TAGS_PATH_OFFSET)) {
            int swaggerTagsPathOffset = Integer.parseInt(config.get(SWAGGER_TAGS_PATH_OFFSET));

            if (swaggerTagsPathOffset < 0) {
                System.err.println("Please provide positive integer number for option --swaggerTagsPathOffset\n");
                throw new IllegalArgumentException("Please provide positive integer number for option --swaggerTagsPathOffset");
            }

            tagsPathOffset = swaggerTagsPathOffset;
        }

        if (config.containsKey(DOMAIN)) {
            domain = config.get(DOMAIN);
        }

        if (config.containsKey(SWAGGER_SCHEMES)) {
            schemes = extractSwaggerSchemes(config.get(SWAGGER_SCHEMES));
        }

        if (config.containsKey(RENDER_SWAGGER_TAGS)) {
            renderTags = Boolean.parseBoolean(config.get(RENDER_SWAGGER_TAGS));
        }
    }

    private Set<SwaggerScheme> extractSwaggerSchemes(final String schemes) {
        return Stream.of(schemes.split(","))
                .map(this::extractSwaggerScheme)
                .collect(() -> EnumSet.noneOf(SwaggerScheme.class), Set::add, Set::addAll);
    }

    private SwaggerScheme extractSwaggerScheme(final String scheme) {
        switch (scheme.toLowerCase()) {
            case "http":
                return SwaggerScheme.HTTP;
            case "https":
                return SwaggerScheme.HTTPS;
            case "ws":
                return SwaggerScheme.WS;
            case "wss":
                return SwaggerScheme.WSS;
            default:
                throw new IllegalArgumentException("Unknown swagger scheme " + scheme);
        }
    }

}
