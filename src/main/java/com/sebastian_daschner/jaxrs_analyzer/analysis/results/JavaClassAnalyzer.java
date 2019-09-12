package com.sebastian_daschner.jaxrs_analyzer.analysis.results;

import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeIdentifier;

import java.util.HashMap;
import java.util.Map;

/**
 * An extensible thing used to determine the properties of a java
 * class.  IF your thing is responsible for a given type, return
 * a result.  IF your thing is not responsible for a type, return
 * null.
 *
 * To add an analyzer, create a service entry in your META-INF/services
 * per {@link java.util.ServiceLoader} rules.
 *
 * @see PojoAnalyzer
 */
public interface JavaClassAnalyzer {

	/**
	 * Used to represent the result of analysis on a java class.
	 * Analyzers may populate fields and getters or may swap the
	 * type with a different type completely.
	 */
	final class JavaClassAnalysis {
		public final Class<?> replacement;
		public final Map<String, TypeIdentifier> properties = new HashMap<>();

		/**
		 * Swap the type with a replacement type
		 * @param replacement The replacement type
		 */
		public JavaClassAnalysis(Class<?> replacement) {
			this.replacement = replacement;
		}

		/**
		 * Use these fields for this type
		 * @param properties The fields to use for this type
		 */
		public JavaClassAnalysis(Map<String, TypeIdentifier> properties) {
			this.properties.putAll(properties);
			this.replacement = null;
		}
	}

	/**
	 * Analyze the java type and return a result or null.
	 *
	 * @param type The type descriptor
	 * @param clazz The class
	 * @return A result or null if you aren't responsible for this type
	 */
	JavaClassAnalysis analyze(final String type, final Class<?> clazz);
}
