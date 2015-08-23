package com.sebastian_daschner.jaxrs_analyzer.model.types;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for extracting {@link Type}s from the Java descriptors.
 *
 * @author Sebastian Daschner
 */
abstract class TypeExtractor {

    static CtClass toErasuredClass(final String type) throws BadBytecode, NotFoundException {
//        if (type.chars().allMatch(Character::isLowerCase))
//            return convertPrimitive(type);

        final String className;
        final int index = type.indexOf('<');
        if (index >= 0) {
            final int lastIndex = type.lastIndexOf('>');

            if (lastIndex != type.length() - 1)
                // Type<Type>[] -> Type[]
                className = type.substring(0, index) + type.substring(type.lastIndexOf('>') + 1, type.length());
            else
                // Type<Type> -> Type
                className = type.substring(0, index);

        } else
            className = type;

        return ClassPool.getDefault().get(className);
    }

    static List<String> toParameterNames(final String genericSignature) {
        // <E:Ljava/lang/Object;>Ljava/lang/Object;Ljava/util/Collection<TE;>;
        if ((genericSignature == null) || !genericSignature.startsWith("<")) {
            return Collections.emptyList();
        }
        String genericParameters = genericSignature.substring(1,genericSignature.indexOf(">"));
        String[] split = genericParameters.split(";");
        return Arrays.stream(split).map(s -> s.substring(0, s.indexOf(':'))).collect(Collectors.toList());
    }

    static List<Type> toTypeParameters(final String descriptor) {
        final int index = descriptor.indexOf('<');
        if (index < 0)
            return Collections.emptyList();

        final String withoutPrefix = descriptor.substring(index + 1);
        // be aware of [] at the end -> last > is not necessarily at end
        final int lastIndex = withoutPrefix.lastIndexOf('>');
        final String nestedTypes = withoutPrefix.substring(0, lastIndex);

        return getTypesFirstLevel(nestedTypes).stream().map(Type::new).collect(Collectors.toList());
    }

    private static List<String> getTypesFirstLevel(final String type) {
        final List<String> types = new LinkedList<>();

        int level = 0;
        int lastCut = 0;
        for (int i = 0; i < type.length(); i++) {
            final char character = type.charAt(i);
            if (character == '<')
                level++;
            else if (character == '>')
                level--;
            else if (character == ',' && level == 0) {
                types.add(type.substring(lastCut, i).trim());
                lastCut = i + 1;
            }
        }

        types.add(type.substring(lastCut).trim());

        return types;
    }

    private static CtClass convertPrimitive(final String type) throws NotFoundException {
        switch (type) {
            case "boolean":
                return Types.PRIMITIVE_BOOLEAN.getCtClass();
            case "char":
                return Types.PRIMITIVE_CHAR.getCtClass();
            case "byte":
                return Types.PRIMITIVE_BYTE.getCtClass();
            case "short":
                return Types.PRIMITIVE_SHORT.getCtClass();
            case "int":
                return Types.PRIMITIVE_INT.getCtClass();
            case "long":
                return Types.PRIMITIVE_LONG.getCtClass();
            case "float":
                return Types.PRIMITIVE_FLOAT.getCtClass();
            case "double":
                return Types.PRIMITIVE_DOUBLE.getCtClass();
            case "void":
                return Types.PRIMITIVE_VOID.getCtClass();
            default:
                throw new RuntimeException("Unknown primitive: " + type);
        }
    }


}
