package com.sebastian_daschner.jaxrs_analyzer.analysis.classes.annotation;

import com.sebastian_daschner.jaxrs_analyzer.model.results.ClassResult;
import com.sebastian_daschner.jaxrs_analyzer.model.results.MethodResult;

import java.util.Objects;

/**
 * @author Sebastian Daschner
 */
public abstract class ClassAndMethodAnnotationVisitor<T> extends ValueAnnotationVisitor<T> {

    private final ClassResult classResult;
    private final MethodResult methodResult;

    protected ClassAndMethodAnnotationVisitor(ClassResult classResult) {
        this(classResult, null);
        Objects.requireNonNull(classResult);
    }

    protected ClassAndMethodAnnotationVisitor(MethodResult methodResult) {
        this(null, methodResult);
        Objects.requireNonNull(methodResult);
    }

    private ClassAndMethodAnnotationVisitor(ClassResult classResult, MethodResult methodResult) {
        this.classResult = classResult;
        this.methodResult = methodResult;
    }

    protected abstract void visitValue(T value, ClassResult classResult);

    protected abstract void visitValue(T value, MethodResult methodResult);

    @Override
    protected final void visitValue(T value) {
        if (classResult != null)
            visitValue(value, classResult);
        else
            visitValue(value, methodResult);
    }

}
