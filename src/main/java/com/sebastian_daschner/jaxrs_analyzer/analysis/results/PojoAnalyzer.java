package com.sebastian_daschner.jaxrs_analyzer.analysis.results;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeIdentifier;
import com.sebastian_daschner.jaxrs_analyzer.utils.Pair;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.sebastian_daschner.jaxrs_analyzer.model.JavaUtils.*;

/**
 * Basic java pojo analyzer.  If you want to modify the behavior of the basic pojo
 * analysis, inherit from this type and override {@link #isRelevant(Field, XmlAccessType)}
 * or {@link #isRelevant(Field, XmlAccessType)}
 */
public class PojoAnalyzer implements JavaClassAnalyzer {
	private static final String[] NAMES_TO_IGNORE = {"getClass"};
	private static final Set<String> ignoredFieldNames = new HashSet<>();

	/**
	 * Checks if the method is public and non-static and that the method is a Getter.
	 * Does not allow methods with ignored names.
	 * Does also not take methods annotated with {@link XmlTransient}.
	 *
	 * @param method The method
	 * @param accessType The xml access type annotation
	 * @return {@code true} if the method should be analyzed further
	 */
	protected boolean isRelevant(final Method method, final XmlAccessType accessType) {
		if (method.isSynthetic() || !isGetter(method))
			return false;

		final boolean propertyIgnored = ignoredFieldNames.contains(extractPropertyName(method.getName()));
		if (propertyIgnored || hasIgnoreAnnotation(method) || isTypeIgnored(method.getReturnType())) {
			return false;
		}

		if (isAnnotationPresent(method, XmlElement.class))
			return true;

		if (accessType == XmlAccessType.PROPERTY)
			return !isAnnotationPresent(method, XmlTransient.class);
		else if (accessType == XmlAccessType.PUBLIC_MEMBER)
			return Modifier.isPublic(method.getModifiers()) && !isAnnotationPresent(method, XmlTransient.class);

		return false;
	}

	/*
	 * Checks if the method is non-static.
	 * Does not allow fields with ignored names.
	 * Does also not take fields annotated with {@link XmlTransient}.
	 *
	 * @param method The method
	 * @param accessType The xml access type annotation
	 * @return {@code true} if the field should be analyzed further
	 */
	protected boolean isRelevant(final Field field, final XmlAccessType accessType) {
		if (field.isSynthetic())
			return false;

		if (hasIgnoreAnnotation(field) || isTypeIgnored(field.getType())) {
			ignoredFieldNames.add(field.getName());
			return false;
		}

		if (isAnnotationPresent(field, XmlElement.class))
			return true;

		final int modifiers = field.getModifiers();
		if (accessType == XmlAccessType.FIELD)
			// always take, unless static or transient
			return !Modifier.isTransient(modifiers) && !Modifier.isStatic(modifiers) && !isAnnotationPresent(field, XmlTransient.class);
		else if (accessType == XmlAccessType.PUBLIC_MEMBER)
			// only for public, non-static
			return Modifier.isPublic(modifiers) && !Modifier.isStatic(modifiers) && !isAnnotationPresent(field, XmlTransient.class);

		return false;
	}

	/**
	 * Uses reflection to determine the properties of the given type
	 *
	 * @param type The type descriptor
	 * @param clazz The class
	 * @return the analysis result
	 */
	@Override
	public JavaClassAnalysis analyze(String type, Class<?> clazz) {
		// TODO analyze & test annotation inheritance
		final XmlAccessType value = getXmlAccessType(clazz);

		ignoredFieldNames.clear();
		final List<Field> relevantFields = Stream.of(clazz.getDeclaredFields())
				.filter(f -> isRelevant(f, value))
				.collect(Collectors.toList());
		final List<Method> relevantGetters = Stream.of(clazz.getDeclaredMethods())
				.filter(m -> isRelevant(m, value))
				.collect(Collectors.toList());

		Map<String, TypeIdentifier> properties = Stream.concat(
				relevantFields.stream().map(f -> mapField(f, type)),
				relevantGetters.stream().map(g -> mapGetter(g, type)))
				.filter(Objects::nonNull)
				.collect(Collectors.toMap(Pair::getLeft, p -> TypeIdentifier.ofType(p.getRight())));

		return new JavaClassAnalysis(properties);
	}


	private static Pair<String, String> mapField(final Field field, final String containedType) {
		final String type = getFieldDescriptor(field, containedType);
		if (type == null)
			return null;

		return Pair.of(field.getName(), type);
	}

	private static Pair<String, String> mapGetter(final Method method, final String containedType) {
		final String returnType = getReturnType(getMethodSignature(method), containedType);
		if (returnType == null)
			return null;

		return Pair.of(extractPropertyName(method.getName()), returnType);
	}



	private static XmlAccessType getXmlAccessType(final Class<?> clazz) {
		Class<?> current = clazz;

		while (current != null) {
			if (isAnnotationPresent(current, XmlAccessorType.class))
				return getAnnotation(current, XmlAccessorType.class).value();
			current = current.getSuperclass();
		}

		return XmlAccessType.PUBLIC_MEMBER;
	}

	private static boolean isGetter(final Method method) {
		if (Modifier.isStatic(method.getModifiers()))
			return false;

		final String name = method.getName();
		if (Stream.of(NAMES_TO_IGNORE).anyMatch(n -> n.equals(name)))
			return false;

		if (name.startsWith("get") && name.length() > 3)
			return method.getReturnType() != void.class;

		return name.startsWith("is") && name.length() > 2 && method.getReturnType() == boolean.class;
	}

	private static <T extends AccessibleObject & Member> boolean hasIgnoreAnnotation(final T member) {
		return isAnnotationPresent(member, JsonIgnore.class) || isTypeIgnored(member.getDeclaringClass());
	}

	private static boolean isTypeIgnored(final Class<?> declaringClass) {
		return isAnnotationPresent(declaringClass, JsonIgnoreType.class);
	}
}
