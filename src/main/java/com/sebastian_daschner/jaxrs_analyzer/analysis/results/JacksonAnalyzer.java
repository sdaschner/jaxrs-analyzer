package com.sebastian_daschner.jaxrs_analyzer.analysis.results;

import static com.sebastian_daschner.jaxrs_analyzer.model.JavaUtils.getFieldDescriptor;
import static com.sebastian_daschner.jaxrs_analyzer.model.JavaUtils.getMethodSignature;
import static com.sebastian_daschner.jaxrs_analyzer.model.JavaUtils.getReturnType;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeIdentifier;
import com.sebastian_daschner.jaxrs_analyzer.utils.Pair;

public class JacksonAnalyzer implements NormalizedTypeAnalyzer {

    private final JavaTypeAnalyzer javaTypeAnalyzer;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JacksonAnalyzer(JavaTypeAnalyzer javaTypeAnalyzer) {
        this.javaTypeAnalyzer = javaTypeAnalyzer;
    }

    @Override
    public Map<String, TypeIdentifier> analyzeClass(String type, Class<?> clazz) {
        if (clazz == null || JavaTypeAnalyzer.isJDKType(type))
            return Collections.emptyMap();
        
        JavaType jacksonType = objectMapper.getTypeFactory().constructType(clazz);
        BeanDescription introspection = objectMapper.getSerializationConfig().introspect(jacksonType);
        List<BeanPropertyDefinition> jacksonProperties = introspection.findProperties();
        final Map<String, TypeIdentifier> properties = new HashMap<>();
        jacksonProperties
        .stream()
        .filter(bp->bp.couldSerialize())
        .map(bp->mapProperty(bp, type))
        .filter(Objects::nonNull)
        .forEach(p->{
            properties.put(p.getLeft(), TypeIdentifier.ofType(p.getRight()));
            javaTypeAnalyzer.analyze(p.getRight());
        });
        ;
        return properties;
    }

    private Pair<String, String> mapProperty(BeanPropertyDefinition property, final String containedType) {
        AnnotatedMember primaryMember  = property.getPrimaryMember();
        if (primaryMember instanceof AnnotatedField) {
            AnnotatedField annotatedField = (AnnotatedField)primaryMember;
            return mapField(property.getName(),annotatedField.getAnnotated(),containedType);
        } else if (primaryMember instanceof AnnotatedMethod) {
            AnnotatedMethod annotatedMethod = (AnnotatedMethod)primaryMember;
            return mapGetter(property.getName(),annotatedMethod.getAnnotated(),containedType);
        }
        return null;
    }
    
    private static Pair<String, String> mapField(final String jsonProperty, final Field field, final String containedType) {
        final String type = getFieldDescriptor(field, containedType);
        if (type == null)
            return null;

        return Pair.of(jsonProperty, type);
    }

    private static Pair<String, String> mapGetter(final String jsonProperty,final Method method, final String containedType) {
        final String returnType = getReturnType(getMethodSignature(method), containedType);
        if (returnType == null)
            return null;
        return Pair.of(jsonProperty, returnType);
    }


}
