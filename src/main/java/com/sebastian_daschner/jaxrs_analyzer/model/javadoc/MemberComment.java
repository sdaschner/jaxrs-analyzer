package com.sebastian_daschner.jaxrs_analyzer.model.javadoc;

public class MemberComment {

    protected final String comment;
    protected final boolean deprecated;

    public MemberComment(String comment, boolean deprecated) {
        this.comment = comment;
        this.deprecated = deprecated;
    }

    public String getComment() {
        return comment;
    }

    public boolean isDeprecated() {
        return deprecated;
    }

}
