package com.sebastian_daschner.jaxrs_analyzer.analysis.results;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ServiceLoader;

/**
 * Singleton registry for plug-able things.  Current extension points are:
 *
 * <ul>
 *     <li>{@link JavaClassAnalyzer}</li>
 * </ul>
 *
 */
public final class PluginRegistry {

	private static volatile PluginRegistry INSTANCE;

	public static PluginRegistry getInstance() {
		PluginRegistry r;
		if ((r = INSTANCE) == null) {
			synchronized (PluginRegistry.class) {
				if ((r = INSTANCE) == null) {
					r = INSTANCE = new PluginRegistry();
				}
			}
		}
		return r;
	}

	private final List<JavaClassAnalyzer> classAnalyzers;

	private PluginRegistry() {
		List<JavaClassAnalyzer> analyzerExtensions = new ArrayList<>();
		for (JavaClassAnalyzer javaClassAnalyzer : ServiceLoader.load(JavaClassAnalyzer.class)) {
			analyzerExtensions.add(javaClassAnalyzer);
		}
		analyzerExtensions.removeIf(c -> c.getClass().equals(PojoAnalyzer.class));
		analyzerExtensions.add(new PojoAnalyzer());
		classAnalyzers = Collections.unmodifiableList(analyzerExtensions);
	}

	public List<JavaClassAnalyzer> getJavaClassAnalyzers() {
		return classAnalyzers;
	}
}
