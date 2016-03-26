package com.sebastian_daschner.jaxrs_analyzer.analysis.classes;

import com.sebastian_daschner.jaxrs_analyzer.LogProvider;
import com.sebastian_daschner.jaxrs_analyzer.analysis.classes.annotation.*;
import com.sebastian_daschner.jaxrs_analyzer.model.Types;
import com.sebastian_daschner.jaxrs_analyzer.model.results.ClassResult;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;

/**
 * @author Sebastian Daschner
 */
class JAXRSFieldVisitor extends FieldVisitor {

    private final ClassResult classResult;
    private final String desc;
    private final String signature;

    JAXRSFieldVisitor(final ClassResult classResult, final String desc, final String signature) {
        super(Opcodes.ASM5);
        this.classResult = classResult;
        this.desc = desc;
        this.signature = signature;
    }

    @Override
    public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
        switch (desc) {
            case Types.PATH_PARAM:
                return new PathParamAnnotationVisitor(classResult, signature == null ? this.desc : signature);
            case Types.QUERY_PARAM:
                return new QueryParamAnnotationVisitor(classResult, signature == null ? this.desc : signature);
            case Types.HEADER_PARAM:
                return new HeaderParamAnnotationVisitor(classResult, signature == null ? this.desc : signature);
            case Types.FORM_PARAM:
                return new FormParamAnnotationVisitor(classResult, signature == null ? this.desc : signature);
            case Types.COOKIE_PARAM:
                return new CookieParamAnnotationVisitor(classResult, signature == null ? this.desc : signature);
            case Types.MATRIX_PARAM:
                return new MatrixParamAnnotationVisitor(classResult, signature == null ? this.desc : signature);
            default:
                LogProvider.debug("Annotation not handled: " + desc);
                return null;
        }
    }

}
