package com.sebastian_daschner.jaxrs_analyzer.analysis.results;

import com.sebastian_daschner.jaxrs_analyzer.analysis.results.testclasses.typeanalyzer.TestClass31;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeIdentifier;
import org.objectweb.asm.Type;

import java.util.HashMap;
import java.util.Map;

public class Type31Analyzer implements JavaClassAnalyzer {
	@Override
	public JavaClassAnalysis analyze(String type, Class<?> clazz) {
		if (clazz.equals(TestClass31.class)) {
			Map<String, TypeIdentifier>  properties = new HashMap<>();
			properties.put("name", TypeIdentifier.ofType(Type.getDescriptor(String.class)));
			properties.put("anotherThing", TypeIdentifier.ofType(Type.getDescriptor(String.class)));
			return new JavaClassAnalysis(properties);
		}
		return null;
	}
}
