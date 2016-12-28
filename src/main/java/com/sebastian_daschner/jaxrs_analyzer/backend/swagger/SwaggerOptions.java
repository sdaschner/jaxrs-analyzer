package com.sebastian_daschner.jaxrs_analyzer.backend.swagger;

import com.sebastian_daschner.jaxrs_analyzer.LogProvider;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonPatch;
import java.nio.file.Files;
import java.nio.file.Paths;
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
    public static final String JSON_PATCH = "jsonPatch";

    private static final String DEFAULT_DOMAIN = "";
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

    /**
     * The optional JSON patch (RFC 6902) that can modify the Swagger JSON output.
     */
    private JsonPatch jsonPatch;

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

    JsonPatch getJsonPatch() {
        return jsonPatch;
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

        if (config.containsKey(JSON_PATCH)) {
            jsonPatch = readPatch(config.get(JSON_PATCH));
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

    private static JsonPatch readPatch(final String patchFile) {
        try {
            final JsonArray patchArray = Json.createReader(Files.newBufferedReader(Paths.get(patchFile))).readArray();
            return Json.createPatchBuilder(patchArray).build();
        } catch (Exception e) {
            LogProvider.error("Could not read JSON patch from the specified location, reason: " + e.getMessage());
            LogProvider.error("Patch won't be applied");
            LogProvider.debug(e);
            return null;
        }
    }

}
