package com.sebastian_daschner.jaxrs_analyzer.model.javadoc;

import java.util.Collections;
import java.util.List;

public class MethodComment extends MemberComment {

    private final List<MemberParameterTag> paramTags;
    private final ClassComment containingClassComment;

    public MethodComment(String comment) {
        this(comment, Collections.emptyList(), null, false);
    }

    public MethodComment(String comment, List<MemberParameterTag> paramTags) {
        this(comment, paramTags, null, false);
    }

    public MethodComment(String comment, List<MemberParameterTag> paramTags, ClassComment containingClassComment) {
        this(comment, paramTags, containingClassComment, false);
    }

    public MethodComment(String comment, List<MemberParameterTag> paramTags, ClassComment containingClassComment, boolean deprecated) {
        super(comment, deprecated);
        this.paramTags = Collections.unmodifiableList(paramTags);
        this.containingClassComment = containingClassComment;
    }

    public List<MemberParameterTag> getParamTags() {
        return paramTags;
    }

    public ClassComment getContainingClassComment() {
        return containingClassComment;
    }

}
