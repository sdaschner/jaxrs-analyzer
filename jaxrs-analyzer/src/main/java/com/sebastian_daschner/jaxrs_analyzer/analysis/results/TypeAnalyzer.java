package com.sebastian_daschner.jaxrs_analyzer.analysis.results;

import com.sebastian_daschner.jaxrs_analyzer.LogProvider;
import com.sebastian_daschner.jaxrs_analyzer.analysis.utils.JavaUtils;
import com.sebastian_daschner.jaxrs_analyzer.analysis.utils.Pair;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeRepresentation;
import javassist.*;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.SignatureAttribute;

import javax.json.*;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlTransient;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Analyzes a class (usually a POJO) for it's properties and methods.
 * The analysis is used to derive the JSON/XML representations. This class is thread-safe.
 *
 * @author Sebastian Daschner
 */
class TypeAnalyzer {

    private final static String[] NAMES_TO_IGNORE = {"getClass"};
    private static final JsonString EMPTY_JSON_STRING = new JsonString() {
        private static final String TYPE = "string";

        @Override
        public ValueType getValueType() {
            return ValueType.STRING;
        }

        @Override
        public String getString() {
            return TYPE;
        }

        @Override
        public CharSequence getChars() {
            return TYPE;
        }
    };

    private final Lock lock = new ReentrantLock();
    private String type;
    private boolean collection;

    /**
     * Analyzes the given type. Resolves known generics and creates a representation of the contained class.
     *
     * @param type The type to analyze
     * @return The type representation of the class (currently just for application/json)
     */
    TypeRepresentation analyze(final String type) {
        lock.lock();
        try {
            collection = ResponseTypeNormalizer.isCollection(type);
            this.type = ResponseTypeNormalizer.normalizeWrapper(type);

            if (!isRelevant())
                return new TypeRepresentation(this.type);

            // TODO analyze XML as well
            final TypeRepresentation representation = new TypeRepresentation(ResponseTypeNormalizer.normalize(this.type));
            representation.getRepresentations().put(MediaType.APPLICATION_JSON, analyzeInternal(this.type));
            return representation;
        } finally {
            lock.unlock();
        }
    }

    private boolean isRelevant() {
        // exclude java, javax, etc. packages
        return collection || !type.startsWith("java");
    }

    private static JsonValue analyzeInternal(final String type) {
        if (ResponseTypeNormalizer.isCollection(type)) {
            final JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
            addToArray(arrayBuilder, ResponseTypeNormalizer.normalizeCollection(type));
            return arrayBuilder.build();
        }

        final CtClass ctClass;
        try {
            ctClass = ClassPool.getDefault().get(type);
        } catch (NotFoundException e) {
            return Json.createObjectBuilder().build();
        }

        if (ctClass.isEnum())
            return EMPTY_JSON_STRING;

        // TODO analyze other XMLAccessorTypes -> assuming PUBLIC_MEMBER
        // analyze superclasses for JAXB annotations

        final List<CtField> publicFields = Stream.of(ctClass.getFields()).filter(TypeAnalyzer::isRelevant).collect(Collectors.toList());
        final List<CtMethod> publicGetters = Stream.of(ctClass.getMethods()).filter(TypeAnalyzer::isRelevant).collect(Collectors.toList());

        final JsonObjectBuilder builder = Json.createObjectBuilder();

        publicFields.stream().map(TypeAnalyzer::mapField).filter(Objects::nonNull).forEach(p -> addToObject(builder, p.getLeft(), p.getRight()));
        publicGetters.stream().map(TypeAnalyzer::mapGetter).filter(Objects::nonNull).forEach(p -> addToObject(builder, p.getLeft(), p.getRight()));

        return builder.build();
    }

    private static boolean isRelevant(final CtField field) {
        return Modifier.isPublic(field.getModifiers()) && !Modifier.isStatic(field.getModifiers()) && !field.hasAnnotation(XmlTransient.class);
    }

    /**
     * Checks if the method is public and non-static and that the method is a Getter. Does not allow methods with ignored names.
     * Does also not take methods annotated with {@link XmlTransient}
     *
     * @param method The method
     * @return {@code true} if the method should be analyzed further
     */
    private static boolean isRelevant(final CtMethod method) {
        if (!Modifier.isPublic(method.getModifiers()) || Modifier.isStatic(method.getModifiers()) || method.hasAnnotation(XmlTransient.class))
            return false;

        final String name = method.getName();
        return name.startsWith("get") && name.length() > 3 && Stream.of(NAMES_TO_IGNORE).noneMatch(n -> n.equals(name));
    }

    private static Pair<String, String> mapField(final CtField field) {
        try {
            final String sig = field.getGenericSignature() != null ? field.getGenericSignature() : field.getSignature();
            final String fieldType = JavaUtils.getType(SignatureAttribute.toFieldSignature(sig));
            return Pair.of(field.getName(), fieldType);
        } catch (BadBytecode e) {
            LogProvider.getLogger().accept("Could not analyze field: " + field);
            return null;
        }
    }

    private static Pair<String, String> mapGetter(final CtMethod method) {
        try {
            final String sig = method.getGenericSignature() != null ? method.getGenericSignature() : method.getSignature();
            final String returnType = JavaUtils.getType(SignatureAttribute.toMethodSignature(sig).getReturnType());
            return Pair.of(normalizeGetter(method.getName()), returnType);
        } catch (BadBytecode e) {
            LogProvider.getLogger().accept("Could not analyze method: " + method);
            return null;
        }
    }

    /**
     * Converts a getter name to the property name (without the "get" and lowercase).
     *
     * @param name The name of the method (MUST match "get[A-Z]+"
     * @return The name of the property
     */
    private static String normalizeGetter(final String name) {
        final char chars[] = name.substring(3).toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }

    private static void addToObject(final JsonObjectBuilder builder, final String key, final String type) {
        switch (type) {
            case "java.lang.String":
                builder.add(key, "string");
                break;
            case "java.util.Date":
            case "java.time.LocalDate":
                builder.add(key, "date");
                break;
            case "java.lang.Integer":
            case "int":
            case "java.lang.Long":
            case "long":
            case "java.math.BigInteger":
                builder.add(key, 0);
                break;
            case "java.lang.Double":
            case "double":
            case "java.math.BigDecimal":
                builder.add(key, 0.0);
                break;
            case "java.lang.Boolean":
            case "boolean":
                builder.add(key, false);
                break;
            default:
                builder.add(key, analyzeInternal(type));
        }
    }

    private static void addToArray(final JsonArrayBuilder builder, final String type) {
        switch (type) {
            case "java.lang.String":
                builder.add("string");
                break;
            case "java.lang.Integer":
            case "int":
            case "java.lang.Long":
            case "long":
            case "java.math.BigInteger":
                builder.add(0);
                break;
            case "java.lang.Double":
            case "double":
            case "java.math.BigDecimal":
                builder.add(0.0);
                break;
            case "java.lang.Boolean":
            case "boolean":
                builder.add(false);
                break;
            default:
                builder.add(analyzeInternal(type));
        }
    }

}
