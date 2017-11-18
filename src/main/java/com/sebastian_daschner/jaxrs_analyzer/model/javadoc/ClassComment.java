package com.sebastian_daschner.jaxrs_analyzer.model.javadoc;

import java.util.ArrayList;
import java.util.List;

public class ClassComment extends MemberComment {

    private List<MemberParameterTag> fieldComments = new ArrayList<>();

    public ClassComment(String comment, boolean deprecated) {
        super(comment, deprecated);
    }

    public List<MemberParameterTag> getFieldComments() {
        return fieldComments;
    }

}
