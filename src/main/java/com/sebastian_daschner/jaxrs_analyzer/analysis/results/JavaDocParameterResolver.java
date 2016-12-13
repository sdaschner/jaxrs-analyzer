package com.sebastian_daschner.jaxrs_analyzer.analysis.results;

import com.sebastian_daschner.jaxrs_analyzer.model.rest.MethodParameter;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.ParameterType;
import com.sun.javadoc.*;

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

    static Optional<ParamTag> findParameterDoc(final MethodParameter parameter, final MethodDoc methodDoc) {
        final Optional<String> paramName = Stream.of(methodDoc.parameters())
                .filter(p -> hasAnnotation(parameter, p.annotations()))
                .map(Parameter::name)
                .findAny();

        if (!paramName.isPresent())
            return Optional.empty();

        return Stream.of(methodDoc.paramTags())
                .filter(t -> t.parameterName().equals(paramName.get()))
                .findAny();
    }

    static Optional<FieldDoc> findFieldDoc(final MethodParameter parameter, final ClassDoc classDoc) {
        if (classDoc == null)
            return Optional.empty();

        return Stream.of(classDoc.fields(false))
                .filter(f -> hasAnnotation(parameter, f.annotations()))
                .findAny();
    }

    static Optional<ParamTag> findRequestBodyDoc(final MethodDoc methodDoc) {
        final Optional<String> paramName = Stream.of(methodDoc.parameters())
                .filter(p -> isRequestBody(p.annotations()))
                .map(Parameter::name)
                .findAny();

        return Stream.of(methodDoc.paramTags())
                .filter(t -> t.parameterName().equals(paramName.get()))
                .findAny();
    }

    private static boolean hasAnnotation(final MethodParameter parameter, final AnnotationDesc... annotations) {
        return Stream.of(annotations)
                .filter(a -> annotationTypeMatches(a.annotationType().qualifiedTypeName(), parameter.getParameterType()))
                .anyMatch(a -> annotationValueMatches(a.elementValues(), parameter.getName()));
    }

    private static boolean isRequestBody(final AnnotationDesc... annotations) {
        return Stream.of(annotations)
                .map(AnnotationDesc::annotationType)
                .map(AnnotationTypeDoc::qualifiedTypeName)
                .noneMatch(t -> Stream.of(KNOWN_ANNOTATIONS).anyMatch(a -> t.equals(toReadableType(a))));
    }

    private static boolean annotationTypeMatches(final String qualifiedTypeName, final ParameterType parameterType) {
        return qualifiedTypeName.equals(getJavaType(parameterType));
    }

    private static boolean annotationValueMatches(final AnnotationDesc.ElementValuePair[] elementValuePairs, final String name) {
        return Stream.of(elementValuePairs)
                .anyMatch(p -> "value".equals(p.element().name()) && name.equals(p.value().value()));
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
