package com.sebastian_daschner.jaxrs_test;

import com.sebastian_daschner.jaxrs_analyzer.analysis.results.JavaClassAnalyzer;
import com.sebastian_daschner.jaxrs_analyzer.model.JavaUtils;
import com.sebastian_daschner.jaxrs_analyzer.model.Types;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeIdentifier;

import java.util.HashMap;
import java.util.Map;

public class SpecialClassAnalyzer implements JavaClassAnalyzer {
	@Override
	public JavaClassAnalysis analyze(String type, Class<?> clazz) {
		if (StringableValue.class.equals(clazz)) {
			return new JavaClassAnalysis(String.class);
		} else if (SpecialWrappingResult.class.equals(clazz)) {
			String typeParameter = JavaUtils.getTypeParameters(type).get(0);
			String replacement = JavaUtils.toTypeWithParameters(JavaUtils.toClassName(Types.COLLECTION),
					JavaUtils.toClassName(typeParameter));
			return new JavaClassAnalysis(replacement);

		} else if (GeneratedThing.class.equals(clazz)) {
			Map<String, TypeIdentifier> properties = new HashMap<>();
			properties.put("id", TypeIdentifier.ofType(StringableValue.class));
			properties.put("name", TypeIdentifier.ofType(String.class));
			return new JavaClassAnalysis(properties);
		}
		return null;
	}
}
