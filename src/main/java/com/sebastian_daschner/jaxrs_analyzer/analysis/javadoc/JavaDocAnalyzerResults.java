package com.sebastian_daschner.jaxrs_analyzer.analysis.javadoc;

import com.sebastian_daschner.jaxrs_analyzer.model.javadoc.ClassComment;
import com.sebastian_daschner.jaxrs_analyzer.model.results.ClassResult;

import java.util.Map;
import java.util.Set;

/**
 * @author Tristan Perry
 */
public class JavaDocAnalyzerResults {

    /**
     * Contains a range of class-level data, including the JAX-RS 'endpoint' methods which will be invoked when endpoints are hit.
     */
    private final Set<ClassResult> classResults;

    /**
     * Contains JavaDoc comments/messages for classes and their fields.
     */
    private final Map<String, ClassComment> classComments;

    public JavaDocAnalyzerResults(Set<ClassResult> classResults, Map<String, ClassComment> classComments) {
        this.classResults = classResults;
        this.classComments = classComments;
    }

    public Set<ClassResult> getClassResults() {
        return classResults;
    }

    public Map<String, ClassComment> getClassComments() {
        return classComments;
    }

}
