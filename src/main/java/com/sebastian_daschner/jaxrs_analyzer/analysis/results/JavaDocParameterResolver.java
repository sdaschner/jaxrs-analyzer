package com.sebastian_daschner.jaxrs_analyzer.analysis.results;

import com.sebastian_daschner.jaxrs_analyzer.model.javadoc.ClassComment;
import com.sebastian_daschner.jaxrs_analyzer.model.javadoc.MemberParameterTag;
import com.sebastian_daschner.jaxrs_analyzer.model.javadoc.MethodComment;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.MethodParameter;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.ParameterType;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static com.sebastian_daschner.jaxrs_analyzer.model.JavaUtils.toReadableType;
import static com.sebastian_daschner.jaxrs_analyzer.model.Types.*;

/**
 * Resolves the actual {@code *Param} parameters analyzed by both the JavaDoc and Bytecode analysis.
 *
 * @author Sebastian Daschner
 */
final class JavaDocParameterResolver {

    private static final String[] KNOWN_ANNOTATIONS = {PATH_PARAM, QUERY_PARAM, HEADER_PARAM, FORM_PARAM, COOKIE_PARAM, MATRIX_PARAM, DEFAULT_VALUE, SUSPENDED, CONTEXT};

    private JavaDocParameterResolver() {
        throw new UnsupportedOperationException();
    }

    static Optional<MemberParameterTag> findParameterDoc(final MethodParameter parameter, final MethodComment methodDoc) {
        return methodDoc.getParamTags().stream()
                .filter(p -> hasAnnotation(parameter, p.getAnnotations()))
                .findAny();
    }

    static Optional<MemberParameterTag> findFieldDoc(final MethodParameter parameter, final ClassComment classDoc) {
        if (classDoc == null)
            return Optional.empty();

        return classDoc.getFieldComments().stream()
                .filter(f -> hasAnnotation(parameter, f.getAnnotations()))
                .findAny();
    }

    static Optional<MemberParameterTag> findRequestBodyDoc(final MethodComment methodDoc) {
        return methodDoc.getParamTags().stream()
                .filter(p -> isRequestBody(p.getAnnotations()))
                .findAny();
    }

    private static boolean hasAnnotation(final MethodParameter parameter, final Map<String, String> annotations) {
        return annotations.entrySet().stream()
                .filter(e -> annotationTypeMatches(e.getKey(), parameter.getParameterType()))
                .anyMatch(e -> Objects.equals(e.getValue(), parameter.getName()));
    }

    private static boolean isRequestBody(final Map<String, String> annotations) {
        return annotations.entrySet().stream()
                .noneMatch(e -> findKnownAnnotation(e.getKey()));
    }

    private static boolean findKnownAnnotation(String simpleTypeName) {
        return Stream.of(KNOWN_ANNOTATIONS).anyMatch(a -> a.contains(simpleTypeName));
    }

    private static boolean annotationTypeMatches(final String qualifiedTypeName, final ParameterType parameterType) {
        String javaType = getJavaType(parameterType);
        return javaType != null && javaType.contains(qualifiedTypeName);
    }

    private static String getJavaType(final ParameterType parameterType) {
        switch (parameterType) {
            case PATH:
                return toReadableType(PATH_PARAM);
            case QUERY:
                return toReadableType(QUERY_PARAM);
            case HEADER:
                return toReadableType(HEADER_PARAM);
            case FORM:
                return toReadableType(FORM_PARAM);
            case MATRIX:
                return toReadableType(MATRIX_PARAM);
            case COOKIE:
                return toReadableType(COOKIE_PARAM);
            default:
                return null;
        }
    }

}
