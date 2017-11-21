package com.sebastian_daschner.jaxrs_analyzer.model.javadoc;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MethodComment extends MemberComment {

    private final List<MemberParameterTag> paramTags;
    private final ClassComment containingClassComment;

    public MethodComment(String comment) {
        this(comment, Collections.emptyList(), Collections.emptyMap(), null, false);
    }

    public MethodComment(String comment, List<MemberParameterTag> paramTags, Map<Integer, String> responseComments, ClassComment containingClassComment, boolean deprecated) {
        super(comment, responseComments, deprecated);
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
