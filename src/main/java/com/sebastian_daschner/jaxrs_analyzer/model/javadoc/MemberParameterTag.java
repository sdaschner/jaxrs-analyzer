package com.sebastian_daschner.jaxrs_analyzer.model.javadoc;

import java.util.Collections;
import java.util.Map;

/**
 * The comment parameter to a method parameter tag or a class field.
 * <p>
 * All types are not necessarily valid Java types but the simple names of the types.
 * Doing a full JavaDoc type resolving with all imports adds too much complexity at this point.
 * This is a best-effort approach.
 */
public class MemberParameterTag {

    private final String comment;
    private final String tagName;

    /**
     * The annotations with their type and {@code values()};
     */
    private final Map<String, String> annotations;

    public MemberParameterTag(String comment, String tagName, Map<String, String> annotations) {
        this.comment = comment;
        this.tagName = tagName;
        this.annotations = Collections.unmodifiableMap(annotations);
    }

    public String getComment() {
        return comment;
    }

    public String getTagName() {
        return tagName;
    }

    public Map<String, String> getAnnotations() {
        return annotations;
    }

}
