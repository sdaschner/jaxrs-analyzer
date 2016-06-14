package com.sebastian_daschner.jaxrs_analyzer.analysis.classes;

import com.sebastian_daschner.jaxrs_analyzer.LogProvider;
import com.sebastian_daschner.jaxrs_analyzer.analysis.classes.annotation.DefaultValueAnnotationVisitor;
import com.sebastian_daschner.jaxrs_analyzer.analysis.classes.annotation.ParamAnnotationVisitor;
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
    private final String name;
    private final String desc;
    private final String signature;

    JAXRSFieldVisitor(final ClassResult classResult, final String name, final String desc, final String signature) {
        super(Opcodes.ASM5);
        this.classResult = classResult;
        this.name = name;
        this.desc = desc;
        this.signature = signature;
    }

    @Override
    public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
        switch (desc) {
            case Types.PATH_PARAM:
            case Types.QUERY_PARAM:
            case Types.HEADER_PARAM:
            case Types.FORM_PARAM:
            case Types.COOKIE_PARAM:
            case Types.MATRIX_PARAM:
                return new ParamAnnotationVisitor(classResult, this.name, desc, signature == null ? this.desc : signature);
            case Types.DEFAULT_VALUE:
                return new DefaultValueAnnotationVisitor(classResult, this.name, desc, signature == null ? this.desc : signature);
            default:
                LogProvider.debug("Annotation not handled: " + desc);
                return null;
        }
    }

}
