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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Type(final CtClass ctClass) {
        try {
            this.ctClass = TypeExtractor.toErasuredClass(ctClass.getName());
            typeParameters = Collections.emptyList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Type(final SignatureAttribute.Type sigType) {
        this(sigType, null, null, null);
    }

    /**
     * Constructs a new type for the given Javassist signature type and the generic class signature of the containing classes.
     * The later is used for resolving generic type arguments (e.g. {@code public void A getSomething()} declared in {@code public class AClass&lt;A&gt;})
     * or generic methods (e.g. {@code public &lt;T&gt; T getSomething(Class&lt;T&gt; clazz, ...)}).
     *
     * @param sigType                The signature type
     * @param genericClassSignature  The generic class signature of the containing class, needed for generic classes (can be {@code null}).
     * @param containingType         The type with the actual type arguments, needed for resolving generic classes with actual arguments
     * @param genericMethodSignature The generic signature of the method, needed for generic methods (can be {@code null}).
     */
    public Type(final SignatureAttribute.Type sigType, final SignatureAttribute.ClassSignature genericClassSignature, final Type containingType,
                final SignatureAttribute.MethodSignature genericMethodSignature) {
        final String type = getType(sigType, genericClassSignature, containingType, genericMethodSignature);
        try {
            ctClass = TypeExtractor.toErasuredClass(type);
            typeParameters = TypeExtractor.toTypeParameters(type);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the Java type representation (including {@code $} as inner class separator) of the given {@link SignatureAttribute.Type}.
     * Resolves potential generic type arguments into the type parameters (e.g. {@code A} as type defined in {@code &lt;A:Ljava/lang/String;&gt;}).
     *
     * @param type                   The Javassist type
     * @param genericClassSignature  The generic class signature of the containing class (can be {@code null}).
     * @param containingType         The type with the actual type arguments
     * @param genericMethodSignature The generic signature of the method, needed for generic methods (can be {@code null}).
     * @return The Java type representation
     */
    // FIXME refactor
    private static String getType(final SignatureAttribute.Type type, final SignatureAttribute.ClassSignature genericClassSignature, final Type containingType,
                                  final SignatureAttribute.MethodSignature genericMethodSignature) {
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

                // TODO test nested class type which has type parameters
                declaringClasses.forEach(c -> builder.append(c.getName()).append('$'));
                builder.append(nestedClassType.getName());
            } else {
                builder.append(((SignatureAttribute.ClassType) type).getName());
            }

            final SignatureAttribute.ClassType classType = (SignatureAttribute.ClassType) type;
            if (classType.getTypeArguments() != null) {
                builder.append('<');
//                Stream.of(classType.getTypeArguments()).map(JavaUtils::getTypeArgument).collect(Collectors.joining(","));
                for (int i = 0; i < classType.getTypeArguments().length; i++) {
                    builder.append(getTypeArgument(classType.getTypeArguments()[i], genericClassSignature, containingType, genericMethodSignature));
                    if (i < classType.getTypeArguments().length - 1)
                        builder.append(',');
                }
                builder.append('>');
            }
            return builder.toString();
        } else if (type instanceof SignatureAttribute.ArrayType) {
            final SignatureAttribute.ArrayType arrayType = (SignatureAttribute.ArrayType) type;
            final StringBuilder builder = new StringBuilder(getType(arrayType.getComponentType(), genericClassSignature, containingType, genericMethodSignature));
            for (int i = 0; i < arrayType.getDimension(); i++)
                builder.append("[]");
            return builder.toString();
        } else if (type instanceof SignatureAttribute.TypeVariable) {
            final SignatureAttribute.TypeVariable typeVariable = (SignatureAttribute.TypeVariable) type;

            // type variable in generic method signature
            if (genericMethodSignature != null) {
                int index = getTypeVariableIndex(genericMethodSignature.getTypeParameters(), typeVariable.getName());
                // TODO resolve type bounds
                if (index < genericMethodSignature.getTypeParameters().length)
                    return Types.OBJECT.toString();
            }

            // type variable in generic class signature
            if (genericClassSignature != null) {
                int index = getTypeVariableIndex(genericClassSignature.getParameters(), typeVariable.getName());
                if (index < genericClassSignature.getParameters().length)
                    return containingType != null && index < containingType.getTypeParameters().size() ? containingType.getTypeParameters().get(index).toString() :
                            Types.OBJECT.toString();
            }

        }
        return type.toString();
    }

    private static int getTypeVariableIndex(final SignatureAttribute.TypeParameter[] typeParameters, final String typeVariableName) {
        int index = 0;
        for (; index < typeParameters.length; index++) {
            if (typeParameters[index].getName().equals(typeVariableName))
                break;
        }
        return index;
    }

    private static String getTypeArgument(final SignatureAttribute.TypeArgument typeArgument, final SignatureAttribute.ClassSignature genericClassSignature,
                                          final Type containingType, final SignatureAttribute.MethodSignature genericMethodSignature) {
        if (typeArgument.getKind() == '*')
            return Types.OBJECT.toString();
        return getType(typeArgument.getType(), genericClassSignature, containingType, genericMethodSignature);
    }


    public CtClass getCtClass() {
        return ctClass;
    }

    public List<Type> getTypeParameters() {
        return typeParameters;
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
