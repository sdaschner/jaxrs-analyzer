package com.sebastian_daschner.jaxrs_analyzer.model.types;

import com.sebastian_daschner.jaxrs_analyzer.LogProvider;

import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.SignatureAttribute;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents a Java type with information about Generics, superclasses.
 *
 * @author Sebastian Daschner
 */
public class Type {

    private final CtClass ctClass;
    private final List<Type> typeParameters;
    private final List<String> parametersNames;

    /**
     * Constructs a type with the given Java type name.
     *
     * @param type The Java type name
     * @throws RuntimeException If loading the classes or deriving the type failed
     */
    public Type(final String type) {
        try {
            ctClass = TypeExtractor.toErasuredClass(type);
            typeParameters = TypeExtractor.toTypeParameters(type);
            parametersNames = TypeExtractor.toParameterNames(ctClass.getGenericSignature());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Type(final CtClass ctClass) {
        try {
            this.ctClass = TypeExtractor.toErasuredClass(ctClass.getName());
            typeParameters = Collections.emptyList();
            parametersNames = Collections.emptyList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public Type(final SignatureAttribute.Type sigType) {
        final String type = getType(sigType, null);
        try {
            ctClass = TypeExtractor.toErasuredClass(type);
            typeParameters = TypeExtractor.toTypeParameters(type);
            parametersNames = TypeExtractor.toParameterNames(ctClass.getGenericSignature());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Type(final SignatureAttribute.Type sigType, Type parent) {
        final String type = getType(sigType, parent);
        try {
            ctClass = TypeExtractor.toErasuredClass(type);
            typeParameters = TypeExtractor.toTypeParameters(type);
            parametersNames = TypeExtractor.toParameterNames(ctClass.getGenericSignature());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the Java type representation (including {@code $} as inner class separator) of the given {@link SignatureAttribute.Type}.
     *
     * @param type The Javassist type
     * @return The Java type representation
     */
    // FIXME refactor
    private static String getType(final SignatureAttribute.Type type, final Type parent) {
        if (type instanceof SignatureAttribute.ClassType) {
            final StringBuilder builder = new StringBuilder();
            if (type instanceof SignatureAttribute.NestedClassType) {
                final SignatureAttribute.NestedClassType nestedClassType = (SignatureAttribute.NestedClassType) type;

                // declaring class can be nested as well
                final List<SignatureAttribute.ClassType> declaringClasses = new LinkedList<>();
                SignatureAttribute.ClassType declaringClass = nestedClassType.getDeclaringClass();
                while (declaringClass != null) {
                    declaringClasses.add(declaringClass);
                    declaringClass = declaringClass.getDeclaringClass();
                }
                Collections.reverse(declaringClasses);

                declaringClasses.forEach(c -> builder.append(c.getName()).append('$'));
                builder.append(nestedClassType.getName());
            } else
                builder.append(((SignatureAttribute.ClassType) type).getName());

            final SignatureAttribute.ClassType classType = (SignatureAttribute.ClassType) type;
            if (classType.getTypeArguments() != null) {
                builder.append('<');
//                Stream.of(classType.getTypeArguments()).map(JavaUtils::getTypeArgument).collect(Collectors.joining(","));
                for (int i = 0; i < classType.getTypeArguments().length; i++) {
                    builder.append(getTypeArgument(classType.getTypeArguments()[i], parent));
                    if (i < classType.getTypeArguments().length - 1)
                        builder.append(',');
                }
                builder.append('>');
            }
            return builder.toString();
        } else if (type instanceof SignatureAttribute.ArrayType) {
            final SignatureAttribute.ArrayType arrayType = (SignatureAttribute.ArrayType) type;
            final StringBuilder builder = new StringBuilder(getType(arrayType.getComponentType(), parent));
            for (int i = 0; i < arrayType.getDimension(); i++)
                builder.append("[]");
            return builder.toString();
        }

        String typeName = type.toString();
        if (parent != null) {
            int paramIndex = parent.getParametersNames().indexOf(typeName);
            if (paramIndex > -1) {
                typeName = parent.getTypeParameters().get(paramIndex).toString();
            }
        }
        return typeName;
    }

    private static String getTypeArgument(final SignatureAttribute.TypeArgument typeArgument, final Type parent) {
        if (typeArgument.getKind() == '*')
            return Types.OBJECT.toString();
        return getType(typeArgument.getType(), parent);
    }


    public CtClass getCtClass() {
        return ctClass;
    }

    public List<Type> getTypeParameters() {
        return typeParameters;
    }

    public List<String> getParametersNames() {
        return parametersNames;
    }

    /**
     * Checks if this type could be assigned to the given type (i.e. the given type is a superclass or interface of this type).
     *
     * @param type The type to check
     * @return {@code true} if this type could be assigned to {@code type}
     */
    public boolean isAssignableTo(final Type type) {
        if (this.equals(type))
            return true;

        if (this.ctClass.equals(type.ctClass) && (typeParameters.isEmpty() || type.typeParameters.isEmpty()))
            return true;

        try {
            final CtClass superclass = ctClass.getSuperclass();
            if (superclass != null && !Types.OBJECT.ctClass.equals(superclass) && new Type(superclass).isAssignableTo(type)) {
                return true;
            }

            return Stream.of(ctClass.getInterfaces()).anyMatch(i -> new Type(i).isAssignableTo(type));
        } catch (NotFoundException e) {
            LogProvider.error("Could not analyze superclass of: " + ctClass.getName() + ", reason: " + e.getMessage());
            LogProvider.debug(e);
            return false;
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Type type = (Type) o;

        if (!ctClass.equals(type.ctClass)) return false;
        return typeParameters.equals(type.typeParameters);
    }

    @Override
    public int hashCode() {
        int result = ctClass.hashCode();
        result = 31 * result + typeParameters.hashCode();
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder(ctClass.getName());
        final int lastIndexOf = builder.indexOf("[]");
        CharSequence suffix = null;

        if (lastIndexOf >= 0) {
            suffix = builder.subSequence(lastIndexOf, builder.length());
            builder.delete(lastIndexOf, builder.length());
        }

        if (!typeParameters.isEmpty()) {
            builder.append('<');
            builder.append(typeParameters.stream().map(Object::toString).collect(Collectors.joining(", ")));
            builder.append('>');
        }

        if (suffix != null)
            builder.append(suffix);

        return builder.toString();
    }

}
