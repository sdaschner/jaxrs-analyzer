package com.sebastian_daschner.jaxrs_analyzer.model.javadoc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassComment extends MemberComment {

    private final Map<String, MemberParameterTag> fieldComments = new HashMap<>();

    public ClassComment() {
        this("", new HashMap<>(), false);
    }

    public ClassComment(String comment, Map<Integer, String> responseComments, boolean deprecated) {
        super(comment, responseComments, deprecated);
    }

    public Map<String, MemberParameterTag> getFieldComments() {
        return fieldComments;
    }

}
