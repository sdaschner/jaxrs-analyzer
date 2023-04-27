package com.sebastian_daschner.jaxrs_analyzer.analysis.results;

import com.sebastian_daschner.jaxrs_analyzer.analysis.results.testclasses.typeanalyzer.TestClass32;

public class Type32Analyzer implements JavaClassAnalyzer {
	@Override
	public JavaClassAnalysis analyze(String type, Class<?> clazz) {
		if (clazz.equals(TestClass32.class)) {
			return new JavaClassAnalysis(String.class);
		}
		return null;
	}
}
