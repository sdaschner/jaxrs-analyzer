package com.sebastian_daschner.jaxrs_analyzer.analysis.results;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ServiceLoader;

public final class JavaClassAnalyzerRegistry {

	private static final List<JavaClassAnalyzer> analyzers;

	static {
		List<JavaClassAnalyzer> analyzerExtensions = new ArrayList<>();
		for (JavaClassAnalyzer javaClassAnalyzer : ServiceLoader.load(JavaClassAnalyzer.class)) {
			analyzerExtensions.add(javaClassAnalyzer);
		}
		analyzerExtensions.removeIf(c -> c.getClass().equals(PojoAnalyzer.class));
		analyzerExtensions.add(new PojoAnalyzer());
		analyzers = Collections.unmodifiableList(analyzerExtensions);
	}

	public static List<JavaClassAnalyzer> getAnalyzers() {
		return analyzers;
	}

	private JavaClassAnalyzerRegistry() {}
}
