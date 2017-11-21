package com.sebastian_daschner.jaxrs_analyzer.model.javadoc;

import java.util.Collections;
import java.util.Map;

public class MemberComment {

    protected final String comment;
    protected final boolean deprecated;
    protected final Map<Integer, String> responseComments;

    public MemberComment(String comment, Map<Integer, String> responseComments, boolean deprecated) {
        this.comment = comment;
        this.responseComments = Collections.unmodifiableMap(responseComments);
        this.deprecated = deprecated;
    }

    public String getComment() {
        return comment;
    }

    public boolean isDeprecated() {
        return deprecated;
    }

    public Map<Integer, String> getResponseComments() {
        return responseComments;
    }

}
