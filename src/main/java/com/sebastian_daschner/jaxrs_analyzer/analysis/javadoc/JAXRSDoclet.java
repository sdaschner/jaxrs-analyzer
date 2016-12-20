package com.sebastian_daschner.jaxrs_analyzer.analysis.javadoc;

import com.sebastian_daschner.jaxrs_analyzer.model.JavaUtils;
import com.sebastian_daschner.jaxrs_analyzer.model.methods.MethodIdentifier;
import com.sun.javadoc.*;

import java.util.stream.Stream;

import static com.sebastian_daschner.jaxrs_analyzer.model.JavaUtils.getMethodSignature;
import static com.sebastian_daschner.jaxrs_analyzer.model.Types.*;
import static com.sebastian_daschner.jaxrs_analyzer.model.methods.MethodIdentifier.of;

/**
 * @author Sebastian Daschner
 */
public class JAXRSDoclet {

    public static boolean start(RootDoc rootDoc) {
        Stream.of(rootDoc.classes()).forEach(JAXRSDoclet::handleClassDoc);
        return true;
    }

    private static void handleClassDoc(final ClassDoc classDoc) {
        final String className = toClassName(classDoc.qualifiedName());
        JavaDocAnalyzer.put(className, classDoc);
        Stream.of(classDoc.methods()).forEach(m -> handleMethodDoc(m, className));
    }

    private static void handleMethodDoc(final MethodDoc methodDoc, final String className) {
        final String[] parameterTypes = Stream.of(methodDoc.parameters())
                .map(p -> p.type().qualifiedTypeName())
                .map(JAXRSDoclet::toType)
                .toArray(String[]::new);

        final String returnType = toType(methodDoc.returnType().qualifiedTypeName());
        final String signature = getMethodSignature(returnType, parameterTypes);

        final MethodIdentifier identifier = of(className, methodDoc.name(), signature, methodDoc.isStatic());
        JavaDocAnalyzer.put(identifier, methodDoc);
    }

    private static String toClassName(final String qualifiedName) {
        return qualifiedName.replace('.', '/');
    }

    private static String toType(final String qualifiedName) {
        switch (qualifiedName) {
            case CLASS_PRIMITIVE_VOID:
                return PRIMITIVE_VOID;
            case CLASS_PRIMITIVE_BOOLEAN:
                return PRIMITIVE_BOOLEAN;
            case CLASS_PRIMITIVE_CHAR:
                return PRIMITIVE_CHAR;
            case CLASS_PRIMITIVE_INT:
                return PRIMITIVE_INT;
            case CLASS_PRIMITIVE_BYTE:
                return PRIMITIVE_BYTE;
            case CLASS_PRIMITIVE_SHORT:
                return PRIMITIVE_SHORT;
            case CLASS_PRIMITIVE_DOUBLE:
                return PRIMITIVE_DOUBLE;
            case CLASS_PRIMITIVE_FLOAT:
                return PRIMITIVE_FLOAT;
            case CLASS_PRIMITIVE_LONG:
                return PRIMITIVE_LONG;
            default:
                return JavaUtils.toType(toClassName(qualifiedName));
        }
    }

}
