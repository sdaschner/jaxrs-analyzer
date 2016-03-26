package com.sebastian_daschner.jaxrs_analyzer.analysis.classes.annotation;

import com.sebastian_daschner.jaxrs_analyzer.model.results.ClassResult;

/**
 * @author Sebastian Daschner
 */
public class ApplicationPathAnnotationVisitor extends ValueAnnotationVisitor<String> {

    private final ClassResult classResult;

    public ApplicationPathAnnotationVisitor(final ClassResult classResult) {
        this.classResult = classResult;
    }

    @Override
    protected void visitValue(final String value) {
        classResult.setApplicationPath(value);
    }

}
